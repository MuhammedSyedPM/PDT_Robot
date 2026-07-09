// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdSetGPIO extends NurCmd
{
    public static final int CMD = 17;
    private int mGPIO;
    private boolean mState;
    
    public NurCmdSetGPIO(final int gpio, final boolean state) {
        super(17, 0, 2);
        this.mGPIO = gpio;
        this.mState = state;
    }
    
    public NurCmdSetGPIO(final int gpio, final boolean state, final boolean mask) {
        super(17, 0, 2);
        if (!mask) {
            this.mGPIO = 1 << gpio;
        }
        else {
            this.mGPIO = gpio;
        }
        this.mState = state;
    }
    
    @Override
    public int serializePayload(final byte[] data, int offset) {
        final int origOffset = offset;
        offset += NurPacket.PacketByte(data, offset, this.mGPIO);
        offset += NurPacket.PacketByte(data, offset, this.mState ? 1 : 0);
        return offset - origOffset;
    }
}
