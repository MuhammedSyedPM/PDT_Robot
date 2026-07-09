// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

class MD5_CTX
{
    public int[] i;
    public int[] buf;
    public byte[] in;
    public byte[] digest;
    
    public MD5_CTX() {
        this.i = new int[2];
        this.buf = new int[4];
        this.in = new byte[64];
        this.digest = new byte[16];
    }
}
