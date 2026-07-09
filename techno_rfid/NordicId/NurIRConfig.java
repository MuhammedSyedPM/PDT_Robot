// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurIRConfig
{
    public boolean IsRunning;
    public int irType;
    public int irBank;
    public int irAddr;
    public int irWordCount;
    
    public NurIRConfig() {
        this.IsRunning = false;
        this.irType = -1;
        this.irBank = -1;
        this.irAddr = -1;
        this.irWordCount = 0;
    }
}
