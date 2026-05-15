// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdCustom extends NurCmd
{
    private byte[] mDataOut;
    private byte[] mDataIn;
    
    NurCmdCustom(final int cmd, final byte[] dataOut) {
        super(cmd, 0, (dataOut == null) ? 0 : dataOut.length);
        this.mDataOut = null;
        this.mDataIn = null;
        if (dataOut != null) {
            System.arraycopy(dataOut, 0, this.mDataOut = new byte[dataOut.length], 0, dataOut.length);
        }
    }
    
    public byte[] getResponse() throws Exception {
        return this.mDataIn;
    }
    
    @Override
    public int serializePayload(final byte[] data, int offset) throws Exception {
        final int orgOffset = offset;
        if (this.mDataOut != null) {
            offset += NurPacket.PacketBytes(data, offset, this.mDataOut, this.mDataOut.length);
        }
        return offset - orgOffset;
    }
    
    @Override
    public void deserializePayload(final byte[] data, final int offset, final int dataLen) throws Exception {
        if (this.getStatus() == 0) {
            System.arraycopy(data, offset, this.mDataIn = new byte[dataLen], 0, dataLen);
        }
        else {
            this.mDataIn = null;
        }
    }
}
