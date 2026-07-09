// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdCustomReadTag extends NurCmd
{
    public static final int CMD = 60;
    private byte[] mResponse;
    private boolean mCbSecured;
    private int mCbPwd;
    private NurRWSingulationBlock mSb;
    private int mRbAddress;
    private int mRbWordCount;
    private int mCustomCmd;
    private int mCustomCmdbits;
    private int mCustomBank;
    private int mCustomBankbits;
    
    public NurCmdCustomReadTag(final int rdCmd, final int cmdBits, final int rdBank, final int bankBits, final int passwd, final boolean secured, final int sBank, final int sAddress, final int sMaskBitLength, final byte[] sMask, final int rdAddress, final int rdByteCount) throws NurApiException {
        super(60, 0, 28 + sMaskBitLength);
        if (rdByteCount > 510 || sMaskBitLength > 255) {
            throw new NurApiException(5);
        }
        if (rdByteCount % 2 != 0) {
            throw new NurApiException(4106);
        }
        this.mRbWordCount = rdByteCount / 2;
        this.mRbAddress = rdAddress;
        this.mCbPwd = passwd;
        this.mCbSecured = secured;
        this.mCustomCmd = rdCmd;
        this.mCustomCmdbits = cmdBits;
        this.mCustomBank = rdBank;
        this.mCustomBankbits = bankBits;
        this.mSb = new NurRWSingulationBlock(sBank, sAddress, sMaskBitLength, sMask);
    }
    
    public byte[] getResponse() {
        return this.mResponse;
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
        final int btf = ((cflags & 0x8) != 0x0) ? 19 : 15;
        offset += NurPacket.PacketByte(data, offset, btf);
        offset += NurPacket.PacketDword(data, offset, this.mCustomCmd);
        offset += NurPacket.PacketByte(data, offset, this.mCustomCmdbits);
        offset += NurPacket.PacketDword(data, offset, this.mCustomBank);
        offset += NurPacket.PacketByte(data, offset, this.mCustomBankbits);
        if ((cflags & 0x8) != 0x0) {
            throw new Exception("64 bit address is NYI!");
        }
        offset += NurPacket.PacketDword(data, offset, this.mRbAddress);
        offset += NurPacket.PacketByte(data, offset, this.mRbWordCount);
        return offset - origOffset;
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
}
