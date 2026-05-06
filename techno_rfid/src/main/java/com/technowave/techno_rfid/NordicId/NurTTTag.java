// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurTTTag
{
    public byte[] epc;
    public int epcLen;
    public int changedEvents;
    public long lastUpdateTime;
    public long lastSeenTime;
    public int[] rssi;
    public int maxScaledRssi;
    public int maxRssi;
    public int maxRssiAnt;
    public boolean visible;
    public float X;
    public float Y;
    public int prevSector;
    public int sector;
    public int firstTTIOReadSource;
    public int secondTTIOReadSource;
    public int directionTTIO;
    
    private void createEpc(final byte[] b) {
        if (b != null && b.length > 0 && b.length <= 62) {
            System.arraycopy(b, 0, this.epc = new byte[b.length], 0, b.length);
        }
    }
    
    private void clearAll() {
        for (int i = 0; i < this.rssi.length; ++i) {
            this.rssi[i] = -128;
        }
    }
    
    public NurTTTag(final NurTag tag) {
        this.epc = null;
        this.epcLen = 0;
        this.changedEvents = 0;
        this.lastUpdateTime = 0L;
        this.lastSeenTime = 0L;
        this.rssi = new int[32];
        this.maxScaledRssi = 0;
        this.maxRssi = -128;
        this.maxRssiAnt = -1;
        this.visible = false;
        this.X = -1.0f;
        this.Y = -1.0f;
        this.prevSector = -1;
        this.sector = -1;
        this.firstTTIOReadSource = -1;
        this.secondTTIOReadSource = -1;
        this.directionTTIO = 0;
        this.createEpc(tag.mEpc);
        this.clearAll();
    }
    
    public NurTTTag(final byte[] epcBytes) {
        this.epc = null;
        this.epcLen = 0;
        this.changedEvents = 0;
        this.lastUpdateTime = 0L;
        this.lastSeenTime = 0L;
        this.rssi = new int[32];
        this.maxScaledRssi = 0;
        this.maxRssi = -128;
        this.maxRssiAnt = -1;
        this.visible = false;
        this.X = -1.0f;
        this.Y = -1.0f;
        this.prevSector = -1;
        this.sector = -1;
        this.firstTTIOReadSource = -1;
        this.secondTTIOReadSource = -1;
        this.directionTTIO = 0;
        this.createEpc(epcBytes);
        this.clearAll();
    }
    
    public NurTTTag(final byte[] epcBytes, final boolean isVisible) {
        this.epc = null;
        this.epcLen = 0;
        this.changedEvents = 0;
        this.lastUpdateTime = 0L;
        this.lastSeenTime = 0L;
        this.rssi = new int[32];
        this.maxScaledRssi = 0;
        this.maxRssi = -128;
        this.maxRssiAnt = -1;
        this.visible = false;
        this.X = -1.0f;
        this.Y = -1.0f;
        this.prevSector = -1;
        this.sector = -1;
        this.firstTTIOReadSource = -1;
        this.secondTTIOReadSource = -1;
        this.directionTTIO = 0;
        this.createEpc(epcBytes);
        this.visible = isVisible;
        this.clearAll();
    }
}
