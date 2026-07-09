// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdNotifyEpcEnum extends NurCmd
{
    public static final int CMD = 138;
    private NurEventEpcEnum mResp;
    
    public NurEventEpcEnum getResponse() {
        return this.mResp;
    }
    
    public NurCmdNotifyEpcEnum() {
        super(138);
        this.mResp = new NurEventEpcEnum();
    }
    
    @Override
    public void deserializePayload(final byte[] data, int offset, final int dataLen) throws Exception {
        final int epcLen = NurPacket.BytesToByte(data, offset++);
        final int tidLen = NurPacket.BytesToByte(data, offset++);
        this.mResp.tidAddress = NurPacket.BytesToByte(data, offset++);
        System.arraycopy(data, offset, this.mResp.lastEPC = new byte[epcLen], 0, epcLen);
        System.arraycopy(data, offset, this.mResp.assignedTID = new byte[tidLen], 0, tidLen);
    }
}
