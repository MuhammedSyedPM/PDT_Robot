// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdGetMode extends NurCmd
{
    public static final int CMD = 4;
    private String mMode;
    
    public NurCmdGetMode() {
        super(4);
    }
    
    public String getResponse() {
        return this.mMode;
    }
    
    @Override
    public void deserializePayload(final byte[] data, final int offset, final int dataLen) {
        this.mMode = NurPacket.BytesToString(data, offset, dataLen);
    }
}
