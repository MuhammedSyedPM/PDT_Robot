// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdNotifyTraceTag extends NurCmd
{
    public static final int CMD = 132;
    private NurEventTraceTag mResp;
    
    public NurEventTraceTag getResponse() {
        return this.mResp;
    }
    
    protected NurCmdNotifyTraceTag() {
        super(132);
        this.mResp = new NurEventTraceTag();
    }
    
    @Override
    public void deserializePayload(final byte[] data, int offset, final int dataLen) {
        if (this.getStatus() == 0) {
            final int epcLen = dataLen - 3;
            this.mResp.rssi = data[offset++];
            this.mResp.scaledRssi = NurPacket.BytesToByte(data, offset++);
            this.mResp.antennaId = NurPacket.BytesToByte(data, offset++);
            if (epcLen > 0) {
                System.arraycopy(data, offset, this.mResp.epc = new byte[epcLen], 0, epcLen);
                this.mResp.epcStr = NurApi.byteArrayToHexString(this.mResp.epc);
            }
        }
        else {
            this.mResp.rssi = -127;
            this.mResp.scaledRssi = 0;
            this.mResp.antennaId = -1;
        }
    }
}
