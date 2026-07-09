// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdNotifyAccessory extends NurCmd
{
    public static final int CMD = 144;
    private NurEventAccessory mResp;
    
    public NurEventAccessory getResponse() {
        return this.mResp;
    }
    
    protected NurCmdNotifyAccessory() {
        super(144);
        this.mResp = new NurEventAccessory();
    }
    
    @Override
    public void deserializePayload(final byte[] data, final int offset, final int dataLen) {
        this.mResp.type = ACC_EVENT_TYPE.valueOf(data[offset] & 0xFF);
        if (this.mResp.type == ACC_EVENT_TYPE.Barcode) {
            this.mResp.source = data[offset - 1];
            System.arraycopy(data, offset + 1, this.mResp.dataBuffer = new byte[dataLen - 1], 0, this.mResp.dataBuffer.length);
        }
        else {
            this.mResp.source = data[offset + 1];
            System.arraycopy(data, 4, this.mResp.dataBuffer = new byte[dataLen - 2], 0, dataLen - 2);
        }
    }
}
