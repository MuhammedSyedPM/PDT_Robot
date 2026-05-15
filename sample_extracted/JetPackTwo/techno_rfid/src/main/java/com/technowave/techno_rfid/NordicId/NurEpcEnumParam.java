// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurEpcEnumParam
{
    int antenna;
    int twAddr;
    int twLen;
    public int useBlWrite;
    public byte[] startVal;
    public int epcLen;
    public int modAddr;
    public int bitLen;
    public boolean bReset;
    public byte[] baseEPC;
    
    public NurEpcEnumParam() {
        this.startVal = new byte[8];
        this.baseEPC = new byte[16];
    }
}
