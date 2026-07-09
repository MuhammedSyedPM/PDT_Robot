// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdBlockWriteEx extends NurCmd
{
    private static final int CMD = 66;
    private int mBlockSize;
    private boolean mCbSecured;
    private int mCbPwd;
    private NurRWSingulationBlock mSb;
    private int mWbBank;
    private int mWbAddress;
    private int mWbWordCount;
    private byte[] mWbData;
    
    public NurCmdBlockWriteEx(final int passwd, final boolean secured, final int sBank, final int sAddress, final int sMaskBitLength, final byte[] sMask, final int wrBank, final int wrAddress, final byte[] wrData, final int blockSize) throws NurApiException {
        super(66, 21 + sMaskBitLength + wrData.length);
        this.mBlockSize = 2;
        if (wrData == null || wrData.length > 240) {
            throw new NurApiException(5);
        }
        if (wrData.length % 2 != 0) {
            throw new NurApiException(4106);
        }
        this.mSb = new NurRWSingulationBlock(sBank, sAddress, sMaskBitLength, sMask);
        this.mCbPwd = passwd;
        this.mCbSecured = secured;
        this.mWbBank = wrBank;
        this.mWbAddress = wrAddress;
        this.mWbWordCount = wrData.length / 2;
        this.mWbData = wrData;
        this.mBlockSize = blockSize;
    }
    
    @Override
    public int serializePayload(final byte[] data, int offset) throws Exception {
        final int origOffset = offset;
        int cflags = 0;
        int pw = 0;
        if (this.mCbSecured) {
            cflags |= 0x1;
            pw = this.mCbPwd;
        }
        cflags |= this.mSb.getFlags();
        offset += NurPacket.PacketByte(data, offset, cflags);
        offset += NurPacket.PacketDword(data, offset, pw);
        offset = this.mSb.serialize(data, offset);
        int btf = ((cflags & 0x8) != 0x0) ? 11 : 7;
        btf += this.mWbWordCount * 2;
        offset += NurPacket.PacketByte(data, offset, btf);
        offset += NurPacket.PacketByte(data, offset, this.mWbBank);
        if ((cflags & 0x8) != 0x0) {
            throw new Exception("64 bit address is NYI!");
        }
        offset += NurPacket.PacketDword(data, offset, this.mWbAddress);
        offset += NurPacket.PacketByte(data, offset, this.mWbWordCount);
        offset += NurPacket.PacketByte(data, offset, this.mBlockSize);
        offset += NurPacket.PacketBytes(data, offset, this.mWbData, this.mWbWordCount * 2);
        return offset - origOffset;
    }
}
