// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdBeep extends NurCmd
{
    public static final int CMD = 13;
    private int mFrequency;
    private int mTime;
    private int mDuty;
    private static final int DEFAULT_FREQUENCY = 1000;
    private static final int DEFAULT_TIME = 200;
    private static final int DEFAULT_DUTY = 50;
    private static final int MIN_FREQUENCY = 500;
    private static final int MAX_FREQUENCY = 4000;
    private static final int MIN_TIME = 20;
    private static final int MAX_TIME = 1000;
    private static final int MIN_DUTY = 5;
    private static final int MAX_DUTY = 95;
    
    public NurCmdBeep(final int frequency, final int time, final int duty) {
        super(13, 0, 9);
        if (frequency < 500) {
            this.mFrequency = 500;
        }
        else if (frequency > 4000) {
            this.mFrequency = 4000;
        }
        else {
            this.mFrequency = frequency;
        }
        if (time < 20) {
            this.mTime = 20;
        }
        else if (time > 1000) {
            this.mTime = 1000;
        }
        else {
            this.mTime = time;
        }
        if (duty < 5) {
            this.mDuty = 5;
        }
        else if (duty > 95) {
            this.mDuty = 95;
        }
        else {
            this.mDuty = duty;
        }
    }
    
    public NurCmdBeep() {
        super(13, 0, 9);
        this.mFrequency = 1000;
        this.mTime = 200;
        this.mDuty = 50;
    }
    
    @Override
    public int serializePayload(final byte[] data, int offset) throws Exception {
        final int origOffset = offset;
        offset += NurPacket.PacketDword(data, offset, this.mFrequency);
        offset += NurPacket.PacketDword(data, offset, this.mTime);
        offset += NurPacket.PacketByte(data, offset, this.mDuty);
        final int rc = offset - origOffset;
        return rc;
    }
}
