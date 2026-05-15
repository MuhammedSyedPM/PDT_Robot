// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdNxpEasAlarm extends NurCmd
{
    public static final int CMD = 82;
    private boolean mStreamCmd;
    private boolean mStartStream;
    
    public NurCmdNxpEasAlarm() {
        super(82);
        this.mStreamCmd = false;
        this.mStartStream = false;
    }
    
    public NurCmdNxpEasAlarm(final boolean startStream) {
        super(82, 0, 1);
        this.mStreamCmd = false;
        this.mStartStream = false;
        this.mStreamCmd = true;
        this.mStartStream = startStream;
    }
    
    @Override
    public int serializePayload(final byte[] data, final int offset) throws Exception {
        if (!this.mStreamCmd) {
            return 0;
        }
        return NurPacket.PacketByte(data, offset, this.mStartStream ? 1 : 0);
    }
}
