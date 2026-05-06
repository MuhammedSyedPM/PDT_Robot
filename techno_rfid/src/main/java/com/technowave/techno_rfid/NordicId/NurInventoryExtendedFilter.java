// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurInventoryExtendedFilter
{
    public static final int SESSION_S0 = 0;
    public static final int SESSION_S1 = 1;
    public static final int SESSION_S2 = 2;
    public static final int SESSION_S3 = 3;
    public static final int SESSION_SL = 4;
    public static final int FILTER_ACTION_0 = 0;
    public static final int FILTER_ACTION_1 = 1;
    public static final int FILTER_ACTION_2 = 2;
    public static final int FILTER_ACTION_3 = 3;
    public static final int FILTER_ACTION_4 = 4;
    public static final int FILTER_ACTION_5 = 5;
    public static final int FILTER_ACTION_6 = 6;
    public static final int FILTER_ACTION_7 = 7;
    public static final int BANK_PASSWD = 0;
    public static final int BANK_EPC = 1;
    public static final int BANK_TID = 2;
    public static final int BANK_USER = 3;
    public boolean truncate;
    public int targetSession;
    public int action;
    public int bank;
    public int address;
    public int maskBitLength;
    public byte[] maskdata;
    
    public int getFilterByteSize() throws NurApiException {
        final int size = 9;
        int maskByteLength = 0;
        if (this.maskBitLength > 0) {
            maskByteLength = NurApi.bitLenToByteLen(this.maskBitLength);
            if (this.maskBitLength > 255 || this.maskdata == null || this.maskdata.length < maskByteLength) {
                throw new NurApiException("getFilterByteSize", 5);
            }
        }
        return size + maskByteLength;
    }
}
