// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdWriteRegister extends NurCmd
{
    public static final int CMD = 145;
    private int mReg;
    private int mValue;
    
    public NurCmdWriteRegister(final int register, final int value) {
        super(145, 0, 2);
        this.mReg = register;
        this.mValue = value;
    }
    
    @Override
    public int serializePayload(final byte[] data, int offset) throws Exception {
        final int origOffset = offset;
        offset += NurPacket.PacketByte(data, offset, this.mReg);
        offset += NurPacket.PacketByte(data, offset, this.mValue);
        return offset - origOffset;
    }
}
