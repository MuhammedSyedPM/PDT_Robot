// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdAppValidate extends NurCmd
{
    public static final int CMD = 116;
    private int mSize;
    private long mCrc;
    
    public NurCmdAppValidate(final int newcommand, final int size, final long crc) {
        super(newcommand, 0, 8);
        this.mSize = size;
        this.mCrc = crc;
    }
    
    public NurCmdAppValidate(final int size, final long crc) {
        this(116, size, crc);
    }
    
    @Override
    public int serializePayload(final byte[] data, int offset) throws Exception {
        final int origOffset = offset;
        offset += NurPacket.PacketDword(data, offset, this.mSize);
        offset += NurPacket.PacketDword(data, offset, this.mCrc);
        return offset - origOffset;
    }
}
