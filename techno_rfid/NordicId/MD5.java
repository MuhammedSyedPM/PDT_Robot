// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

class MD5
{
    static byte[] PADDING;
    
    static int F(final int x, final int y, final int z) {
        return (x & y) | (~x & z);
    }
    
    static int G(final int x, final int y, final int z) {
        return (x & z) | (y & ~z);
    }
    
    static int H(final int x, final int y, final int z) {
        return x ^ y ^ z;
    }
    
    static int I(final int x, final int y, final int z) {
        return y ^ (x | ~z);
    }
    
    static int ROTATE_LEFT(final int x, final int n) {
        return x << n | x >> 32 - n;
    }
    
    static void FF(int a, final int b, final int c, final int d, final int x, final int s, final long ac) {
        a += F(b, c, d) + x + (int)ac;
        a = ROTATE_LEFT(a, s);
        a += b;
    }
    
    static void GG(int a, final int b, final int c, final int d, final int x, final int s, final long ac) {
        a += G(b, c, d) + x + (int)ac;
        a = ROTATE_LEFT(a, s);
        a += b;
    }
    
    static void HH(int a, final int b, final int c, final int d, final int x, final int s, final long ac) {
        a += H(b, c, d) + x + (int)ac;
        a = ROTATE_LEFT(a, s);
        a += b;
    }
    
    static void II(int a, final int b, final int c, final int d, final int x, final int s, final long ac) {
        a += I(b, c, d) + x + (int)ac;
        a = ROTATE_LEFT(a, s);
        a += b;
    }
    
    public static void MD5Init(final MD5_CTX mdContext) {
        mdContext.i[0] = (mdContext.i[1] = 0);
        mdContext.buf[0] = 1732584193;
        mdContext.buf[1] = -271733879;
        mdContext.buf[2] = -1732584194;
        mdContext.buf[3] = 271733878;
    }
    
    public static void MD5Update(final MD5_CTX mdContext, final byte[] inBuf, final int offset, final int inLen) {
        final int[] in = new int[16];
        int mdi = mdContext.i[0] >> 3 & 0x3F;
        if (mdContext.i[0] + (inLen << 3) < mdContext.i[0]) {
            final int[] j = mdContext.i;
            final int n2 = 1;
            ++j[n2];
        }
        final int[] k = mdContext.i;
        final int n3 = 0;
        k[n3] += inLen << 3;
        final int[] l = mdContext.i;
        final int n4 = 1;
        l[n4] += inLen >> 29;
        for (int n = 0; n < inLen; ++n) {
            mdContext.in[mdi++] = inBuf[n + offset];
            if (mdi == 64) {
                for (int i = 0, ii = 0; i < 16; ++i, ii += 4) {
                    in[i] = (mdContext.in[ii + 3] << 24 | mdContext.in[ii + 2] << 16 | mdContext.in[ii + 1] << 8 | mdContext.in[ii]);
                }
                Transform(mdContext.buf, in);
                mdi = 0;
            }
        }
    }
    
