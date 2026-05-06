// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdFetchTags extends NurCmd
{
    public static final int CMD = 6;
    public static final int CMD_META = 7;
    
    public NurCmdFetchTags() {
        this(true);
    }
    
    public NurCmdFetchTags(final boolean includeMeta) {
        super(includeMeta ? 7 : 6, 0, 1);
    }
    
    @Override
    public int serializePayload(final byte[] data, final int offset) {
        return NurPacket.PacketByte(data, offset, 1);
    }
    
    @Override
    public void deserializePayload(final byte[] data, final int offset, final int dataLen) {
        boolean isMeta = false;
        boolean isIrData = false;
        if (this.command == 7) {
            isMeta = true;
            if ((this.getFlags() & 0x2) != 0x0) {
                isIrData = true;
            }
        }
        this.mOwner.getStorage().parseIdBuffer(this.mOwner, data, offset, dataLen, isMeta, isIrData);
    }
}
