// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdRfSettings extends NurCmd
{
    public static final int CMD = 100;
    private boolean mGetRfSettings;
    private byte[] mResp;
    private byte[] mBuffer;
    private int mBufferLength;
    
    public byte[] getResponse() {
        return this.mResp;
    }
    
    public NurCmdRfSettings() {
        super(100);
        this.mGetRfSettings = false;
        this.mGetRfSettings = true;
    }
    
    public NurCmdRfSettings(final byte[] buffer, final int bufferLength) {
        super(100, 0, bufferLength);
        this.mGetRfSettings = false;
        this.mBuffer = buffer;
        this.mBufferLength = bufferLength;
    }
    
    @Override
    public int serializePayload(final byte[] data, final int offset) {
        if (this.mGetRfSettings) {
            return 0;
        }
        return NurPacket.PacketBytes(data, offset, this.mBuffer, this.mBufferLength);
    }
    
    @Override
    public void deserializePayload(final byte[] data, final int offset, final int dataLen) {
        System.arraycopy(data, offset, this.mResp = new byte[dataLen], 0, dataLen);
    }
}
