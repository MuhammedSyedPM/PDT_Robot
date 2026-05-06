// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdTitleGet extends NurCmd
{
    public static final int CMD = 24;
    private String mTitle;
    
    public String getResponse() {
        return this.mTitle;
    }
    
    public NurCmdTitleGet() {
        super(24);
    }
    
    @Override
    public void deserializePayload(final byte[] data, int offset, final int dataLen) throws Exception {
        if (this.status == 0) {
            this.mTitle = NurPacket.BytesToString(data, offset, dataLen);
            offset += this.mTitle.length();
        }
    }
}
