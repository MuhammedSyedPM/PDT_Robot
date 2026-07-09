// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdVersions extends NurCmd
{
    public static final int CMD = 12;
    private static final int MIN_RESPONSE_BYTES = 7;
    private NurRespVersions mVersions;
    
    public NurRespVersions getResponse() {
        return this.mVersions;
    }
    
    public NurCmdVersions() {
        super(12);
        this.mVersions = null;
    }
    
    @Override
    public void deserializePayload(final byte[] data, final int offset, final int dataLen) throws NurApiException {
        if (dataLen < 7) {
            throw new NurApiException(4103);
        }
        this.mVersions = new NurRespVersions();
        int src = offset;
        if (data[src++] == 65) {
            this.mVersions.isApplicationMode = true;
        }
        else {
            this.mVersions.isApplicationMode = false;
        }
        this.mVersions.primaryVersion = Integer.toString(data[src]) + "." + Integer.toString(data[src + 1]) + "-" + (char)data[src + 2];
        src += 3;
        this.mVersions.secondaryVersion = Integer.toString(data[src]) + "." + Integer.toString(data[src + 1]) + "-" + (char)data[src + 2];
    }
}
