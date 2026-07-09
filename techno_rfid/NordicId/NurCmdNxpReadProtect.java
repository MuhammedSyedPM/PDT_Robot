// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdNxpReadProtect extends NurCmd
{
    public static final int CMD = 80;
    private int mPasswd;
    private boolean mSet;
    private NurRWSingulationBlock mSb;
    
    public NurCmdNxpReadProtect(final int passwd, final boolean set, final int sBank, final int sAddress, final int sMaskBitLength, final byte[] sMask) {
        this(80, passwd, set, sBank, sAddress, sMaskBitLength, sMask);
    }
    
    public NurCmdNxpReadProtect(final int newcmd, final int passwd, final boolean set, final int sBank, final int sAddress, final int sMaskBitLength, final byte[] sMask) {
        super(newcmd, 0, 15 + sMaskBitLength);
        this.mPasswd = passwd;
        this.mSb = new NurRWSingulationBlock(sBank, sAddress, sMaskBitLength, sMask);
        this.mSet = set;
    }
    
    @Override
    public int serializePayload(final byte[] data, int offset) throws Exception {
        final int origOffset = offset;
        int cFlags = 1;
        cFlags |= this.mSb.getFlags();
        offset += NurPacket.PacketByte(data, offset, cFlags);
        offset += NurPacket.PacketDword(data, offset, this.mPasswd);
        offset = this.mSb.serialize(data, offset);
        offset += NurPacket.PacketByte(data, offset, 1);
        offset += NurPacket.PacketByte(data, offset, this.mSet ? 1 : 0);
        return offset - origOffset;
    }
}
