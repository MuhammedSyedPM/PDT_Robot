// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurBinaryHeader
{
    public static final int NUR_FILE_CS_SIZE = 16;
    public static final int N_RESERVED_DW = 16;
    public int id;
    public int szHdr;
    public int hdrVersion;
    public int type;
    public int arch;
    public int vMajor;
    public int vMinor;
    public int devbuild;
    public int verRsvrd;
    public int flags;
    public int first;
    public int count;
    public int payloadCRC;
    public int actualsize;
    public byte[] checksum;
    public int[] resrvd;
    
    public NurBinaryHeader() {
        this.checksum = new byte[16];
        this.resrvd = new int[16];
    }
}
