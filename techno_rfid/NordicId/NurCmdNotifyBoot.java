// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdNotifyBoot extends NurCmd
{
    public static final int CMD = 128;
    private String info;
    public boolean needBootSetup;
    
    public NurCmdNotifyBoot() {
        super(128);
        this.info = "";
        this.needBootSetup = true;
    }
    
    public String getResponse() {
        return this.info;
    }
    
    @Override
    public void deserializePayload(final byte[] data, final int offset, final int dataLen) {
        this.info = NurPacket.BytesToString(data, offset, dataLen);
    }
}
