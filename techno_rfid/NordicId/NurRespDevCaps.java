// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurRespDevCaps
{
    public static final int DC_RXDECFM0 = 1;
    public static final int DC_RXDECM2 = 2;
    public static final int DC_RXDECM4 = 4;
    public static final int DC_RXDECM8 = 8;
    public static final int DC_RXLF40k = 16;
    public static final int DC_RXLF80k = 32;
    public static final int DC_RXLF160k = 64;
    public static final int DC_RXLF256k = 128;
    public static final int DC_RXLF320k = 256;
    public static final int DC_RXLF640k = 512;
    public static final int DC_RXLFres1 = 1024;
    public static final int DC_RXLFres2 = 2048;
    public static final int DC_RXLFMASK = 4080;
    public static final int DC_HASBEEP = 4096;
    public static final int DC_HASLIGHT = 8192;
    public static final int DC_HASTAP = 16384;
    public static final int DC_ANTTUNE = 32768;
    public static final int DC_CHSCANNER = 65536;
    public static final int DC_INVREAD = 131072;
    public static final int DC_ANTPOWER = 262144;
    public static final int DC_POWEROFS = 524288;
    public static final int DC_BEAMANT = 1048576;
    public static final int DC_FETCHSINGLE = 2097152;
    public static final int DC_ANTENNAMAP = 4194304;
    public static final int DC_GEN2VER2 = 8388608;
    public static final int DC_RFPROFILE = 16777216;
    public static final int DC_DIAG = 33554432;
    public static final int DC_TAGPHASE = 67108864;
    public static final int MODULETYPE_NUR05W = 1;
    public static final int MODULETYPE_NUR05WL = 2;
    public static final int MODULETYPE_NUR05WL2 = 3;
    public static final int MODULETYPE_NUR1W0 = 4;
    public static final int MODULETYPE_NUR2_1W = 5;
    public static final int MODULETYPE_NUR2_01W = 6;
    public static final int CFG_NONE = 0;
    public static final int CFG_USB_TABLE_READER = 1;
    public static final int CFG_ETH_TABLE_READER = 2;
    public static final int CFG_STIX_READER = 4;
    public static final int CFG_ONEWATT_READER = 32;
    public static final int CFG_BEAM_READER = 64;
    public static final int CFG_MULTIPORT = 128;
    public static final int CHIPVER_AS3992 = 1;
    public static final int CHIPVER_AS3993 = 2;
    public static final int CHIPVER_R2000 = 3;
    public int responseSize;
    public int flagSet1;
    public int flagSet2;
    public int maxTxdBm;
    public int txAttnStep;
    public int maxTxmW;
    public int txSteps;
    public int szTagBuffer;
    public int curCfgMaxAnt;
    public int curCfgMaxGPIO;
    public int chipVersion;
    public int moduleType;
    public int moduleConfigFlags;
    public int v2Level;
    public int secChipMajorVersion;
    public int secChipMinorVersion;
    public int secChipMaintenanceVersion;
    public int secChipReleaseVersion;
    
    public NurRespDevCaps() {
        this.responseSize = 0;
        this.flagSet1 = 0;
        this.flagSet2 = 0;
        this.maxTxdBm = 0;
        this.txAttnStep = 0;
        this.maxTxmW = 0;
        this.txSteps = 0;
        this.szTagBuffer = 0;
        this.curCfgMaxAnt = 0;
        this.curCfgMaxGPIO = 0;
        this.moduleConfigFlags = 0;
        this.v2Level = 0;
    }
    
    public boolean hasFM0() {
        return (this.flagSet1 & 0x1) != 0x0;
    }
    
    public boolean hasM2() {
        return (this.flagSet1 & 0x2) != 0x0;
    }
    
    public boolean hasM4() {
        return (this.flagSet1 & 0x4) != 0x0;
    }
    
    public boolean hasM8() {
        return (this.flagSet1 & 0x8) != 0x0;
    }
    
    public boolean hasLf40k() {
        return (this.flagSet1 & 0x10) != 0x0;
    }
    
    public boolean hasLf80k() {
        return (this.flagSet1 & 0x20) != 0x0;
    }
    
    public boolean hasLf160k() {
        return (this.flagSet1 & 0x40) != 0x0;
    }
    
    public boolean hasLf256k() {
        return (this.flagSet1 & 0x80) != 0x0;
    }
    
    public boolean hasLf320k() {
        return (this.flagSet1 & 0x100) != 0x0;
    }
    
    public boolean hasLf640k() {
        return (this.flagSet1 & 0x200) != 0x0;
    }
    
    public boolean canBeep() {
        return (this.flagSet1 & 0x1000) != 0x0;
    }
    
    public boolean hasLightSensor() {
        return (this.flagSet1 & 0x2000) != 0x0;
    }
    
    public boolean hasTapSensor() {
        return (this.flagSet1 & 0x4000) != 0x0;
    }
    
    public boolean canTuneAntenna() {
        return (this.flagSet1 & 0x8000) != 0x0;
    }
    
    public boolean canScanChannels() {
        return (this.flagSet1 & 0x10000) != 0x0;
    }
    
    public boolean hasInventoryRead() {
        return (this.flagSet1 & 0x20000) != 0x0;
    }
    
    public boolean hasPerAntennaPower() {
        return (this.flagSet1 & 0x40000) != 0x0;
    }
    
    public boolean hasPowerOffset() {
        return (this.flagSet1 & 0x80000) != 0x0;
    }
    
    public boolean hasBeamAntenna() {
        return (this.flagSet1 & 0x100000) != 0x0;
    }
    
    public boolean isUsbTableReader() {
        return (this.moduleConfigFlags & 0x1) != 0x0;
    }
    
    public boolean isEthernetTableReader() {
        return (this.moduleConfigFlags & 0x2) != 0x0;
    }
    
    public boolean isStixReader() {
        return (this.moduleConfigFlags & 0x4) != 0x0;
    }
    
    public boolean isOneWattReader() {
        return (this.moduleConfigFlags & 0x20) != 0x0;
    }
    
    public boolean isBeamReader() {
        return (this.moduleConfigFlags & 0x40) != 0x0;
    }
    
    public boolean supportsv2Commands() {
        return (this.flagSet1 & 0x800000) != 0x0 && this.v2Level > 0;
    }
    
    public boolean hasRfProfile() {
        return (this.flagSet1 & 0x1000000) != 0x0;
    }
    
    public boolean hasDiagnostics() {
        return (this.flagSet1 & 0x2000000) != 0x0;
    }
    
    public boolean hasTagPhase() {
        return (this.flagSet1 & 0x4000000) != 0x0;
    }
    
    public String getModuleType() {
        switch (this.moduleType) {
            case 1: {
                return "NUR05W";
            }
            case 2: {
                return "NUR05WL";
            }
            case 3: {
                return "NUR05WL2";
            }
            case 4: {
                return "NUR10W";
            }
            case 5: {
                return "NUR2-1W";
            }
            case 6: {
                return "NUR2-01W";
            }
            default: {
                return "unknown type " + this.moduleType;
            }
        }
    }
}
