// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdInvReadControl extends NurCmd
{
    public static final int CMD = 65;
    private boolean mStart;
    private NurIRConfig mConfig;
    
    public NurCmdInvReadControl(final boolean start) {
        super(65, 0, 1);
        this.mStart = false;
        this.mConfig = new NurIRConfig();
        this.mStart = start;
    }
    
    public NurIRConfig getResponse() {
        return this.mConfig;
    }
    
    @Override
    public int serializePayload(final byte[] data, int offset) {
        final byte ctlValue = (byte)(this.mStart ? 1 : 0);
        offset += NurPacket.PacketByte(data, offset, ctlValue);
        return 1;
    }
    
    @Override
    public void deserializePayload(final byte[] data, int offset, final int dataLen) throws NurApiException {
        if (this.status != 0) {
            throw new NurApiException(this.status);
        }
        if (NurPacket.BytesToByte(data, offset) == 0) {
            this.mConfig.IsRunning = false;
        }
        else {
            this.mConfig.IsRunning = true;
        }
        ++offset;
        this.mConfig.irType = NurPacket.BytesToByte(data, offset);
        ++offset;
        this.mConfig.irBank = NurPacket.BytesToByte(data, offset);
        ++offset;
        this.mConfig.irAddr = NurPacket.BytesToDword(data, offset);
        offset += 4;
        this.mConfig.irWordCount = NurPacket.BytesToByte(data, offset);
    }
}
