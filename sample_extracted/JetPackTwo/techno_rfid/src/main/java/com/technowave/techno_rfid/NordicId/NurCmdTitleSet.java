// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdTitleSet extends NurCmd
{
    public static final int CMD = 23;
    private String mTitle;
    
    public String getResponse() {
        return this.mTitle;
    }
    
    public NurCmdTitleSet(final String title) throws NurApiException {
        super(23, 0, title.length());
        if (title.length() < 0 || title.length() > 32) {
            throw new NurApiException(5);
        }
        this.mTitle = title;
    }
    
    @Override
    public int serializePayload(final byte[] data, final int offset) throws Exception {
        final byte[] arr = new byte[this.mTitle.length()];
        for (int i = 0; i < this.mTitle.length(); ++i) {
            arr[i] = (byte)this.mTitle.charAt(i);
        }
        return NurPacket.PacketBytes(data, offset, arr, arr.length);
    }
    
    @Override
    public void deserializePayload(final byte[] data, int offset, final int dataLen) throws Exception {
        if (this.status == 0) {
            this.mTitle = NurPacket.BytesToString(data, offset, dataLen);
            offset += this.mTitle.length();
        }
    }
}
