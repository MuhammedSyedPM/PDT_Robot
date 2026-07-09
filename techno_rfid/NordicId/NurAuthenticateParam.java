// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurAuthenticateParam
{
    public int csi;
    public int rxLength;
    public boolean rxAttn;
    public boolean reSelect;
    public int timeout;
    public int preTxWait;
    public int msgBitLength;
    private byte[] mMessage;
    
    public void setMessage(final int msgBitLen, final byte[] message) throws NurApiException {
        if (message == null || message.length > 128) {
            throw new NurApiException("Authentication message is invalid.", 5);
        }
        final int copyLen = NurApi.bitLenToByteLen(msgBitLen);
        if (copyLen < 1 || copyLen > 128) {
            throw new NurApiException("Authentication: invalid bit length.", 5);
        }
        this.mMessage = new byte[128];
        this.msgBitLength = msgBitLen;
        System.arraycopy(message, 0, this.mMessage, 0, copyLen);
    }
    
    public byte[] getMessage() throws NurApiException {
        if (this.mMessage == null) {
            throw new NurApiException("Authentication: no message available.", 4100);
        }
        final int copyLen = NurApi.bitLenToByteLen(this.msgBitLength);
        if (this.msgBitLength < 1 || this.msgBitLength > 1024) {
            throw new NurApiException("Authentication: message bit length is invalid.", 5);
        }
        final byte[] ret = new byte[copyLen];
        System.arraycopy(this.mMessage, 0, ret, 0, copyLen);
        return ret;
    }
    
    public NurAuthenticateParam() {
        this.csi = 0;
        this.rxLength = 0;
        this.rxAttn = false;
        this.reSelect = false;
        this.timeout = 25;
        this.preTxWait = 0;
        this.msgBitLength = 0;
        this.mMessage = new byte[128];
    }
    
    public NurAuthenticateParam(final NurAuthenticateParam other) {
        this.csi = 0;
        this.rxLength = 0;
        this.rxAttn = false;
        this.reSelect = false;
        this.timeout = 25;
        this.preTxWait = 0;
        this.msgBitLength = 0;
        this.mMessage = new byte[128];
        this.csi = other.csi;
        this.rxLength = other.rxLength;
        this.rxAttn = other.rxAttn;
        this.reSelect = other.reSelect;
        this.timeout = other.timeout;
        this.preTxWait = other.preTxWait;
        this.msgBitLength = other.msgBitLength;
        try {
            this.setMessage(other.msgBitLength, other.getMessage());
        }
        catch (Exception e) {
            this.msgBitLength = 0;
            this.mMessage = null;
        }
    }
}
