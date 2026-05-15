// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

class CRC32
{
    static boolean initialized;
    static long[] crctable;
    public static long POLYNOMIAL;
    
    private static void initCRC32() {
        CRC32.initialized = true;
        long h = 1L;
        CRC32.crctable[0] = 0L;
        for (int i = 128; i != 0; i >>= 1) {
            h = (h >> 1 ^ (((h & 0x1L) != 0x0L) ? CRC32.POLYNOMIAL : 0L));
            for (int j = 0; j < 256; j += 2 * i) {
                CRC32.crctable[i + j & 0xFF] = (CRC32.crctable[j & 0xFF] ^ h);
            }
        }
    }
    
    public static long calc(final long crc32, final byte[] buf, final int offset, final int len) {
        if (!CRC32.initialized) {
            initCRC32();
        }
        long crc33 = crc32;
        crc33 ^= 0xFFFFFFFFL;
        for (int n = 0; n < len; ++n) {
            crc33 = ((crc33 >> 8 ^ CRC32.crctable[(int)((crc33 ^ (long)(buf[offset + n] & 0xFF)) & 0xFFL)]) & -1L);
        }
        return crc33 ^ 0xFFFFFFFFL;
    }
    
    static {
        CRC32.initialized = false;
        CRC32.crctable = new long[256];
        CRC32.POLYNOMIAL = 3988292384L;
    }
}
