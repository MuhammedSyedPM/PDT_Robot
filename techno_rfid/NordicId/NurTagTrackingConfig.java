// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurTagTrackingConfig
{
    public int flags;
    public int events;
    public int rssiDeltaFilter;
    public float positionDeltaFilter;
    public int scanUntilNewTagsCount;
    public int visibilityTimeout;
    public byte selectBank;
    public int selectAddress;
    public short selectMaskBitLength;
    public byte[] selectMask;
    public int inAntennaMask;
    public int outAntennaMask;
    public NurInventoryExtendedFilter[] complexFilters;
    public NurInventoryExtended complexFilterParams;
    
    public NurTagTrackingConfig() {
        this.complexFilters = null;
        this.complexFilterParams = null;
    }
}
