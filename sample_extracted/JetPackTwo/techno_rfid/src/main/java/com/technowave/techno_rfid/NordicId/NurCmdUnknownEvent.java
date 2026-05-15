// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdUnknownEvent extends NurCmd
{
    public int CMD;
    private NurEventUnknown mResp;
    
    public NurCmdUnknownEvent(final int cmd) {
        super(cmd);
        this.CMD = 0;
        this.mResp = new NurEventUnknown();
        this.CMD = cmd;
        this.mResp.setCommand(this.CMD);
    }
    
    public NurEventUnknown getResponse() {
        return this.mResp;
    }
    
    @Override
    public void setStatus(final int flags, final int status) {
        super.setStatus(flags, status);
        this.mResp.setStatus(flags, status);
    }
    
    @Override
    public void deserializePayload(final byte[] data, final int offset, final int dataLen) {
        this.mResp.setData(data, offset, dataLen);
    }
}
