// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdNotifyNxpAlarm extends NurCmd
{
    public static final int CMD = 137;
    private static final int EASALARM_ARMED = 1;
    private static final int EASALARM_STOPPPED = 2;
    private NurEventNxpAlarm mResp;
    
    public NurEventNxpAlarm getResponse() {
        return this.mResp;
    }
    
    public NurCmdNotifyNxpAlarm() {
        super(137);
        this.mResp = new NurEventNxpAlarm();
    }
    
    @Override
    public void deserializePayload(final byte[] data, int offset, final int dataLen) throws Exception {
        final int flags = NurPacket.BytesToByte(data, offset++);
        this.mResp.stopped = ((flags & 0x2) != 0x0);
        this.mResp.armed = ((flags & 0x1) != 0x0);
    }
}
