﻿/*
 * Syscoin-JSON-RPC-Client License
 * 
 * Copyright (c) 2013, Mikhail Yevchenko.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the 
 * Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject
 * to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR
 * ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package wf.syscoin.javasyscoindrpcclient;

import java.util.Map;

/**
 * an object represents the error in a syscoind rpc call
 * 
 * @author frankchen
 * @create 2018年7月9日 下午8:58:13
 */
public class SyscoinRPCError {
    private int code;
    private String message;

    @SuppressWarnings({ "rawtypes" })
    public SyscoinRPCError(Map errorMap) {
        Number n = (Number) errorMap.get("code");
        this.code    = n != null ? n.intValue() : 0;
        this.message = (String) errorMap.get("message");
    }

    /**
     * get the code returned by the syscoind.<br/>
     * some of the error codes are defined in {@link SyscoinRPCErrorCode}
     */
    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
