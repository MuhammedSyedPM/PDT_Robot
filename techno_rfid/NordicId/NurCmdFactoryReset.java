// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdFactoryReset extends NurCmd
{
    public static final int CMD = 19;
    private int mResetCode;
    
    public NurCmdFactoryReset(final int resetCode) {
        super(19, 0, 4);
        this.mResetCode = resetCode;
    }
    
    @Override
    public int serializePayload(final byte[] data, final int offset) {
        return NurPacket.PacketDword(data, offset, this.mResetCode);
    }
}
