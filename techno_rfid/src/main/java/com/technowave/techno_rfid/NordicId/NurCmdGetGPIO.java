// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdGetGPIO extends NurCmd
{
    public static final int CMD = 16;
    private int mSetupFlags;
    private int mRespFlags;
    private NurRespGPIOStatus mResp;
    
    public NurRespGPIOStatus getResponse() {
        return this.mResp;
    }
    
    public NurCmdGetGPIO(final int flags) {
        super(16, 0, 1);
        this.mResp = new NurRespGPIOStatus();
        this.mSetupFlags = 1 << flags;
    }
    
    @Override
    public int serializePayload(final byte[] data, final int offset) {
        return NurPacket.PacketByte(data, offset, this.mSetupFlags);
    }
    
    @Override
    public void deserializePayload(final byte[] data, int offset, final int dataLen) {
        this.mRespFlags = NurPacket.BytesToByte(data, offset++);
        if (this.mRespFlags > 0) {
            ++offset;
            this.mResp.enabled = (NurPacket.BytesToByte(data, offset++) != 0);
            this.mResp.type = NurPacket.BytesToByte(data, offset++);
            this.mResp.state = (NurPacket.BytesToByte(data, offset++) != 0);
        }
    }
}
