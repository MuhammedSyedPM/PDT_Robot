// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdScanChannels extends NurCmd
{
    public static final int CMD = 99;
    private NurRespScanChannelInfo[] mInfo;
    private int mChannel;
    
    public NurRespScanChannelInfo[] getResponse() {
        return this.mInfo;
    }
    
    public NurRespScanChannelInfo getSingleResponse() {
        return this.mInfo[0];
    }
    
    public NurCmdScanChannels() {
        super(99);
        this.mChannel = -1;
    }
    
    public NurCmdScanChannels(final int channel) {
        super(99, 0, 1);
        this.mChannel = -1;
        this.mChannel = channel;
    }
    
    @Override
    public int serializePayload(final byte[] data, final int offset) throws Exception {
        if (this.mChannel != -1) {
            data[offset] = (byte)this.mChannel;
            return 1;
        }
        return 0;
    }
    
    @Override
    public void deserializePayload(final byte[] data, int offset, final int dataLen) throws NurApiException {
        if (dataLen > 0 && dataLen % 12 == 0) {
            final int arraySize = dataLen / 12;
            this.mInfo = new NurRespScanChannelInfo[arraySize];
            for (int i = 0; i < arraySize; ++i) {
                this.mInfo[i] = new NurRespScanChannelInfo();
                final int f = NurPacket.BytesToDword(data, offset);
                this.mInfo[i].freq = f;
                offset += 4;
                final int rssi = NurPacket.BytesToDword(data, offset);
                this.mInfo[i].rssi = rssi;
                offset += 8;
            }
            return;
        }
        throw new NurApiException("Scan channels: unexpected response length " + dataLen + " bytes.");
    }
}
