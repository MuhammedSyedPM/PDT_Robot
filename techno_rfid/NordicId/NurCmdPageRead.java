// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdPageRead extends NurCmd
{
    public static final int CMD = 114;
    private static final int FLASH_PAGE_SIZE = 256;
    private int mPageToRead;
    private NurRespPageRead mPageRead;
    
    public NurRespPageRead getResponse() {
        return this.mPageRead;
    }
    
    public NurCmdPageRead(final int pageNum) {
        super(114, 0, 2);
        this.mPageToRead = pageNum;
        this.mPageRead = new NurRespPageRead();
    }
    
    @Override
    public int serializePayload(final byte[] data, final int offset) {
        return NurPacket.PacketWord(data, offset, this.mPageToRead);
    }
    
    @Override
    public void deserializePayload(final byte[] data, int offset, final int dataLen) throws NurApiException {
        if (this.status == 0) {
            final int len = dataLen - 6;
            this.mPageRead.pageNum = NurPacket.BytesToWord(data, offset);
            offset += 2;
            this.mOwner.VLog("mPageToRead: " + this.mPageToRead + ", mPageRead.pageNum : " + this.mPageRead.pageNum + ", len: " + len + ", NurBootloader.FLASH_PAGE_SIZE: " + 256);
            if (this.mPageToRead != this.mPageRead.pageNum || len != 256) {
                throw new NurApiException(5);
            }
            System.arraycopy(data, offset, this.mPageRead.data, 0, len);
            offset += len;
            this.mPageRead.pageAddr = NurPacket.BytesToDword(data, offset);
        }
    }
}
