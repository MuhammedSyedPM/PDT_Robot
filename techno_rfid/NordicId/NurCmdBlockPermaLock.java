// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdBlockPermaLock extends NurCmd
{
    private static final int CMD = 68;
    private boolean mRead;
    private NurRespPermalock mResp;
    private boolean mCbSecured;
    private int mCbPwd;
    private NurRWSingulationBlock mSb;
    private int mRbBank;
    private int mRbAddress;
    private int mRbWordCount;
    private short[] mLockWords;
    
    public NurCmdBlockPermaLock(final int passwd, final boolean secured, final int sBank, final int sAddress, final int sMaskBitLength, final byte[] sMask, final int rdBank, final int rdAddress, final int rdBlockCount) throws NurApiException {
        super(68, 0, 20 + sMaskBitLength);
        this.mRead = false;
        this.mResp = new NurRespPermalock();
        this.mLockWords = null;
        this.mRead = true;
        this.mSb = new NurRWSingulationBlock(sBank, sAddress, sMaskBitLength, sMask);
        this.mCbPwd = passwd;
        this.mCbSecured = secured;
        this.mRbBank = rdBank;
        this.mRbAddress = rdAddress;
        this.mRbWordCount = rdBlockCount;
    }
    
    public NurCmdBlockPermaLock(final int passwd, final boolean secured, final int sBank, final int sAddress, final int sMaskBitLength, final byte[] sMask, final int rdBank, final int rdAddress, final short[] lockWords) throws NurApiException {
        super(68, 0, 20 + sMaskBitLength + lockWords.length * 2);
        this.mRead = false;
        this.mResp = new NurRespPermalock();
        this.mLockWords = null;
        this.mRead = true;
        this.mSb = new NurRWSingulationBlock(sBank, sAddress, sMaskBitLength, sMask);
        this.mCbPwd = passwd;
        this.mCbSecured = secured;
        this.mRbBank = rdBank;
        this.mRbAddress = rdAddress;
        this.mRbWordCount = lockWords.length;
        this.mLockWords = lockWords;
    }
    
    public NurRespPermalock getResponse() {
        return this.mResp;
    }
    
    @Override
    public void deserializePayload(final byte[] data, int offset, final int dataLen) {
        final int status = this.getStatus();
        if (status == 0) {
            this.mResp.wasOk = true;
            if (this.mRead) {
                this.mResp.fromBank = NurPacket.BytesToByte(data, offset++);
                this.mResp.atAddr = NurPacket.BytesToByte(data, offset++);
                int w = NurPacket.BytesToByte(data, offset++);
                this.mResp.lockWords = new short[w];
                for (int i = 0; i < this.mResp.lockWords.length; ++i) {
                    w = NurPacket.BytesToByte(data, offset++);
                    w |= NurPacket.BytesToByte(data, offset++) << 8;
                    this.mResp.lockWords[i] = (short)w;
                }
            }
            return;
        }
        if (status == 66) {
            this.mResp.isTagError = true;
            this.mResp.tagError = NurPacket.BytesToByte(data, offset);
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
        if (!this.mRead) {
            for (final short w : this.mLockWords) {
                offset += NurPacket.PacketWord(data, offset, w);
            }
        }
        return offset - origOffset;
    }
}
