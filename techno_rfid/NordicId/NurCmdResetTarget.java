// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdResetTarget extends NurCmd
{
    public static final int CMD = 58;
    private int mSession;
    private boolean mTargetIsA;
    
    public NurCmdResetTarget(final int session, final boolean targetIsA) {
        super(58, 0, 2);
        this.mSession = session;
        this.mTargetIsA = targetIsA;
    }
    
    @Override
    public int serializePayload(final byte[] data, int offset) throws Exception {
        final int origOffset = offset;
        offset += NurPacket.PacketByte(data, offset, this.mSession);
        offset += NurPacket.PacketByte(data, offset, this.mTargetIsA ? 1 : 0);
        return offset - origOffset;
    }
}
