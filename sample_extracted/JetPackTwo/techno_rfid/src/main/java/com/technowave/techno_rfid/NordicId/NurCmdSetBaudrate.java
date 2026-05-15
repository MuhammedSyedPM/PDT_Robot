// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdSetBaudrate extends NurCmd
{
    public static final int CMD = 32;
    private int currentBaundrate;
    private int mBaudrate;
    
    public int getResponse() {
        return this.currentBaundrate;
    }
    
    public NurCmdSetBaudrate() {
        super(32, 0, 1);
    }
    
    public NurCmdSetBaudrate(final int baudrate) {
        super(32, 0, 1);
        this.mBaudrate = baudrate;
    }
    
    @Override
    public int serializePayload(final byte[] data, final int offset) throws Exception {
        return NurPacket.PacketByte(data, offset, this.mBaudrate);
    }
    
    @Override
    public void deserializePayload(final byte[] data, int offset, final int dataLen) {
        this.currentBaundrate = NurPacket.BytesToByte(data, offset++);
    }
}
