// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurRespReadData
{
    public int antennaID;
    public int rssi;
    public int scaledRssi;
    public int epcLen;
    public byte[] epc;
    public String epcStr;
    
    public NurRespReadData() {
        this.epcStr = "";
        this.epc = new byte[62];
    }
}
