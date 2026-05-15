// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurRespPageRead
{
    private static final int FLASH_PAGE_SIZE = 256;
    public int pageNum;
    public int pageAddr;
    public byte[] data;
    
    public NurRespPageRead() {
        this.data = new byte[256];
    }
}
