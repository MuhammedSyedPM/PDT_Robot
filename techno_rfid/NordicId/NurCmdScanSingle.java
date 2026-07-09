// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdScanSingle extends NurCmd
{
    public static final int CMD = 48;
    private NurRespReadData mResp;
    private int mTimeout;
    private static final int MIN_TIMEOUT = 50;
    private static final int MAX_TIMEOUT = 2000;
    
    public NurRespReadData getResponse() {
        return this.mResp;
    }
    
    public NurCmdScanSingle(final int timeout) {
        super(48, 0, 2);
        if (timeout < 50) {
            this.mTimeout = 50;
        }
        else if (timeout > 2000) {
            this.mTimeout = 2000;
        }
        else {
            this.mTimeout = timeout;
        }
        this.mResp = new NurRespReadData();
    }
    
    @Override
    public int serializePayload(final byte[] data, final int offset) throws Exception {
        return NurPacket.PacketWord(data, offset, this.mTimeout);
    }
    
    @Override
    public void deserializePayload(final byte[] data, int offset, final int dataLen) {
        this.mResp.epcLen = dataLen - 3;
        this.mResp.antennaID = NurPacket.BytesToByte(data, offset++);
        this.mResp.rssi = NurPacket.BytesToByte(data, offset++);
        this.mResp.scaledRssi = NurPacket.BytesToByte(data, offset++);
        if (this.mResp.epcLen > 0) {
            System.arraycopy(data, offset, this.mResp.epc = new byte[this.mResp.epcLen], 0, this.mResp.epcLen);
            this.mResp.epcStr = NurApi.byteArrayToHexString(this.mResp.epc);
        }
        else {
            this.mResp.epcStr = "";
        }
    }
}
