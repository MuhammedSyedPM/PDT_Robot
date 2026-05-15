// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdMCUArch extends NurCmd
{
    public static final int CMD = 10;
    private int mRequiredArch;
    private NurRespMCUArch mcuArch;
    
    public NurRespMCUArch getResponse() {
        return this.mcuArch;
    }
    
    public NurCmdMCUArch(final int requiredArch) {
        super(10, 0, 4);
        this.mRequiredArch = requiredArch;
        this.mcuArch = new NurRespMCUArch();
    }
    
    @Override
    public int serializePayload(final byte[] data, final int offset) throws Exception {
        return NurPacket.PacketDword(data, offset, this.mRequiredArch);
    }
    
    @Override
    public void deserializePayload(final byte[] data, int offset, final int dataLen) throws Exception {
        this.mcuArch.error = this.status;
        if (this.status == 0 || this.status == 5) {
            this.mcuArch.actualArch = NurPacket.BytesToWord(data, offset);
            offset += 4;
            if (this.mcuArch.actualArch == this.mRequiredArch && this.status != 0) {
                this.mOwner.ELog(String.format("NurApiConfirmMCUArch() amazing error, the architectures 0x%d08lX (given) and 0x%d08lX (read) are identical!", this.mRequiredArch, this.mcuArch.actualArch));
                this.mcuArch.error = 0;
            }
        }
    }
}
