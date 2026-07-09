// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdNotifyFrequencyHop extends NurCmd
{
    public static final int CMD = 134;
    private NurEventFrequencyHop mResp;
    
    public NurEventFrequencyHop getResponse() {
        return this.mResp;
    }
    
    public NurCmdNotifyFrequencyHop() {
        super(134);
        this.mResp = new NurEventFrequencyHop();
    }
    
    @Override
    public void deserializePayload(final byte[] data, int offset, final int dataLen) {
        this.mResp.regionId = NurPacket.BytesToByte(data, offset++);
        this.mResp.index = NurPacket.BytesToByte(data, offset++);
    }
}
