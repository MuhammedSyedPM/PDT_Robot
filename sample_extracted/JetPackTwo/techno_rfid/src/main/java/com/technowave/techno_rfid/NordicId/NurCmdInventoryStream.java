// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdInventoryStream extends NurCmd
{
    public static final int CMD = 57;
    private boolean mStopStream;
    
    public NurCmdInventoryStream() {
        this(false);
    }
    
    public NurCmdInventoryStream(final boolean stopStream) {
        super(57, 0, stopStream ? 0 : 1);
        this.mStopStream = stopStream;
    }
    
    @Override
    public int serializePayload(final byte[] data, final int offset) {
        this.mOwner.Log(0, "NurCmdInventoryStream " + this.mStopStream);
        if (this.mStopStream) {
            return 0;
        }
        NurPacket.PacketByte(data, offset, 1);
        return 1;
    }
}
