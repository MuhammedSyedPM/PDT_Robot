// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdReadRegister extends NurCmd
{
    public static final int CMD = 144;
    private NurRespRegister mRegister;
    private int mReg;
    
    public NurRespRegister getResponse() {
        return this.mRegister;
    }
    
    public NurCmdReadRegister() {
        super(144, 0, 1);
        this.mReg = 10;
        this.mRegister = new NurRespRegister();
    }
    
    @Override
    public int serializePayload(final byte[] data, final int offset) throws Exception {
        return NurPacket.PacketByte(data, offset, this.mReg);
    }
    
    @Override
    public void deserializePayload(final byte[] data, int offset, final int dataLen) {
        this.mRegister.register = NurPacket.BytesToByte(data, offset++);
        this.mRegister.value = NurPacket.BytesToByte(data, offset);
    }
}
