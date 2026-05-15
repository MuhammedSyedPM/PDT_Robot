// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdReadTag extends NurCmd
{
    public static final int CMD = 51;
    private byte[] mResponse;
    private boolean mCbSecured;
    private int mCbPwd;
    private NurRWSingulationBlock mSb;
    private int mRbBank;
    private int mRbAddress;
    private int mRbWordCount;
    
    public NurCmdReadTag(final int passwd, final boolean secured, final int sBank, final int sAddress, final int sMaskBitLength, final byte[] sMask, final int rdBank, final int rdAddress, final int rdByteCount) throws NurApiException {
        super(51, 0, 20 + sMaskBitLength);
        if (rdByteCount > 510 || sMaskBitLength > 255) {
            throw new NurApiException(5);
        }
        if (rdByteCount % 2 != 0) {
            throw new NurApiException(4106);
        }
        this.mSb = new NurRWSingulationBlock(sBank, sAddress, sMaskBitLength, sMask);
        this.mCbPwd = passwd;
        this.mCbSecured = secured;
        this.mRbBank = rdBank;
        this.mRbAddress = rdAddress;
        this.mRbWordCount = rdByteCount / 2;
    }
    
    public byte[] getResponse() {
        return this.mResponse;
    }
    
    @Override
    public void deserializePayload(final byte[] data, final int offset, final int dataLen) {
        if (this.getStatus() == 0) {
            System.arraycopy(data, offset, this.mResponse = new byte[dataLen], 0, dataLen);
        }
        else {
            this.mResponse = null;
        }
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
        final int btf = ((cflags & 0x8) != 0x0) ? 10 : 6;
        offset += NurPacket.PacketByte(data, offset, btf);
        offset += NurPacket.PacketByte(data, offset, this.mRbBank);
        if ((cflags & 0x8) != 0x0) {
            throw new Exception("64 bit address is NYI!");
        }
        offset += NurPacket.PacketDword(data, offset, this.mRbAddress);
        offset += NurPacket.PacketByte(data, offset, this.mRbWordCount);
        return offset - origOffset;
    }
}
