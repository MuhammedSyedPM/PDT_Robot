// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurTTHashObject
{
    public boolean seenFlag;
    public long lastSeenTimestamp;
    public float[] scaledRssi;
    public float[] weightedRssi;
    public float[] beamRssi;
    public int antennaID;
    public NurTTTag ttTag;
    
    public NurTTHashObject(final byte[] epc) {
        this.seenFlag = false;
        this.lastSeenTimestamp = 0L;
        this.scaledRssi = new float[32];
        this.weightedRssi = new float[32];
        this.beamRssi = new float[32];
        this.ttTag = null;
        this.ttTag = new NurTTTag(epc);
    }
    
    public NurTTHashObject(final NurTag tag) {
        this.seenFlag = false;
        this.lastSeenTimestamp = 0L;
        this.scaledRssi = new float[32];
        this.weightedRssi = new float[32];
        this.beamRssi = new float[32];
        this.ttTag = null;
        this.ttTag = new NurTTTag(tag);
    }
}
