// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdRevertTune extends NurCmd
{
    public static final int CMD = 102;
    private int mHowto;
    
    public NurCmdRevertTune(final int revertType) throws NurApiException {
        super(102, 0, 16);
        this.mHowto = 0;
        if (revertType < 0 || revertType > 2) {
            throw new NurApiException(5);
        }
        this.mHowto = revertType;
    }
    
    public int getResponse() {
        return this.mHowto;
    }
    
    @Override
    public int serializePayload(final byte[] data, int offset) {
        offset += NurPacket.PacketDword(data, offset, this.mHowto);
        offset += NurPacket.PacketFill(data, offset, (byte)0, 12);
        return 16;
    }
    
    @Override
    public void deserializePayload(final byte[] data, final int offset, final int dataLen) throws Exception {
        if (this.status != 0 || dataLen < 4) {
            return;
        }
        this.mHowto = NurPacket.BytesToDword(data, offset);
    }
}
