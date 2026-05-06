// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdRegionInfo extends NurCmd
{
    public static final int CMD = 36;
    private NurRespRegionInfo mResp;
    private int mRegionId;
    
    public NurCmdRegionInfo() {
        super(36);
        this.mResp = new NurRespRegionInfo();
        this.mRegionId = -1;
    }
    
    public NurCmdRegionInfo(final int regionId) throws NurApiException {
        super(36, 0, (regionId != -1) ? 1 : 0);
        this.mResp = new NurRespRegionInfo();
        this.mRegionId = -1;
        if (regionId < 0) {
            throw new NurApiException(5);
        }
        this.mRegionId = regionId;
    }
    
    public NurRespRegionInfo getResponse() {
        return this.mResp;
    }
    
    @Override
    public int serializePayload(final byte[] data, final int offset) {
        if (this.mRegionId != -1) {
            return NurPacket.PacketByte(data, offset, this.mRegionId);
        }
        return 0;
    }
    
    @Override
    public void deserializePayload(final byte[] data, int offset, final int dataLen) {
        this.mResp.regionId = NurPacket.BytesToByte(data, offset);
        ++offset;
        this.mResp.baseFrequency = NurPacket.BytesToDword(data, offset);
        offset += 4;
        this.mResp.channelSpacing = NurPacket.BytesToDword(data, offset);
        offset += 4;
        this.mResp.channelCount = NurPacket.BytesToByte(data, offset);
        ++offset;
        this.mResp.channelTime = NurPacket.BytesToDword(data, offset);
        offset += 4;
        final int strLen = NurPacket.BytesToByte(data, offset++);
        this.mResp.name = NurPacket.BytesToString(data, offset, strLen);
        offset += strLen;
    }
}
