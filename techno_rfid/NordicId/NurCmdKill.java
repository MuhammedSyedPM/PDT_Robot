// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdKill extends NurCmd
{
    public static final int CMD = 55;
    private NurRWSingulationBlock mSb;
    private int mPasswd;
    
    public NurCmdKill(final int passwd, final int sBank, final int sAddress, final int sMaskBitLength, final byte[] sMask) {
        super(55, 0, 13 + sMaskBitLength);
        this.mSb = new NurRWSingulationBlock(sBank, sAddress, sMaskBitLength, sMask);
        this.mPasswd = passwd;
    }
    
    @Override
    public int serializePayload(final byte[] data, int offset) throws Exception {
        final int origOffset = offset;
        int flags = 0;
        flags = (this.mSb.getFlags() | 0x1);
        offset += NurPacket.PacketByte(data, offset, flags);
        offset += NurPacket.PacketDword(data, offset, this.mPasswd);
        offset = this.mSb.serialize(data, offset);
        return offset - origOffset;
    }
}
