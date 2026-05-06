// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdContCarrier extends NurCmd
{
    public static final int CMD = 113;
    private byte[] mParams;
    private int mParamsLength;
    
    public NurCmdContCarrier(final byte[] params, final int paramsLength) {
        super(113, 0, paramsLength);
        this.mParams = params;
        this.mParamsLength = paramsLength;
    }
    
    @Override
    public int serializePayload(final byte[] data, final int offset) throws Exception {
        return NurPacket.PacketBytes(data, offset, this.mParams, this.mParamsLength);
    }
}
