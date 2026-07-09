// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurEventTriggeredRead
{
    public boolean sensor;
    public int source;
    public int antennaId;
    public int rssi;
    public int scaledRssi;
    public byte[] epc;
    public String epcStr;
    
    public NurEventTriggeredRead() {
        this.epcStr = "";
    }
}
