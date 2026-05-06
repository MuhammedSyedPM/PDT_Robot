// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurRespCustomExchange
{
    private int mTagError;
    private byte[] mTagBytes;
    
    public int getTagError() {
        return this.mTagError;
    }
    
    public byte[] getTagBytes() {
        return this.mTagBytes;
    }
    
    public NurRespCustomExchange(final int tagError, final byte[] tagBytes) {
        this.mTagError = 0;
        this.mTagBytes = null;
        this.mTagError = tagError;
        if (tagBytes != null) {
            System.arraycopy(tagBytes, 0, this.mTagBytes = new byte[tagBytes.length], 0, tagBytes.length);
        }
    }
}
