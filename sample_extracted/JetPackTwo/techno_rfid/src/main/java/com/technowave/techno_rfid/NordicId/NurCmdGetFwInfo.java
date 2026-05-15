// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdGetFwInfo extends NurCmd
{
    public static final int CMD = 30;
    private String respTxt;
    
    public NurCmdGetFwInfo() {
        super(30);
        this.respTxt = "";
    }
    
    public String getResponse() {
        return this.respTxt;
    }
    
    @Override
    public void deserializePayload(final byte[] data, final int offset, final int dataLen) {
        this.respTxt = NurPacket.BytesToString(data, offset, dataLen);
    }
}
