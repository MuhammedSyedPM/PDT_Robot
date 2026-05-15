// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdPing extends NurCmd
{
    public static final int CMD = 1;
    private int mHostFlags;
    private String respTxt;
    
    public NurCmdPing(final int hostFlags) {
        super(1, 0, 4);
        this.mHostFlags = 0;
        this.respTxt = "";
        this.mHostFlags = hostFlags;
    }
    
    public String getResponse() {
        return this.respTxt;
    }
    
    @Override
    public void deserializePayload(final byte[] data, final int offset, final int dataLen) {
        this.respTxt = NurPacket.BytesToString(data, offset, dataLen);
    }
    
    @Override
    public int serializePayload(final byte[] data, int offset) throws Exception {
        final int origOffset = offset;
        offset += NurPacket.PacketDword(data, offset, this.mHostFlags);
        return offset - origOffset;
    }
}
