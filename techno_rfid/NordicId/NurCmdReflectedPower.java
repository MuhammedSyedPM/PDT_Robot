// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdReflectedPower extends NurCmd
{
    public static final int CMD = 96;
    public static final int CMD_EX = 103;
    private boolean mIsExtended;
    private int mFrequency;
    private ReflectedPower mReflectedPower;
    
    public NurCmdReflectedPower() {
        super(96);
        this.mIsExtended = false;
        this.mFrequency = 0;
        this.mReflectedPower = null;
    }
    
    public NurCmdReflectedPower(final int frequency) {
        super(103, 0, 4);
        this.mIsExtended = false;
        this.mFrequency = 0;
        this.mReflectedPower = null;
        this.mIsExtended = true;
    }
    
    public float getResponse() {
        if (this.mReflectedPower != null) {
            return this.mReflectedPower.mReflectedPower;
        }
        return 0.0f;
    }
    
    public ReflectedPower getResponseEx() {
        return this.mReflectedPower;
    }
    
    @Override
    public int serializePayload(final byte[] data, final int offset) throws Exception {
        int orgOffset = offset;
        if (this.mIsExtended) {
            orgOffset += NurPacket.PacketDword(data, offset, this.mFrequency);
        }
        return orgOffset - offset;
    }
    
    @Override
    public void deserializePayload(final byte[] data, int offset, final int dataLen) {
        int f = 0;
        final int I = NurPacket.BytesToDword(data, offset);
        offset += 4;
        final int Q = NurPacket.BytesToDword(data, offset);
        offset += 4;
        final int D = NurPacket.BytesToDword(data, offset);
        offset += 4;
        if (this.mIsExtended) {
            f = NurPacket.BytesToDword(data, offset);
        }
        this.mReflectedPower = new ReflectedPower(I, Q, D, f);
    }
}
