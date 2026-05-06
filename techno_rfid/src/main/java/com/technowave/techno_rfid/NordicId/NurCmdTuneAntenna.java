// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdTuneAntenna extends NurCmd
{
    public static final int CMD = 102;
    private int mType;
    private int mBand;
    private int mAntenna;
    private boolean mUserSave;
    private boolean mSingleTune;
    private NurTuneResponse[] mResp;
    
    public NurTuneResponse[] getResponse() {
        return this.mResp;
    }
    
    public NurCmdTuneAntenna(final int antenna, final boolean wideTune, final boolean userSave) throws NurApiException {
        super(102, 0, 28);
        this.mType = 0;
        this.mBand = -1;
        this.mAntenna = 0;
        this.mUserSave = false;
        this.mSingleTune = false;
        this.mResp = null;
        if (antenna < 0 || antenna >= 32) {
            throw new NurApiException(5);
        }
        this.mResp = new NurTuneResponse[6];
        if (wideTune) {
            this.mType = 2;
        }
        this.mAntenna = antenna;
        this.mUserSave = userSave;
    }
    
    public NurCmdTuneAntenna(final boolean wideTune, final boolean userSave) {
        super(102, 0, 28);
        this.mType = 0;
        this.mBand = -1;
        this.mAntenna = 0;
        this.mUserSave = false;
        this.mSingleTune = false;
        this.mResp = null;
        this.mResp = new NurTuneResponse[6];
        if (wideTune) {
            this.mType = 2;
        }
        this.mUserSave = userSave;
    }
    
    public NurCmdTuneAntenna(final int antenna, final int band, final boolean wideTune, final boolean userSave) throws NurApiException {
        super(102, 0, 28);
        this.mType = 0;
        this.mBand = -1;
        this.mAntenna = 0;
        this.mUserSave = false;
        this.mSingleTune = false;
        this.mResp = null;
        if (antenna < 0 || antenna >= 32 || band < 0 || band > 5) {
            throw new NurApiException(5);
        }
        this.mResp = new NurTuneResponse[1];
        if (wideTune) {
            this.mType = 2;
        }
        else {
            this.mType = 1;
        }
        this.mAntenna = antenna;
        this.mBand = band;
        this.mUserSave = userSave;
        this.mSingleTune = true;
    }
    
    @Override
    public int serializePayload(final byte[] data, int offset) {
        final byte[] tuneCode = { 0, 0, 0, 0, 0, 0, 0, 0 };
        final int origOffset = offset;
        offset += NurPacket.PacketDword(data, offset, this.mType);
        offset += NurPacket.PacketDword(data, offset, this.mAntenna);
        offset += NurPacket.PacketDword(data, offset, this.mBand);
        offset += NurPacket.PacketDword(data, offset, this.mUserSave ? 1 : 0);
        offset += NurPacket.PacketDword(data, offset, -100);
        offset += NurPacket.PacketBytes(data, offset, tuneCode, tuneCode.length);
        return offset - origOffset;
    }
    
    @Override
    public void deserializePayload(final byte[] data, int offset, final int dataLen) throws Exception {
        if (this.status != 0) {
            throw new NurApiException(this.status);
        }
        final int ant = NurPacket.BytesToDword(data, offset);
        if (!this.mSingleTune) {
            int f = 850000;
            offset += 16;
            for (int i = 0; i < 6; ++i) {
                final int I = NurPacket.BytesToDword(data, offset);
                offset += 4;
                final int Q = NurPacket.BytesToDword(data, offset);
                offset += 4;
                final int idBm = NurPacket.BytesToDword(data, offset);
                offset += 4;
                this.mResp[i] = new NurTuneResponse(ant, f, I, Q, idBm);
                f += 20000;
            }
        }
        else {
            offset += 4;
            final int f = 850000 + NurPacket.BytesToDword(data, offset) * 20000;
            offset += 4;
            final int I = NurPacket.BytesToDword(data, offset);
            offset += 4;
            final int Q = NurPacket.BytesToDword(data, offset);
            offset += 4;
            final int idBm = NurPacket.BytesToDword(data, offset);
            this.mResp[0] = new NurTuneResponse(ant, f, I, Q, idBm);
        }
    }
}
