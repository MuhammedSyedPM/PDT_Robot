// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurEventClientInfo
{
    public NurApi nurApi;
    public String ipAdress;
    public int port;
    
    public NurEventClientInfo() {
        this.ipAdress = "";
    }
    
    public NurEventClientInfo(final NurApi nurApi, final String ipAdress, final int port) {
        this.ipAdress = "";
        this.nurApi = nurApi;
        this.ipAdress = ipAdress;
        this.port = port;
    }
}
