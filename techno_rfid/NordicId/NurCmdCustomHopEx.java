// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdCustomHopEx extends NurCmd
{
    public static final int CMD = 42;
    private NurCustomHopTableEx mResp;
    private NurCustomHopTableEx mParams;
    private boolean mGetCustomHopTable;
    
    public NurCustomHopTableEx getResponse() {
        return this.mResp;
    }
    
    public NurCmdCustomHopEx(final int[] freqs, final int nChan, final int chTime, final int pauseTime, final int lf, final int Tari, final int lbtThresh, final int TxLevel) throws NurApiException {
        super(42, 0, 28 + 4 * nChan);
        this.mGetCustomHopTable = false;
        if (freqs == null || nChan == 0 || freqs.length != nChan || nChan > 100 || pauseTime > 1000 || (lf != 160000 && lf != 256000 && lf != 320000) || (Tari != 1 && Tari != 2) || TxLevel > 19 || lbtThresh < -90) {
            throw new NurApiException(5);
        }
        this.mParams = new NurCustomHopTableEx(freqs, chTime, pauseTime, lf, Tari, lbtThresh, TxLevel);
    }
    
    public NurCmdCustomHopEx() {
        super(42);
        this.mGetCustomHopTable = false;
        this.mResp = new NurCustomHopTableEx();
        this.mGetCustomHopTable = true;
    }
    
    @Override
    public int serializePayload(final byte[] data, int offset) {
        if (this.mGetCustomHopTable) {
            return 0;
        }
        final int origOffset = offset;
        offset += NurPacket.PacketDword(data, offset, this.mParams.freqs.length);
        offset += NurPacket.PacketDword(data, offset, this.mParams.chTime);
        offset += NurPacket.PacketDword(data, offset, this.mParams.pauseTime);
        offset += NurPacket.PacketDword(data, offset, this.mParams.lf);
        offset += NurPacket.PacketDword(data, offset, this.mParams.Tari);
        offset += NurPacket.PacketDword(data, offset, this.mParams.lbtThresh);
        offset += NurPacket.PacketDword(data, offset, this.mParams.TxLevel);
        for (final int f : this.mParams.freqs) {
            offset += NurPacket.PacketDword(data, offset, f);
        }
        return offset - origOffset;
    }
    
    @Override
    public void deserializePayload(final byte[] data, int offset, final int dataLen) throws Exception {
        this.mResp.nChan = NurPacket.BytesToDword(data, offset);
        offset += 4;
        if (this.mResp.nChan == 0) {
            return;
        }
        if (dataLen != 28 + 4 * this.mResp.nChan) {
            throw new NurApiException("NurCmdCustomHopEx : invalid response length");
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
        this.mResp.lbtThresh = NurPacket.BytesToDword(data, offset);
        offset += 4;
        this.mResp.TxLevel = NurPacket.BytesToDword(data, offset);
        offset += 4;
        for (int i = 0; i < this.mResp.nChan; ++i) {
            this.mResp.freqs[i] = NurPacket.BytesToDword(data, offset);
            offset += 4;
        }
    }
}
