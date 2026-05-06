// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurRespPermalock
{
    public boolean wasOk;
    public boolean isTagError;
    public int tagError;
    public int fromBank;
    public int atAddr;
    public short[] lockWords;
    
    public NurRespPermalock() {
        this.wasOk = false;
        this.isTagError = false;
        this.tagError = 0;
        this.fromBank = 1;
        this.atAddr = 0;
    }
}
