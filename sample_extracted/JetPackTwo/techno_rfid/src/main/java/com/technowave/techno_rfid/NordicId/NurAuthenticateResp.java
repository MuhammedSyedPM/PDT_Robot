// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurAuthenticateResp
{
    public static final int RESPONSE_RECEIVED = 0;
    public static final int NO_RESPONSE = 1;
    public static final int TAG_ERROR = 2;
    public static final int ERROR_NOT_RECEIVED = -1;
    public int status;
    public int tagError;
    public int bitLength;
    public byte[] reply;
    
    public NurAuthenticateResp() {
        this.status = 1;
        this.tagError = -1;
        this.bitLength = 0;
        this.reply = null;
    }
}
