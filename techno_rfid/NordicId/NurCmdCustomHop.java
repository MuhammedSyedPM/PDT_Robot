// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdCustomHop extends NurCmd
{
    public static final int CMD = 41;
    private NurCustomHopTable mResp;
    private NurCustomHopTable mParams;
    private boolean mGetCustomHopTable;
    
    public NurCustomHopTable getResponse() {
        return this.mResp;
    }
    
    public NurCmdCustomHop(final int[] freqs, final int nChan, final int chTime, final int pauseTime, final int lf, final int Tari) throws NurApiException {
        super(41, 0, 20 + 4 * nChan);
        this.mGetCustomHopTable = false;
        if (nChan == 0 || nChan > 100 || pauseTime > 1000 || (lf != 160000 && lf != 256000 && lf != 320000) || (Tari != 1 && Tari != 2)) {
            throw new NurApiException(5);
        }
        this.mParams = new NurCustomHopTable();
        this.mParams.freqs = freqs;
        if (freqs.length < nChan) {
            this.mParams.nChan = freqs.length;
        }
        else {
            this.mParams.nChan = nChan;
        }
        this.mParams.chTime = chTime;
        this.mParams.pauseTime = pauseTime;
        this.mParams.lf = lf;
        this.mParams.Tari = Tari;
    }
    
    public NurCmdCustomHop() {
        super(41);
        this.mGetCustomHopTable = false;
        this.mResp = new NurCustomHopTable();
        this.mGetCustomHopTable = true;
    }
    
    @Override
    public int serializePayload(final byte[] data, int offset) {
        if (this.mGetCustomHopTable) {
            return 0;
        }
        final int origOffset = offset;
        offset += NurPacket.PacketDword(data, offset, this.mParams.nChan);
        offset += NurPacket.PacketDword(data, offset, this.mParams.chTime);
        offset += NurPacket.PacketDword(data, offset, this.mParams.pauseTime);
        offset += NurPacket.PacketDword(data, offset, this.mParams.lf);
        offset += NurPacket.PacketDword(data, offset, this.mParams.Tari);
        for (int i = 0; i < this.mParams.nChan; ++i) {
            offset += NurPacket.PacketDword(data, offset, this.mParams.freqs[i]);
        }
        return offset - origOffset;
    }
    
    @Override
    public void deserializePayload(final byte[] data, int offset, final int dataLen) {
        this.mResp.nChan = NurPacket.BytesToDword(data, offset);
        offset += 4;
        if (this.mResp.nChan == 0) {
            return;
        }
        this.mResp.freqs = new int[this.mResp.nChan];
        this.mResp.chTime = NurPacket.BytesToDword(data, offset);
        offset += 4;
        this.mResp.pauseTime = NurPacket.BytesToDword(data, offset);
        offset += 4;
        this.mResp.lf = NurPacket.BytesToDword(data, offset);
        offset += 4;
        this.mResp.Tari = NurPacket.BytesToDword(data, offset);
        offset += 4;
        for (int i = 0; i < this.mResp.nChan; ++i) {
            this.mResp.freqs[i] = NurPacket.BytesToDword(data, offset);
            offset += 4;
        }
    }
}