    static void MD5Final(final MD5_CTX mdContext) {
        final int[] in = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, mdContext.i[0], mdContext.i[1] };
        final int mdi = mdContext.i[0] >> 3 & 0x3F;
        final int padLen = (mdi < 56) ? (56 - mdi) : (120 - mdi);
        MD5Update(mdContext, MD5.PADDING, 0, padLen);
        for (int i = 0, ii = 0; i < 14; ++i, ii += 4) {
            in[i] = (mdContext.in[ii + 3] << 24 | mdContext.in[ii + 2] << 16 | mdContext.in[ii + 1] << 8 | mdContext.in[ii]);
        }
        Transform(mdContext.buf, in);
        for (int i = 0, ii = 0; i < 4; ++i, ii += 4) {
            mdContext.digest[ii] = (byte)(mdContext.buf[i] & 0xFF);
            mdContext.digest[ii + 1] = (byte)(mdContext.buf[i] >> 8 & 0xFF);
            mdContext.digest[ii + 2] = (byte)(mdContext.buf[i] >> 16 & 0xFF);
            mdContext.digest[ii + 3] = (byte)(mdContext.buf[i] >> 24 & 0xFF);
        }
    }
    
    int toUnsigned(final long val) {
        return NurPacket.BytesToDword(null, 0);
    }
    
    static void Transform(final int[] buf, final int[] in) {
        final int a = buf[0];
        final int b = buf[1];
        final int c = buf[2];
        final int d = buf[3];
        final int S11 = 7;
        final int S12 = 12;
        final int S13 = 17;
        final int S14 = 22;
        FF(a, b, c, d, in[0], S11, 3614090360L);
        FF(d, a, b, c, in[1], S12, 3905402710L);
        FF(c, d, a, b, in[2], S13, 606105819L);
        FF(b, c, d, a, in[3], S14, 3250441966L);
        FF(a, b, c, d, in[4], S11, 4118548399L);
        FF(d, a, b, c, in[5], S12, 1200080426L);
        FF(c, d, a, b, in[6], S13, 2821735955L);
        FF(b, c, d, a, in[7], S14, 4249261313L);
        FF(a, b, c, d, in[8], S11, 1770035416L);
        FF(d, a, b, c, in[9], S12, 2336552879L);
        FF(c, d, a, b, in[10], S13, 4294925233L);
        FF(b, c, d, a, in[11], S14, 2304563134L);
        FF(a, b, c, d, in[12], S11, 1804603682L);
        FF(d, a, b, c, in[13], S12, 4254626195L);
        FF(c, d, a, b, in[14], S13, 2792965006L);
        FF(b, c, d, a, in[15], S14, 1236535329L);
        final int S15 = 5;
        final int S16 = 9;
        final int S17 = 14;
        final int S18 = 20;
        GG(a, b, c, d, in[1], S15, 4129170786L);
        GG(d, a, b, c, in[6], S16, 3225465664L);
        GG(c, d, a, b, in[11], S17, 643717713L);
        GG(b, c, d, a, in[0], S18, 3921069994L);
        GG(a, b, c, d, in[5], S15, 3593408605L);
        GG(d, a, b, c, in[10], S16, 38016083L);
        GG(c, d, a, b, in[15], S17, 3634488961L);
        GG(b, c, d, a, in[4], S18, 3889429448L);
        GG(a, b, c, d, in[9], S15, 568446438L);
        GG(d, a, b, c, in[14], S16, 3275163606L);
        GG(c, d, a, b, in[3], S17, 4107603335L);
        GG(b, c, d, a, in[8], S18, 1163531501L);
        GG(a, b, c, d, in[13], S15, 2850285829L);
        GG(d, a, b, c, in[2], S16, 4243563512L);
        GG(c, d, a, b, in[7], S17, 1735328473L);
        GG(b, c, d, a, in[12], S18, 2368359562L);
        final int S19 = 4;
        final int S20 = 11;
        final int S21 = 16;
        final int S22 = 23;
        HH(a, b, c, d, in[5], S19, 4294588738L);
        HH(d, a, b, c, in[8], S20, 2272392833L);
        HH(c, d, a, b, in[11], S21, 1839030562L);
        HH(b, c, d, a, in[14], S22, 4259657740L);
        HH(a, b, c, d, in[1], S19, 2763975236L);
        HH(d, a, b, c, in[4], S20, 1272893353L);
        HH(c, d, a, b, in[7], S21, 4139469664L);
        HH(b, c, d, a, in[10], S22, 3200236656L);
        HH(a, b, c, d, in[13], S19, 681279174L);
        HH(d, a, b, c, in[0], S20, 3936430074L);
        HH(c, d, a, b, in[3], S21, 3572445317L);
        HH(b, c, d, a, in[6], S22, 76029189L);
        HH(a, b, c, d, in[9], S19, 3654602809L);
        HH(d, a, b, c, in[12], S20, 3873151461L);
        HH(c, d, a, b, in[15], S21, 530742520L);
        HH(b, c, d, a, in[2], S22, 3299628645L);
        final int S23 = 6;
        final int S24 = 10;
        final int S25 = 15;
        final int S26 = 21;
        II(a, b, c, d, in[0], S23, 4096336452L);
        II(d, a, b, c, in[7], S24, 1126891415L);
        II(c, d, a, b, in[14], S25, 2878612391L);
        II(b, c, d, a, in[5], S26, 4237533241L);
        II(a, b, c, d, in[12], S23, 1700485571L);
        II(d, a, b, c, in[3], S24, 2399980690L);
        II(c, d, a, b, in[10], S25, 4293915773L);
        II(b, c, d, a, in[1], S26, 2240044497L);
        II(a, b, c, d, in[8], S23, 1873313359L);
        II(d, a, b, c, in[15], S24, 4264355552L);
        II(c, d, a, b, in[6], S25, 2734768916L);
        II(b, c, d, a, in[13], S26, 1309151649L);
        II(a, b, c, d, in[4], S23, 4149444226L);
        II(d, a, b, c, in[11], S24, 3174756917L);
        II(c, d, a, b, in[2], S25, 718787259L);
        II(b, c, d, a, in[9], S26, 3951481745L);
        final int n = 0;
        buf[n] += a;
        final int n2 = 1;
        buf[n2] += b;
        final int n3 = 2;
        buf[n3] += c;
        final int n4 = 3;
        buf[n4] += d;
    }
    
    static {
        MD5.PADDING = new byte[] { -128, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
    }
}
