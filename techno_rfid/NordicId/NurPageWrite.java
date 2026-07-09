// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurPageWrite
{
    public static final int NUR_FLASH_PAGE_SIZE = 256;
    public int pageToWrite;
    public int crc;
    public byte[] data;
    
    public NurPageWrite() {
        this.data = new byte[256];
    }
}
