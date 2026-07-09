// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdNotifyTriggeredRead extends NurCmd
{
    public static final int CMD = 133;
    private static final int SENSOR_EVENT_FLAG = 128;
    private static final int SENSOR_EVENT_MASK = 127;
    private NurEventTriggeredRead mResp;
    
    public NurEventTriggeredRead getResponse() {
        return this.mResp;
    }
    
    protected NurCmdNotifyTriggeredRead() {
        super(133);
        this.mResp = new NurEventTriggeredRead();
    }
    
    @Override
    public void deserializePayload(final byte[] data, int offset, final int dataLen) {
        final int tmp = NurPacket.BytesToByte(data, offset++);
        this.mResp.sensor = ((tmp & 0x80) != 0x0);
        if (this.mResp.sensor) {
            this.mResp.source = (tmp & 0xFF);
        }
        else {
            this.mResp.source = (tmp & 0x7F);
        }
        if (this.getStatus() == 0) {
            final int ecpLen = dataLen - 4;
            this.mResp.antennaId = NurPacket.BytesToByte(data, offset++);
            this.mResp.rssi = data[offset++];
            this.mResp.scaledRssi = NurPacket.BytesToByte(data, offset++);
            if (ecpLen > 0) {
                System.arraycopy(data, offset, this.mResp.epc = new byte[ecpLen], 0, ecpLen);
                this.mResp.epcStr = NurApi.byteArrayToHexString(this.mResp.epc);
            }
        }
        else {
            this.mResp.antennaId = -1;
            this.mResp.rssi = -127;
            this.mResp.scaledRssi = 0;
        }
    }
}
