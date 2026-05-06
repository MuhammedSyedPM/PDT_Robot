// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdDevCaps extends NurCmd
{
    public static final int CMD = 11;
    private NurRespDevCaps mDc;
    
    public NurRespDevCaps getResponse() {
        return this.mDc;
    }
    
    public NurCmdDevCaps() {
        super(11);
        this.mDc = new NurRespDevCaps();
    }
    
    @Override
    public void deserializePayload(final byte[] data, int offset, final int dataLen) {
        this.mDc.responseSize = NurPacket.BytesToDword(data, offset);
        offset += 4;
        this.mDc.flagSet1 = NurPacket.BytesToDword(data, offset);
        offset += 4;
        this.mDc.flagSet2 = NurPacket.BytesToDword(data, offset);
        offset += 4;
        this.mDc.maxTxdBm = NurPacket.BytesToDword(data, offset);
        offset += 4;
        this.mDc.txAttnStep = NurPacket.BytesToDword(data, offset);
        offset += 4;
        this.mDc.maxTxmW = NurPacket.BytesToWord(data, offset);
        offset += 2;
        this.mDc.txSteps = NurPacket.BytesToWord(data, offset);
        offset += 2;
        this.mDc.szTagBuffer = NurPacket.BytesToWord(data, offset);
        offset += 2;
        this.mDc.curCfgMaxAnt = NurPacket.BytesToWord(data, offset);
        offset += 2;
        this.mDc.curCfgMaxGPIO = NurPacket.BytesToWord(data, offset);
        offset += 2;
        this.mDc.chipVersion = NurPacket.BytesToWord(data, offset);
        offset += 2;
        this.mDc.moduleType = NurPacket.BytesToWord(data, offset);
        offset += 2;
        this.mDc.moduleConfigFlags = NurPacket.BytesToDword(data, offset);
        offset += 4;
        this.mDc.v2Level = NurPacket.BytesToWord(data, offset);
        offset += 2;
        this.mDc.secChipMajorVersion = NurPacket.BytesToDword(data, offset);
        offset += 4;
        this.mDc.secChipMinorVersion = NurPacket.BytesToDword(data, offset);
        offset += 4;
        this.mDc.secChipMaintenanceVersion = NurPacket.BytesToDword(data, offset);
        offset += 4;
        this.mDc.secChipReleaseVersion = NurPacket.BytesToDword(data, offset);
        offset += 4;
    }
}
