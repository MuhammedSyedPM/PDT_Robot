// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdInvReadConfig extends NurCmd
{
    public static final int CMD = 65;
    private boolean mGetConfig;
    private boolean mControlOnly;
    private NurIRConfig mConfig;
    
    public NurCmdInvReadConfig(final boolean start, final int type, final int bank, final int addr, final int nWords) throws NurApiException {
        super(65, 0, 8);
        this.mGetConfig = false;
        this.mControlOnly = false;
        this.mConfig = new NurIRConfig();
        if (!start) {
            this.mControlOnly = true;
        }
        this.mConfig.IsRunning = start;
        if (start) {
            this.mConfig.irType = type;
            this.mConfig.irBank = bank;
            this.mConfig.irAddr = addr;
            this.mConfig.irWordCount = nWords;
        }
    }
    
    public NurCmdInvReadConfig(final boolean start) {
        super(65, 0, 1);
        this.mGetConfig = false;
        this.mControlOnly = false;
        this.mConfig = new NurIRConfig();
        this.mConfig.IsRunning = start;
        this.mControlOnly = true;
    }
    
    public NurCmdInvReadConfig() {
        super(65);
        this.mGetConfig = false;
        this.mControlOnly = false;
        this.mConfig = new NurIRConfig();
        this.mGetConfig = true;
    }
    
    public NurIRConfig getResponse() {
        return this.mConfig;
    }
    
    @Override
    public int serializePayload(final byte[] data, int offset) throws Exception {
        final int origOffset = offset;
        if (this.mGetConfig) {
            return 0;
        }
        byte ctlValue;
        if (this.mConfig.IsRunning) {
            ctlValue = 1;
        }
        else {
            ctlValue = 0;
        }
        offset += NurPacket.PacketByte(data, offset, ctlValue);
        if (!this.mControlOnly) {
            offset += NurPacket.PacketByte(data, offset, (byte)this.mConfig.irType);
            offset += NurPacket.PacketByte(data, offset, (byte)this.mConfig.irBank);
            offset += NurPacket.PacketDword(data, offset, this.mConfig.irAddr);
            offset += NurPacket.PacketByte(data, offset, (byte)this.mConfig.irWordCount);
        }
        return offset - origOffset;
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
