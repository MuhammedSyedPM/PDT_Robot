// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdNotifyTTInventory extends NurCmd
{
    public static final int CMD = 131;
    private byte[] mEventByteBuffer;
    
    public NurCmdNotifyTTInventory() {
        super(131);
        this.mEventByteBuffer = null;
    }
    
    public byte[] getResponse() {
        return this.mEventByteBuffer;
    }
    
    @Override
    public void deserializePayload(final byte[] data, final int offset, final int dataLen) {
        if (dataLen > 0) {
            System.arraycopy(data, offset, this.mEventByteBuffer = new byte[dataLen], 0, dataLen);
        }
        else {
            this.mEventByteBuffer = null;
        }
    }
}
