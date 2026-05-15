// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdGetSystem extends NurCmd
{
    public static final int CMD = 8;
    private NurRespSystemInfo mSystemInfo;
    
    public NurRespSystemInfo getResponse() {
        return this.mSystemInfo;
    }
    
    public NurCmdGetSystem() {
        super(8);
        this.mSystemInfo = new NurRespSystemInfo();
    }
    
    @Override
    public void deserializePayload(final byte[] data, int offset, final int dataLen) {
        this.mSystemInfo.blAddr = NurPacket.BytesToDword(data, offset);
        offset += 4;
        this.mSystemInfo.appAddr = NurPacket.BytesToDword(data, offset);
        offset += 4;
        this.mSystemInfo.vectorBase = NurPacket.BytesToDword(data, offset);
        offset += 4;
        this.mSystemInfo.appSzWord = NurPacket.BytesToDword(data, offset);
        offset += 4;
        this.mSystemInfo.appCRCWord = NurPacket.BytesToDword(data, offset);
        offset += 4;
        this.mSystemInfo.szFlash = NurPacket.BytesToDword(data, offset);
        offset += 4;
        this.mSystemInfo.szSram = NurPacket.BytesToDword(data, offset);
        offset += 4;
        this.mSystemInfo.stackTop = NurPacket.BytesToDword(data, offset);
        offset += 4;
        this.mSystemInfo.nvSetAddr = NurPacket.BytesToDword(data, offset);
        offset += 4;
        this.mSystemInfo.szNvSettings = NurPacket.BytesToDword(data, offset);
        offset += 4;
        this.mSystemInfo.mainStackUsage = NurPacket.BytesToDword(data, offset);
        offset += 4;
        this.mSystemInfo.szUsedSram = NurPacket.BytesToDword(data, offset);
        offset += 4;
        this.mSystemInfo.szTagBuffer = NurPacket.BytesToDword(data, offset);
        offset += 4;
    }
}
