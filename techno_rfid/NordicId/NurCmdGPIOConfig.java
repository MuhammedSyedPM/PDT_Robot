// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdGPIOConfig extends NurCmd
{
    public static final int CMD = 15;
    private NurGPIOConfig[] mGPIOConfig;
    private boolean mGetGPIOConfig;
    
    public NurGPIOConfig[] getResponse() {
        return this.mGPIOConfig;
    }
    
    static int calculatePayloadSize(final int configLength) {
        int ret = 1;
        ret += configLength * 4;
        return ret;
    }
    
    public NurCmdGPIOConfig(final NurGPIOConfig[] config) {
        super(15, 0, calculatePayloadSize(config.length));
        this.mGetGPIOConfig = false;
        this.mGPIOConfig = new NurGPIOConfig[config.length];
        for (int i = 0; i < config.length; ++i) {
            this.mGPIOConfig[i] = config[i];
        }
    }
    
    public NurCmdGPIOConfig(final NurGPIOConfig config) {
        super(15, 0, calculatePayloadSize(1));
        this.mGetGPIOConfig = false;
        (this.mGPIOConfig = new NurGPIOConfig[1])[0] = config;
    }
    
    public NurCmdGPIOConfig() {
        super(15);
        this.mGetGPIOConfig = false;
        this.mGPIOConfig = new NurGPIOConfig[8];
        for (int i = 0; i < 8; ++i) {
            this.mGPIOConfig[i] = new NurGPIOConfig();
        }
        this.mGetGPIOConfig = true;
    }
    
    @Override
    public int serializePayload(final byte[] data, int offset) {
        if (this.mGetGPIOConfig) {
            return 0;
        }
        final int origOffset = offset;
        int gpioFlag = 1;
        int cFlags = 0;
        for (int i = 0; i < this.mGPIOConfig.length; ++i) {
            if (this.mGPIOConfig[i].available) {
                cFlags |= gpioFlag;
            }
            gpioFlag <<= 1;
        }
        offset += NurPacket.PacketByte(data, offset, cFlags);
        for (int i = 0; i < this.mGPIOConfig.length; ++i) {
            if (this.mGPIOConfig[i].available) {
                offset += NurPacket.PacketByte(data, offset, 1);
                offset += NurPacket.PacketByte(data, offset, this.mGPIOConfig[i].type);
                offset += NurPacket.PacketByte(data, offset, this.mGPIOConfig[i].edge);
                offset += NurPacket.PacketByte(data, offset, this.mGPIOConfig[i].action);
            }
        }
        return offset - origOffset;
    }
    
    @Override
    public void deserializePayload(final byte[] data, int offset, final int dataLen) {
        int current = 0;
        int gpioFlag = 1;
        final int retFlags = NurPacket.BytesToByte(data, offset++);
        for (int i = 0; i < this.mGPIOConfig.length; ++i) {
            if ((gpioFlag & retFlags) != 0x0) {
                this.mGPIOConfig[current].available = true;
                this.mGPIOConfig[current].enabled = (NurPacket.BytesToByte(data, offset++) != 0);
                this.mGPIOConfig[current].type = NurPacket.BytesToByte(data, offset++);
                this.mGPIOConfig[current].edge = NurPacket.BytesToByte(data, offset++);
                this.mGPIOConfig[current].action = NurPacket.BytesToByte(data, offset++);
                ++current;
            }
            gpioFlag <<= 1;
        }
    }
}
