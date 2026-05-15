// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurRWSingulationBlock
{
    public int mSbBank;
    public int mSbAddress;
    public int mSbMaskBitLength;
    public byte[] mSbMask;
    
    public NurRWSingulationBlock() {
        this.mSbBank = 0;
        this.mSbAddress = 0;
        this.mSbMaskBitLength = 0;
        this.mSbMask = null;
    }
    
    public NurRWSingulationBlock(final int sBank, final int sAddress, final int sMaskBitLength, final byte[] sMask) {
        this.mSbBank = 0;
        this.mSbAddress = 0;
        this.mSbMaskBitLength = 0;
        this.mSbMask = null;
        this.mSbBank = sBank;
        this.mSbAddress = sAddress;
        this.mSbMaskBitLength = sMaskBitLength;
        this.mSbMask = sMask;
    }
    
    public int getFlags() {
        return (this.mSbMaskBitLength > 0) ? 2 : 0;
    }
    
    public int serialize(final byte[] data, int offset) throws NurApiException {
        final int flags = this.getFlags();
        if ((flags & 0x2) == 0x0) {
            return offset;
        }
        int btf = NurApi.bitLenToByteLen(this.mSbMaskBitLength);
        final int hdrSize = ((flags & 0x4) != 0x0) ? 11 : 7;
        btf += hdrSize;
        offset += NurPacket.PacketByte(data, offset, btf);
        offset += NurPacket.PacketByte(data, offset, this.mSbBank);
        if ((flags & 0x4) != 0x0) {
            throw new NurApiException("64 bit address is NYI!");
        }
        offset += NurPacket.PacketDword(data, offset, this.mSbAddress);
        offset += NurPacket.PacketWord(data, offset, this.mSbMaskBitLength);
        offset += NurPacket.PacketBytes(data, offset, this.mSbMask, btf - hdrSize);
        return offset;
    }
    
    public static int getByteLength(final int sMaskBitLength) throws Exception {
        if (sMaskBitLength <= 0) {
            return 0;
        }
        return 8 + NurApi.bitLenToByteLen(sMaskBitLength);
    }
    
    public static int getByteLength(final NurRWSingulationBlock sb) throws Exception {
        if (sb == null || sb.mSbMaskBitLength <= 0) {
            return 0;
        }
        return 8 + NurApi.bitLenToByteLen(sb.mSbMaskBitLength);
    }
}
