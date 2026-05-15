// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdNotifyDebugMsg extends NurCmd
{
    public static final int CMD = 135;
    private String debugMsgBuffer;
    
    public NurCmdNotifyDebugMsg() {
        super(135);
    }
    
    public String getResponse() {
        return this.debugMsgBuffer;
    }
    
    @Override
    public void deserializePayload(final byte[] data, final int offset, final int dataLen) {
        this.debugMsgBuffer = NurPacket.BytesToString(data, offset, dataLen);
    }
}
