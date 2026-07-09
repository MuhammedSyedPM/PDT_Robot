// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurUntraceableParam
{
    public boolean setU;
    public boolean rxAttn;
    public boolean hideUser;
    public boolean hideEPC;
    public int epcWordLen;
    public int tidPolicy;
    public int rangePolicy;
    
    public NurUntraceableParam() {
        this.setU = false;
        this.rxAttn = false;
        this.hideUser = false;
        this.hideEPC = false;
        this.epcWordLen = 6;
        this.tidPolicy = 0;
        this.rangePolicy = 0;
    }
    
    public NurUntraceableParam(final NurUntraceableParam other) {
        this.setU = false;
        this.rxAttn = false;
        this.hideUser = false;
        this.hideEPC = false;
        this.epcWordLen = 6;
        this.tidPolicy = 0;
        this.rangePolicy = 0;
        this.epcWordLen = other.epcWordLen;
        this.hideEPC = other.hideEPC;
        this.hideUser = other.hideUser;
        this.rangePolicy = other.rangePolicy;
        this.rxAttn = other.rxAttn;
        this.setU = other.setU;
        this.tidPolicy = other.tidPolicy;
    }
}
