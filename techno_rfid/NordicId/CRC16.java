// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

class CRC16
{
    static boolean initialized;
    static int[] crctable;
    public static int START;
    public static int MSG_CCITT_CRC_POLY;
    
    private static void initCRC16() {
        for (int i = 0; i < 256; ++i) {
            int c = i << 8;
            for (int j = 0; j < 8; ++j) {
                c = (((c & 0x8000) != 0x0) ? (CRC16.MSG_CCITT_CRC_POLY ^ c << 1) : (c << 1));
            }
            CRC16.crctable[i] = (c & 0xFFFF);
        }
    }
    
    public static int calc(final int crc16, final byte[] buffer, final int offset, final int len) {
        if (!CRC16.initialized) {
            CRC16.initialized = true;
            initCRC16();
        }
        int crc17 = crc16;
        for (int n = 0; n < len; ++n) {
            final int i = ((crc17 >> 8 & 0xFF) ^ buffer[offset + n]) & 0xFFFF;
            crc17 = ((crc17 << 8 ^ (CRC16.crctable[i & 0xFF] & 0xFFFF)) & 0xFFFF);
        }
        return crc17 & 0xFFFF;
    }
    
    public static int calc(final byte[] buffer, final int len) {
        return calc(CRC16.START, buffer, 0, len);
    }
    
    public static int calc(final byte[] buffer, final int offset, final int len) {
        return calc(CRC16.START, buffer, offset, len);
    }
    
    static {
        CRC16.initialized = false;
        CRC16.crctable = new int[256];
        CRC16.START = 65535;
        CRC16.MSG_CCITT_CRC_POLY = 4129;
    }
}
