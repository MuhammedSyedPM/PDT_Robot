// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdDiagConfig extends NurCmd
{
    public static final int CMD = 43;
    public static final int CMD_DIAG_GETREPORT = 1;
    public static final int CMD_DIAG_CFG = 2;
    int mFlags;
    int mInterval;
    NurRespDiagConfig mResp;
    
    public NurRespDiagConfig getResponse() {
        return this.mResp;
    }
    
    public NurCmdDiagConfig(final int flags, final int interval) {
        super(43, 0, 9);
        this.mResp = new NurRespDiagConfig();
        this.mFlags = flags;
        this.mInterval = interval;
    }
    
    public NurCmdDiagConfig() {
        super(43, 0, 1);
        this.mResp = new NurRespDiagConfig();
    }
    
    @Override
    public int serializePayload(final byte[] data, int offset) throws Exception {
        final int origOffset = offset;
        offset += NurPacket.PacketByte(data, offset, 2);
        if (this.payloadLen == 9) {
            offset += NurPacket.PacketDword(data, offset, this.mFlags);
            offset += NurPacket.PacketDword(data, offset, this.mInterval);
        }
        return offset - origOffset;
    }
    
    @Override
    public void deserializePayload(final byte[] data, int offset, final int dataLen) {
        if (dataLen >= 8) {
            this.mResp.flags = NurPacket.BytesToDword(data, offset);
            offset += 4;
            this.mResp.interval = NurPacket.BytesToDword(data, offset);
        }
    }
}
