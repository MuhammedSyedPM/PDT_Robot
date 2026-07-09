// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdPageWrite extends NurCmd
{
    public static final int CMD = 113;
    private static final int NUR_FLASH_PAGE_SIZE = 256;
    private byte[] mBuffer;
    private int mPage;
    
    public NurCmdPageWrite(final int page, final byte[] buffer) {
        super(113, 0, 6 + buffer.length);
        this.mPage = page;
        this.mBuffer = buffer;
    }
    
    @Override
    public int serializePayload(final byte[] data, int offset) throws Exception {
        final int origOffset = offset;
        final long crc = CRC32.calc(0L, this.mBuffer, 0, 256);
        for (int i = 0; i < 256; i += 4) {
            final byte[] mBuffer = this.mBuffer;
            final int n = i;
            mBuffer[n] = (byte)((long)mBuffer[n] ^ (crc >> 0 & 0xFFL));
            final byte[] mBuffer2 = this.mBuffer;
            final int n2 = i + 1;
            mBuffer2[n2] = (byte)((long)mBuffer2[n2] ^ (crc >> 8 & 0xFFL));
            final byte[] mBuffer3 = this.mBuffer;
            final int n3 = i + 2;
            mBuffer3[n3] = (byte)((long)mBuffer3[n3] ^ (crc >> 16 & 0xFFL));
            final byte[] mBuffer4 = this.mBuffer;
            final int n4 = i + 3;
            mBuffer4[n4] = (byte)((long)mBuffer4[n4] ^ (crc >> 24 & 0xFFL));
        }
        offset += NurPacket.PacketWord(data, offset, this.mPage);
        offset += NurPacket.PacketDword(data, offset, crc);
        offset += NurPacket.PacketBytes(data, offset, this.mBuffer, this.mBuffer.length);
        return offset - origOffset;
    }
}
