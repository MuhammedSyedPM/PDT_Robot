// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdDiagGetReport extends NurCmd
{
    public static final int CMD = 43;
    public static final int CMD_DIAG_GETREPORT = 1;
    public static final int CMD_DIAG_CFG = 2;
    int mFlags;
    NurRespDiagGetReport mResp;
    
    public NurRespDiagGetReport getResponse() {
        return this.mResp;
    }
    
    public NurCmdDiagGetReport(final int flags) {
        super(43, 0, 5);
        this.mResp = new NurRespDiagGetReport();
    }
    
    @Override
    public int serializePayload(final byte[] data, int offset) throws Exception {
        final int origOffset = offset;
        offset += NurPacket.PacketByte(data, offset, 1);
        offset += NurPacket.PacketDword(data, offset, this.mFlags);
        return offset - origOffset;
    }
    
    @Override
    public void deserializePayload(final byte[] data, int offset, final int dataLen) {
        this.mResp.flags = NurPacket.BytesToDword(data, offset);
        offset += 4;
        this.mResp.uptime = NurPacket.BytesToDword(data, offset);
        offset += 4;
        this.mResp.rfActiveTime = NurPacket.BytesToDword(data, offset);
        offset += 4;
        this.mResp.temperature = NurPacket.BytesToDword(data, offset);
        offset += 4;
        this.mResp.bytesIn = NurPacket.BytesToDword(data, offset);
        offset += 4;
        this.mResp.bytesOut = NurPacket.BytesToDword(data, offset);
        offset += 4;
        this.mResp.bytesIgnored = NurPacket.BytesToDword(data, offset);
        offset += 4;
        this.mResp.antennaErrors = NurPacket.BytesToDword(data, offset);
        offset += 4;
        this.mResp.hwErrors = NurPacket.BytesToDword(data, offset);
        offset += 4;
        this.mResp.invTags = NurPacket.BytesToDword(data, offset);
        offset += 4;
        this.mResp.invColl = NurPacket.BytesToDword(data, offset);
        offset += 4;
        this.mResp.readTags = NurPacket.BytesToDword(data, offset);
        offset += 4;
        this.mResp.readErrors = NurPacket.BytesToDword(data, offset);
        offset += 4;
        this.mResp.writeTags = NurPacket.BytesToDword(data, offset);
        offset += 4;
        this.mResp.writeErrors = NurPacket.BytesToDword(data, offset);
        offset += 4;
        this.mResp.errorConds = NurPacket.BytesToDword(data, offset);
        offset += 4;
        this.mResp.setupErrs = NurPacket.BytesToDword(data, offset);
        offset += 4;
        this.mResp.invalidCmds = NurPacket.BytesToDword(data, offset);
    }
}
