// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdNotifyAutotune extends NurCmd
{
    public static final int CMD = 141;
    private NurEventAutotune mResp;
    
    public NurEventAutotune getResponse() {
        return this.mResp;
    }
    
    protected NurCmdNotifyAutotune() {
        super(141);
        this.mResp = new NurEventAutotune();
    }
    
    @Override
    public void deserializePayload(final byte[] data, final int offset, final int dataLen) {
        this.mResp.cap1 = NurPacket.BytesToByte(data, offset);
        this.mResp.cap2 = NurPacket.BytesToByte(data, offset + 1);
        this.mResp.reflPower_dBm = NurPacket.BytesToDword(data, offset + 2);
        this.mResp.antenna = NurPacket.BytesToByte(data, offset + 6);
        this.mResp.freqKhz = NurPacket.BytesToDword(data, offset + 7);
    }
}
