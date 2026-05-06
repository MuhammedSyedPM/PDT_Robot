// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdProductionCfg extends NurCmd
{
    public static final int CMD = 118;
    private static final int VARIANT_FLAG_USB_TABLE = 1;
    private static final int VARIANT_FLAG_ETH_TABLE = 2;
    private int mProgrammingCode;
    private NurProductionConfigure mCfg;
    
    public NurCmdProductionCfg(final int dwCode) {
        super(118, 0, 4);
        this.mProgrammingCode = dwCode;
        this.mCfg = null;
    }
    
    public NurCmdProductionCfg(final int dwCode, final NurProductionConfigure cfg) throws NurApiException {
        super(118, 0, 117);
        this.mProgrammingCode = dwCode;
        if (cfg.serial.length() <= 16 && cfg.name.length() <= 16 && cfg.fccId.length() <= 48 && cfg.hwVer.length() <= 8) {
            this.mCfg = cfg;
            return;
        }
        throw new NurApiException(5);
    }
    
    @Override
    public int serializePayload(final byte[] data, int offset) throws Exception {
        if (this.mCfg == null) {
            NurPacket.PacketDword(data, offset, this.mProgrammingCode);
            return 4;
        }
        final int origOffset = offset;
        int cFlags = 0;
        final byte[] tmp = new byte[48];
        if (this.mCfg.usbTableReader) {
            cFlags |= 0x1;
        }
        if (this.mCfg.ethTableReader) {
            cFlags |= 0x2;
        }
        offset += NurPacket.PacketDword(data, offset, this.mProgrammingCode);
        offset += NurPacket.PacketDword(data, offset, cFlags);
        for (int i = 0; i < this.mCfg.serial.length(); ++i) {
            tmp[i] = (byte)this.mCfg.serial.charAt(i);
        }
        offset += NurPacket.PacketByte(data, offset, this.mCfg.serial.length());
        NurPacket.PacketBytes(data, offset, tmp, this.mCfg.serial.length());
        offset += 16;
        for (int i = 0; i < this.mCfg.altSerial.length(); ++i) {
            tmp[i] = (byte)this.mCfg.altSerial.charAt(i);
        }
        offset += NurPacket.PacketByte(data, offset, this.mCfg.altSerial.length());
        NurPacket.PacketBytes(data, offset, tmp, this.mCfg.altSerial.length());
        offset += 16;
        for (int i = 0; i < this.mCfg.name.length(); ++i) {
            tmp[i] = (byte)this.mCfg.name.charAt(i);
        }
        offset += NurPacket.PacketByte(data, offset, this.mCfg.name.length());
        NurPacket.PacketBytes(data, offset, tmp, this.mCfg.name.length());
        offset += 16;
        for (int i = 0; i < this.mCfg.fccId.length(); ++i) {
            tmp[i] = (byte)this.mCfg.fccId.charAt(i);
        }
        offset += NurPacket.PacketByte(data, offset, this.mCfg.fccId.length());
        NurPacket.PacketBytes(data, offset, tmp, this.mCfg.fccId.length());
        offset += 48;
        for (int i = 0; i < this.mCfg.hwVer.length(); ++i) {
            tmp[i] = (byte)this.mCfg.hwVer.charAt(i);
        }
        offset += NurPacket.PacketByte(data, offset, this.mCfg.hwVer.length());
        NurPacket.PacketBytes(data, offset, tmp, this.mCfg.hwVer.length());
        offset += 8;
        return offset - origOffset;
    }
    
    @Override
    public void deserializePayload(final byte[] data, int offset, final int dataLen) throws Exception {
        final int errors = NurPacket.BytesToByte(data, offset++);
        if (errors != 0) {
            this.mOwner.ELog("NurCmdProductionCfg, errors: " + errors);
        }
    }
}
