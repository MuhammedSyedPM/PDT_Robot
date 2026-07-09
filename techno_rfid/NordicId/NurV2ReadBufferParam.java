// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurV2ReadBufferParam
{
    public static final int MIN_BITS = 1;
    public static final int MAX_BITS = 1000;
    public static final int MIN_ADDR = 0;
    public static final int MAX_ADDR = 4095;
    public int bitAddress;
    public int bitCount;
    public int timeout;
    
    public NurV2ReadBufferParam(final int addr, final int count) throws NurApiException {
        this.bitAddress = 0;
        this.bitCount = 0;
        this.timeout = 0;
        if (addr < 0 || addr > 4095 || count < 1 || count > 1000) {
            throw new NurApiException("V2ReadBuffer: invalid parameter(s).", 5);
        }
        this.bitAddress = addr;
        this.bitCount = count;
    }
}
