// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdSetEthConfig extends NurCmd
{
    public static final int CMD = 22;
    private NurEthConfig mEthConfig;
    
    public NurCmdSetEthConfig(final NurEthConfig ethConfig) throws NurApiException {
        super(22, 0, 63);
        if (ethConfig.title.length() > 32) {
            throw new NurApiException(5);
        }
        this.mEthConfig = ethConfig;
    }
    
    @Override
    public int serializePayload(final byte[] data, int offset) throws Exception {
        final int origOffset = offset;
        offset += NurPacket.PacketByte(data, offset, this.mEthConfig.title.length());
        final byte[] arr = new byte[32];
        for (int i = 0; i < this.mEthConfig.title.length(); ++i) {
            arr[i] = (byte)this.mEthConfig.title.charAt(i);
        }
        offset += NurPacket.PacketBytes(data, offset, arr, 32);
        String[] tmp = this.mEthConfig.mask.split("\\.");
        if (tmp.length == 4) {
            for (int i = 0; i < 4; ++i) {
                offset += NurPacket.PacketByte(data, offset, Integer.parseInt(tmp[i]));
            }
        }
        else {
            for (int i = 0; i < 4; ++i) {
                offset += NurPacket.PacketByte(data, offset, 0);
            }
        }
        tmp = this.mEthConfig.gw.split("\\.");
        if (tmp.length == 4) {
            for (int i = 0; i < 4; ++i) {
                offset += NurPacket.PacketByte(data, offset, Integer.parseInt(tmp[i]));
            }
        }
        else {
            for (int i = 0; i < 4; ++i) {
                offset += NurPacket.PacketByte(data, offset, 0);
            }
        }
        offset += NurPacket.PacketByte(data, offset, this.mEthConfig.addrType);
        tmp = this.mEthConfig.staticip.split("\\.");
        if (tmp.length == 4) {
            for (int i = 0; i < 4; ++i) {
                offset += NurPacket.PacketByte(data, offset, Integer.parseInt(tmp[i]));
            }
        }
        else {
            for (int i = 0; i < 4; ++i) {
                offset += NurPacket.PacketByte(data, offset, 0);
            }
        }
        offset += NurPacket.PacketByte(data, offset, this.mEthConfig.serverPort >> 8);
        offset += NurPacket.PacketByte(data, offset, this.mEthConfig.serverPort);
        offset += NurPacket.PacketByte(data, offset, this.mEthConfig.hostMode);
        tmp = this.mEthConfig.hostip.split("\\.");
        if (tmp.length == 4) {
            for (int i = 0; i < 4; ++i) {
                offset += NurPacket.PacketByte(data, offset, Integer.parseInt(tmp[i]));
            }
        }
        else {
            for (int i = 0; i < 4; ++i) {
                offset += NurPacket.PacketByte(data, offset, 0);
            }
        }
        offset += NurPacket.PacketByte(data, offset, this.mEthConfig.hostPort >> 8);
        offset += NurPacket.PacketByte(data, offset, this.mEthConfig.hostPort);
        offset += NurPacket.PacketBytes(data, offset, this.mEthConfig.reserved, 8);
        return offset - origOffset;
    }
}
