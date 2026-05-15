// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdInventory extends NurCmd
{
    public static final int CMD = 49;
    private NurRespInventory mResp;
    private int mQ;
    private int mSession;
    private int mRounds;
    private boolean mParams;
    
    public NurCmdInventory() {
        super(49);
        this.mResp = new NurRespInventory();
        this.mParams = false;
        this.mParams = true;
    }
    
    public NurCmdInventory(final int Q, final int session, final int rounds) throws NurApiException {
        super(49, 0, 3);
        this.mResp = new NurRespInventory();
        this.mParams = false;
        if (Q < 0 || Q > 16 || session < 0 || session > 3) {
            throw new NurApiException(5);
        }
        this.mQ = Q;
        this.mSession = session;
        this.mRounds = rounds;
    }
    
    public NurRespInventory getResponse() {
        return this.mResp;
    }
    
    @Override
    public int serializePayload(final byte[] data, int offset) throws Exception {
        if (this.mParams) {
            return 0;
        }
        final int origOffset = offset;
        offset += NurPacket.PacketByte(data, offset, this.mQ);
        offset += NurPacket.PacketByte(data, offset, this.mSession);
        offset += NurPacket.PacketByte(data, offset, this.mRounds);
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
