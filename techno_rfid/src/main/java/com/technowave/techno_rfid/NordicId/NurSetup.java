// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurSetup
{
    public int flags;
    public int linkFreq;
    public int rxDecoding;
    public int txLevel;
    public int txModulation;
    public int regionId;
    public int inventoryQ;
    public int inventorySession;
    public int inventoryRounds;
    public int antennaMask;
    public int scanSingleTriggerTimeout;
    public int inventoryTriggerTimeout;
    public int selectedAntenna;
    public int opFlags;
    public int inventoryTarget;
    public int inventoryEpcLength;
    public RssiFilter readRssiFilter;
    public RssiFilter writeRssiFilter;
    public RssiFilter inventoryRssiFilter;
    public int readTimeout;
    public int writeTimeout;
    public int lockTimeout;
    public int killTimeout;
    public int periodSetup;
    public int[] antPower;
    public int powerOffset;
    public int antennaMaskEx;
    public AutotuneSetup autotune;
    public int[] antPowerEx;
    public int rxSensitivity;
    public int rfProfile;
    
    public NurSetup() {
        this.flags = 0;
        this.readRssiFilter = new RssiFilter();
        this.writeRssiFilter = new RssiFilter();
        this.inventoryRssiFilter = new RssiFilter();
        this.antPower = new int[4];
        this.antPowerEx = new int[32];
        this.autotune = new AutotuneSetup();
    }
}
