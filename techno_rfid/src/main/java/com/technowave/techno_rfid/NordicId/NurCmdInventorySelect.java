// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdInventorySelect extends NurCmd
{
    public static final int CMD = 50;
    private int mRounds;
    private int mQ;
    private int mSession;
    private boolean mFlags;
    private int mSbBank;
    private int mSbAddress;
    private int mSbMaskBitLength;
    private byte[] mSbMask;
    private NurRespInventory mResp;
    
    public NurRespInventory getResponse() {
        return this.mResp;
    }
    
    public NurCmdInventorySelect(final int rounds, final int Q, final int session, final boolean invertSelect, final int sBank, final int sAddress, final int sMaskBitLength, final byte[] sMask) {
        super(50, 0, 11 + sMaskBitLength);
        this.mResp = new NurRespInventory();
        this.mRounds = rounds;
        this.mQ = Q;
        this.mSession = session;
        this.mFlags = invertSelect;
        this.mSbBank = sBank;
        this.mSbAddress = sAddress;
        this.mSbMaskBitLength = sMaskBitLength;
        this.mSbMask = sMask;
    }
    
    @Override
    public int serializePayload(final byte[] data, int offset) throws NurApiException {
        final int btf = 6;
        final int origOffset = offset;
        offset += NurPacket.PacketByte(data, offset, this.mQ);
        offset += NurPacket.PacketByte(data, offset, this.mSession);
        offset += NurPacket.PacketByte(data, offset, this.mRounds);
        offset += NurPacket.PacketByte(data, offset, btf);
        offset += NurPacket.PacketByte(data, offset, this.mSbBank);
        offset += NurPacket.PacketByte(data, offset, this.mFlags ? 1 : 0);
        offset += NurPacket.PacketDword(data, offset, this.mSbAddress);
        offset += NurPacket.PacketByte(data, offset, this.mSbMaskBitLength);
        offset += NurPacket.PacketBytes(data, offset, this.mSbMask);
        return offset - origOffset;
    }
    
    @Override
    public void deserializePayload(final byte[] data, int offset, final int dataLen) {
        if (this.status != 0 && this.status != 32) {
            return;
        }
        if (this.status == 32) {
            this.status = 0;
        }
        this.mResp.numTagsFound = NurPacket.BytesToWord(data, offset);
        offset += 2;
        this.mResp.numTagsMem = NurPacket.BytesToWord(data, offset);
        offset += 2;
        this.mResp.roundsDone = NurPacket.BytesToByte(data, offset++);
        this.mResp.collisions = NurPacket.BytesToWord(data, offset);
        offset += 2;
        this.mResp.Q = NurPacket.BytesToByte(data, offset);
    }
}
