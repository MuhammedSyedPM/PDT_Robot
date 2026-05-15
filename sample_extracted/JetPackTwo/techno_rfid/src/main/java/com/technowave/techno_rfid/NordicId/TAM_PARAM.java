// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class TAM_PARAM
{
    public boolean tam2;
    public boolean decrypt;
    public int keyNum;
    public int mpi;
    public int protMode;
    public int offset;
    public int blockCount;
    private byte[] mKey;
    
    public byte[] getKey() {
        final byte[] key = new byte[16];
        System.arraycopy(this.mKey, 0, key, 0, this.mKey.length);
        return key;
    }
    
    public void setKey(final byte[] key) throws NurApiException {
        if (key == null || key.length != 16) {
            throw new NurApiException("TAM_PARAM: invalid key", 5);
        }
        System.arraycopy(key, 0, this.mKey, 0, this.mKey.length);
    }
    
    public TAM_PARAM(final byte[] key) throws NurApiException {
        this();
        this.setKey(key);
    }
    
    public TAM_PARAM() {
        this.tam2 = false;
        this.decrypt = false;
        this.keyNum = 0;
        this.mpi = 0;
        this.protMode = 0;
        this.offset = 0;
        this.blockCount = 1;
        this.mKey = null;
        this.mKey = new byte[16];
    }
}
