// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurRespRegionInfo
{
    public int regionId;
    public int baseFrequency;
    public int channelSpacing;
    public int channelCount;
    public int channelTime;
    public String name;
    
    public NurRespRegionInfo() {
        this.regionId = 0;
        this.baseFrequency = 0;
        this.channelSpacing = 0;
        this.channelCount = 0;
        this.channelTime = 0;
        this.name = "";
    }
}
