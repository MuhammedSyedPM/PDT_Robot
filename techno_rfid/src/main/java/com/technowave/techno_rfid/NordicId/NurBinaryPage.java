// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurBinaryPage
{
    public static final int NUR_FLASH_PAGE_SIZE = 256;
    public int dwCRC;
    public byte[] data;
    
    public NurBinaryPage() {
        this.data = new byte[256];
    }
}
