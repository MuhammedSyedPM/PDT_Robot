// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class AccConfig
{
    public static final int ACC_EXT_GET_CFG = 1;
    public static final int ACC_EXT_SET_CFG = 2;
    public static final int APP_PERM_SIG = 553883655;
    public static final int APP_PERM_SIG_OLD1 = 553883651;
    public static final int APP_CTX_SIZE = 50;
    public static final int APP_CTX_SIZE_OLD1 = 24;
    public static final int SZ_NAME_FIELD = 32;
    public static final int MAX_NAME_LENGTH = 31;
    public static final int APP_FL_HID_BARCODE = 1;
    public static final int APP_FL_HID_RFID = 2;
    public static final int APP_FL_USE_PEERMGR = 32;
    public static final int APP_FL_HID_USB = 64;
    public static final int CONFIG_FLAG_EXA51 = 1;
    public static final int CONFIG_FLAG_EXA31 = 2;
    public static final int DEV_FEATURE_IMAGER = 4;
    public static final int DEV_FEATURE_WIRELESS_CHG = 8;
    public static final int DEV_FEATURE_VIBRATOR = 16;
    public int signature;
    public int configValue;
    public int flags;
    public int hidBarcodeTimeout;
    public int hidRFIDTimeout;
    public int hidRFIDMaxTags;
    public String name;
    
    public AccConfig() {
        this.signature = -1;
        this.configValue = -1;
        this.flags = 0;
        this.hidBarcodeTimeout = -1;
        this.hidRFIDTimeout = -1;
        this.hidRFIDMaxTags = -1;
        this.name = "No name";
    }
    
    private static void checkSignatureThrow(final int signature, final String message) throws NurApiException {
        if (signature != 553883655 && signature != 553883651) {
            throw new NurApiException(message + "; signature = " + signature, 4103);
        }
    }
    
    private static void checkSignatureThrow(final byte[] source, final String message) throws NurApiException {
        if (source == null || source.length < 4) {
            throw new NurApiException(message + "; source invalid", 4103);
        }
        checkSignatureThrow(NurPacket.BytesToDword(source, 0), message);
    }
    
    public ACC_HID_MODE getHidMode() {
        if ((this.flags & 0x1) != 0x0 && (this.flags & 0x2) != 0x0) {
            return ACC_HID_MODE.EnableRfidBarcode;
        }
        if ((this.flags & 0x1) != 0x0 && (this.flags & 0x2) == 0x0) {
            return ACC_HID_MODE.EnableBarcode;
        }
        if ((this.flags & 0x1) == 0x0 && (this.flags & 0x2) != 0x0) {
            return ACC_HID_MODE.EnableRFID;
        }
        return ACC_HID_MODE.Disabled;
    }
    
    public void setHidBarcode(final boolean setHID) {
        if (setHID) {
            this.flags |= 0x1;
        }
        else {
            this.flags &= 0xFFFFFFFE;
        }
    }
    
    public boolean getHidBarCode() {
        return (this.flags & 0x1) != 0x0;
    }
    
    public void setHidRFID(final boolean setHID) {
        if (setHID) {
            this.flags |= 0x2;
        }
        else {
            this.flags &= 0xFFFFFFFD;
        }
    }
    
    public boolean getHidRFID() {
        return (this.flags & 0x2) != 0x0;
    }
    
    public boolean getAllowPairingState() {
        return (this.flags & 0x20) != 0x0;
    }
    
    public void setAllowPairingState(final boolean setAllowPairing) {
        if (setAllowPairing) {
            this.flags |= 0x20;
        }
        else {
            this.flags &= 0xFFFFFFDF;
        }
    }
    
    public boolean getHidUsbState() {
        return (this.flags & 0x40) != 0x0;
    }
    
    public void setHidUsbState(final boolean setHidUsb) {
        if (setHidUsb) {
            this.flags |= 0x40;
        }
        else {
            this.flags &= 0xFFFFFFBF;
        }
    }
    
    public static AccConfig deserializeConfigurationReply(final byte[] reply) throws Exception {
        int sourceOffset = 0;
        checkSignatureThrow(reply, "Accessory extension, getConfig: unknown configuration signature");
        if (reply.length < 50) {
            throw new NurApiException("Accessory config, invalid reply length", 4103);
        }
        final AccConfig cfg = new AccConfig();
        cfg.signature = NurPacket.BytesToDword(reply, sourceOffset);
        sourceOffset += 4;
        cfg.configValue = NurPacket.BytesToDword(reply, sourceOffset);
        sourceOffset += 4;
        cfg.flags = NurPacket.BytesToDword(reply, sourceOffset);
        sourceOffset += 4;
        if (cfg.signature != 553883651) {
            cfg.name = "";
            for (int n = 0; n < 32; ++n) {
                if (reply[sourceOffset + n] == 0) {
                    cfg.name = new String(reply, sourceOffset, n);
                    break;
                }
            }
            sourceOffset += 32;
            cfg.hidBarcodeTimeout = NurPacket.BytesToWord(reply, sourceOffset);
            sourceOffset += 2;
            cfg.hidRFIDTimeout = NurPacket.BytesToWord(reply, sourceOffset);
            sourceOffset += 2;
            cfg.hidRFIDMaxTags = NurPacket.BytesToWord(reply, sourceOffset);
        }
        else {
            cfg.name = "NOT SUPPORTED";
            cfg.hidBarcodeTimeout = NurPacket.BytesToDword(reply, sourceOffset);
            sourceOffset += 4;
            cfg.hidRFIDTimeout = NurPacket.BytesToDword(reply, sourceOffset);
            sourceOffset += 4;
            cfg.hidRFIDMaxTags = NurPacket.BytesToDword(reply, sourceOffset);
        }
        return cfg;
    }
    
    public static byte[] serializeConfiguration(final AccConfig cfg) throws Exception {
        checkSignatureThrow(cfg.signature, "Accessory extension, setConfig: unknown configuration signature");
        int offset = 0;
        byte[] serializedCfg;
        if (cfg.signature == 553883651) {
            serializedCfg = new byte[25];
        }
        else {
            serializedCfg = new byte[51];
        }
        serializedCfg[offset++] = 2;
        offset += NurPacket.PacketDword(serializedCfg, offset, cfg.signature);
        offset += NurPacket.PacketDword(serializedCfg, offset, cfg.configValue);
        offset += NurPacket.PacketDword(serializedCfg, offset, cfg.flags);
        if (cfg.signature != 553883651) {
            final byte[] nameBytes = new byte[32];
            for (int n = 0; n < 32; ++n) {
                nameBytes[n] = 0;
            }
            final int copyLength = (cfg.name.length() > 31) ? 31 : cfg.name.length();
            System.arraycopy(cfg.name.getBytes(), 0, nameBytes, 0, copyLength);
            offset += NurPacket.PacketBytes(serializedCfg, offset, nameBytes);
            offset += NurPacket.PacketWord(serializedCfg, offset, cfg.hidBarcodeTimeout);
            offset += NurPacket.PacketWord(serializedCfg, offset, cfg.hidRFIDTimeout);
            NurPacket.PacketWord(serializedCfg, offset, cfg.hidRFIDMaxTags);
        }
        else {
            offset += NurPacket.PacketDword(serializedCfg, offset, cfg.hidBarcodeTimeout);
            offset += NurPacket.PacketDword(serializedCfg, offset, cfg.hidRFIDTimeout);
            NurPacket.PacketDword(serializedCfg, offset, cfg.hidRFIDMaxTags);
        }
        return serializedCfg;
    }
    
    public static byte[] getQueryCommand() {
        return new byte[] { 1 };
    }
    
    public static AccConfig allocEmpty() {
        final AccConfig newConfig = new AccConfig();
        newConfig.signature = 553883655;
        return newConfig;
    }
    
    public boolean isDeviceEXA51() {
        return (this.configValue & 0x1) != 0x0;
    }
    
    public boolean isDeviceEXA31() {
        return this.hasImagerScanner() && (this.configValue & 0x2) != 0x0;
    }
    
    public boolean isDeviceEXA21() {
        return !this.hasImagerScanner();
    }
    
    public String getDeviceType() {
        if (this.isDeviceEXA21()) {
            return "EXA21";
        }
        if (this.isDeviceEXA31()) {
            return "EXA31";
        }
        if (this.isDeviceEXA51()) {
            return "EXA51";
        }
        return "N/A";
    }
    
    public boolean hasImagerScanner() {
        return (this.configValue & 0x4) != 0x0;
    }
    
    public boolean hasWirelessCharging() {
        return (this.configValue & 0x8) != 0x0;
    }
    
    public boolean hasVibrator() {
        return (this.configValue & 0x10) != 0x0;
    }
}
