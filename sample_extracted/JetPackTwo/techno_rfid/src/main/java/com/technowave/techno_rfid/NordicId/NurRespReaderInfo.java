// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurRespReaderInfo
{
    public int szResponse;
    public int version;
    public String serial;
    public String altSerial;
    public String name;
    public String fccId;
    public String hwVersion;
    public int swVerMajor;
    public int swVerMinor;
    public char swVerDev;
    public int numGpio;
    public int numSensors;
    public int numRegions;
    public int numAntennas;
    public int maxAntennas;
    public String swVersion;
    
    public NurRespReaderInfo() {
        this.szResponse = 0;
        this.version = 0;
        this.serial = "";
        this.altSerial = "";
        this.name = "";
        this.fccId = "";
        this.hwVersion = "";
        this.swVerMajor = 0;
        this.swVerMinor = 0;
        this.swVerDev = 'X';
        this.numGpio = 0;
        this.numSensors = 0;
        this.numRegions = 0;
        this.numAntennas = 0;
        this.maxAntennas = -1;
        this.swVersion = "";
    }
}
