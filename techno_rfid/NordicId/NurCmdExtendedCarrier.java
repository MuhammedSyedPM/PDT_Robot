// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdExtendedCarrier extends NurCmd
{
    public static final int CMD = 98;
    private int mSetCarrier;
    
    public NurCmdExtendedCarrier(final boolean on) {
        super(98, 0, 4);
        this.mSetCarrier = 0;
        this.mSetCarrier = (on ? 1 : 0);
    }
    
    @Override
    public int serializePayload(final byte[] data, final int offset) {
        return NurPacket.PacketDword(data, offset, this.mSetCarrier);
    }
}
