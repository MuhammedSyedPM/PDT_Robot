// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdGetEthConfig extends NurCmd
{
    public static final int CMD = 21;
    private NurEthConfig mEthConfig;
    
    public NurCmdGetEthConfig() {
        super(21);
        this.mEthConfig = new NurEthConfig();
    }
    
    public NurEthConfig getResponse() {
        return this.mEthConfig;
    }
    
    @Override
    public void deserializePayload(final byte[] data, int offset, final int dataLen) {
        int titleLength = 0;
        titleLength = NurPacket.BytesToByte(data, offset++);
        if (titleLength != 0) {
            this.mEthConfig.title = NurPacket.BytesToString(data, offset, titleLength);
            offset += titleLength;
        }
        this.mEthConfig.version = NurPacket.BytesToDword(data, offset);
        offset += 4;
        this.mEthConfig.ip = String.format("%d.%d.%d.%d", data[offset++] & 0xFF, data[offset++] & 0xFF, data[offset++] & 0xFF, data[offset++] & 0xFF);
        this.mEthConfig.mask = String.format("%d.%d.%d.%d", data[offset++] & 0xFF, data[offset++] & 0xFF, data[offset++] & 0xFF, data[offset++] & 0xFF);
        this.mEthConfig.gw = String.format("%d.%d.%d.%d", data[offset++] & 0xFF, data[offset++] & 0xFF, data[offset++] & 0xFF, data[offset++] & 0xFF);
        this.mEthConfig.addrType = NurPacket.BytesToByte(data, offset++);
        this.mEthConfig.staticip = String.format("%d.%d.%d.%d", data[offset++] & 0xFF, data[offset++] & 0xFF, data[offset++] & 0xFF, data[offset++] & 0xFF);
        this.mEthConfig.mac = String.format("%02X:%02X:%02X:%02X:%02X:%02X", data[offset++], data[offset++], data[offset++], data[offset++], data[offset++], data[offset++]);
        this.mEthConfig.serverPort = NurPacket.BytesToDword(data, offset);
        offset += 4;
        this.mEthConfig.hostMode = NurPacket.BytesToByte(data, offset++);
        this.mEthConfig.hostip = String.format("%d.%d.%d.%d", data[offset++] & 0xFF, data[offset++] & 0xFF, data[offset++] & 0xFF, data[offset++] & 0xFF);
        this.mEthConfig.hostPort = NurPacket.BytesToDword(data, offset);
        offset += 4;
        System.arraycopy(data, offset, this.mEthConfig.reserved, 0, 8);
        offset += 8;
    }
}
