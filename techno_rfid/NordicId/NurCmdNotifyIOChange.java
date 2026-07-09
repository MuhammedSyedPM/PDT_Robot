// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdNotifyIOChange extends NurCmd
{
    public static final int CMD = 129;
    private static final int SENSOR_EVENT_FLAG = 128;
    private static final int SENSOR_EVENT_MASK = -129;
    private NurEventIOChange mResp;
    
    public NurEventIOChange getResponse() {
        return this.mResp;
    }
    
    public NurCmdNotifyIOChange() {
        super(129);
        this.mResp = new NurEventIOChange();
    }
    
    @Override
    public void deserializePayload(final byte[] data, int offset, final int dataLen) {
        final int tmp = NurPacket.BytesToByte(data, offset++);
        this.mResp.sensor = ((tmp & 0x80) != 0x0);
        if (this.mResp.sensor) {
            this.mResp.source = (tmp & 0xFF);
        }
        else {
            this.mResp.source = (tmp & 0xFFFFFF7F);
        }
        this.mResp.direction = NurPacket.BytesToByte(data, offset++);
    }
}
