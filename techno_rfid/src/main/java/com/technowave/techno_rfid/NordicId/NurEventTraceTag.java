// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurEventTraceTag
{
    public int rssi;
    public int scaledRssi;
    public int antennaId;
    public byte[] epc;
    public String epcStr;
    
    public NurEventTraceTag() {
        this.epcStr = "";
    }
}
