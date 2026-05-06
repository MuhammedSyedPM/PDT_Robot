// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdMz4Qt extends NurCmd
{
    public static final int CMD = 83;
    private int mPasswd;
    private boolean mWrite;
    private boolean mReduce;
    private boolean mPubMem;
    private NurRespMonzaQT mResp;
    private NurRWSingulationBlock mSb;
    private static final int QT_MEM = 16384;
    private static final int QT_SR = 32768;
    
    public NurRespMonzaQT getResponse() {
        return this.mResp;
    }
    
    public NurCmdMz4Qt(final int passwd, final boolean reduce, final boolean pubMem, final int sBank, final int sAddress, final int sMaskBitLength, final byte[] sMask) {
        super(83, 0, 17 + sMaskBitLength);
        this.mPasswd = passwd;
        this.mWrite = true;
        this.mReduce = reduce;
        this.mPubMem = pubMem;
        this.mSb = new NurRWSingulationBlock(sBank, sAddress, sMaskBitLength, sMask);
    }
    
    public NurCmdMz4Qt(final int passwd, final int sBank, final int sAddress, final int sMaskBitLength, final byte[] sMask) {
        super(83, 0, 15 + sMaskBitLength);
        this.mPasswd = passwd;
        this.mSb = new NurRWSingulationBlock(sBank, sAddress, sMaskBitLength, sMask);
    }
    
    @Override
    public int serializePayload(final byte[] data, int offset) throws Exception {
        final int origOffset = offset;
        int qt = 0;
        int cFlags = 1;
        cFlags |= this.mSb.getFlags();
        offset += NurPacket.PacketByte(data, offset, cFlags);
        offset += NurPacket.PacketDword(data, offset, this.mPasswd);
        offset = this.mSb.serialize(data, offset);
        offset += NurPacket.PacketByte(data, offset, this.mWrite ? 3 : 1);
        offset += NurPacket.PacketByte(data, offset, 0);
        if (this.mWrite) {
            if (this.mReduce) {
                qt |= 0x8000;
            }
            if (this.mPubMem) {
                qt |= 0x4000;
            }
            offset += NurPacket.PacketWord(data, offset, qt);
        }
        return offset - origOffset;
    }
    
    @Override
    public void deserializePayload(final byte[] data, int offset, final int dataLen) throws Exception {
        if (this.status == 0 && !this.mWrite) {
            this.mResp = new NurRespMonzaQT();
            this.mResp.reduce = (NurPacket.BytesToByte(data, offset++) != 0);
            this.mResp.publicMemory = (NurPacket.BytesToByte(data, offset++) != 0);
        }
    }
}
