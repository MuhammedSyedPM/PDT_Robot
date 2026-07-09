// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdQueryCrc extends NurCmd
{
    public static final int CMD = 117;
    private NurRespQueryCrc mResp;
    
    public NurRespQueryCrc getResponse() {
        return this.mResp;
    }
    
    public NurCmdQueryCrc() {
        super(117);
        this.mResp = new NurRespQueryCrc();
    }
    
    @Override
    public void deserializePayload(final byte[] data, int offset, final int dataLen) {
        this.mResp.appInfoBase = NurPacket.BytesToDword(data, offset);
        offset += 4;
        this.mResp.appInfoPage = NurPacket.BytesToDword(data, offset);
        offset += 4;
        this.mResp.szApp = NurPacket.BytesToDword(data, offset);
        offset += 4;
        this.mResp.memCRC = NurPacket.BytesToDword(data, offset);
        offset += 4;
        this.mResp.calcCRC = NurPacket.BytesToDword(data, offset);
        offset += 4;
    }
}
