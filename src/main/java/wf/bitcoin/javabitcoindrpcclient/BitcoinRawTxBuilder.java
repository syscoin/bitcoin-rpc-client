/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wf.syscoin.javasyscoindrpcclient;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author azazar
 */
public class SyscoinRawTxBuilder {

  public final SyscoindRpcClient syscoin;

  public SyscoinRawTxBuilder(SyscoindRpcClient syscoin) {
    this.syscoin = syscoin;
  }
  public Set<SyscoindRpcClient.TxInput> inputs = new LinkedHashSet<>();
  public List<SyscoindRpcClient.TxOutput> outputs = new ArrayList<>();
  public List<String> privateKeys;

  @SuppressWarnings("serial")
  private class Input extends SyscoindRpcClient.BasicTxInput {

    public Input(String txid, Integer vout) {
      super(txid, vout);
    }

    public Input(SyscoindRpcClient.TxInput copy) {
      this(copy.txid(), copy.vout());
    }

    @Override
    public int hashCode() {
      return txid.hashCode() + vout;
    }

    @Override
    public boolean equals(Object obj) {
      if (obj == null)
        return false;
      if (!(obj instanceof SyscoindRpcClient.TxInput))
        return false;
      SyscoindRpcClient.TxInput other = (SyscoindRpcClient.TxInput) obj;
      return vout == other.vout() && txid.equals(other.txid());
    }

  }

  public SyscoinRawTxBuilder in(SyscoindRpcClient.TxInput in) {
    inputs.add(new Input(in.txid(), in.vout()));
    return this;
  }

  public SyscoinRawTxBuilder in(String txid, int vout) {
    in(new SyscoindRpcClient.BasicTxInput(txid, vout));
    return this;
  }

  public SyscoinRawTxBuilder out(String address, BigDecimal amount) {
    return out(address, amount, null);
  }

  public SyscoinRawTxBuilder out(String address, BigDecimal amount, byte[] data) {
    outputs.add(new SyscoindRpcClient.BasicTxOutput(address, amount, data));
    return this;
  }

  public SyscoinRawTxBuilder in(BigDecimal value) throws GenericRpcException {
    return in(value, 6);
  }

  public SyscoinRawTxBuilder in(BigDecimal value, int minConf) throws GenericRpcException {
    List<SyscoindRpcClient.Unspent> unspent = syscoin.listUnspent(minConf);
    BigDecimal v = value;
    for (SyscoindRpcClient.Unspent o : unspent) {
      if (!inputs.contains(new Input(o))) {
        in(o);
        v = v.subtract(o.amount());
      }
      if (v.compareTo(BigDecimal.ZERO) < 0)
        break;
    }
    if (BigDecimal.ZERO.compareTo(v) < 0)
      throw new GenericRpcException("Not enough syscoins (" + v + "/" + value + ")");
    return this;
  }

  private HashMap<String, SyscoindRpcClient.RawTransaction> txCache = new HashMap<>();

  private SyscoindRpcClient.RawTransaction tx(String txId) throws GenericRpcException {
    SyscoindRpcClient.RawTransaction tx = txCache.get(txId);
    if (tx != null)
      return tx;
    tx = syscoin.getRawTransaction(txId);
    txCache.put(txId, tx);
    return tx;
  }

  public SyscoinRawTxBuilder outChange(String address) throws GenericRpcException {
    return outChange(address, BigDecimal.ZERO);
  }

  public SyscoinRawTxBuilder outChange(String address, BigDecimal fee) throws GenericRpcException {
    BigDecimal is = BigDecimal.ZERO;
    for (SyscoindRpcClient.TxInput i : inputs)
      is = is.add(tx(i.txid()).vOut().get(i.vout()).value());
    BigDecimal os = fee;
    for (SyscoindRpcClient.TxOutput o : outputs)
      os = os.add(o.amount());
    if (os.compareTo(is) < 0)
      out(address, is.subtract(os));
    return this;
  }
  
  public SyscoinRawTxBuilder addPrivateKey(String privateKey)
  {
	  if ( privateKeys == null )
		  privateKeys = new ArrayList<String>();
	  privateKeys.add(privateKey);
	  return this;
  }

  public String create() throws GenericRpcException {
    return syscoin.createRawTransaction(new ArrayList<>(inputs), outputs);
  }

  public String sign() throws GenericRpcException {
    return syscoin.signRawTransaction(create(), null, privateKeys);
  }

  public String send() throws GenericRpcException {
    return syscoin.sendRawTransaction(sign());
  }

}
