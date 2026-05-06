// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdStoreSetup extends NurCmd
{
    public static final int CMD = 40;
    private int mFlags;
    
    public NurCmdStoreSetup(final int flags) {
        super(40, 0, 1);
        this.mFlags = flags;
    }
    
    @Override
    public int serializePayload(final byte[] data, final int offset) throws Exception {
        return NurPacket.PacketByte(data, offset, this.mFlags);
    }
}
