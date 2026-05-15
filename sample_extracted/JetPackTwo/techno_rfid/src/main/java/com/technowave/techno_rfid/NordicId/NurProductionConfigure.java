// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurProductionConfigure
{
    public boolean usbTableReader;
    public boolean ethTableReader;
    public String serial;
    public String altSerial;
    public String name;
    public String fccId;
    public String hwVer;
    
    public NurProductionConfigure() {
        this.usbTableReader = false;
        this.ethTableReader = false;
        this.serial = "";
        this.altSerial = "";
        this.name = "";
        this.fccId = "";
        this.hwVer = "";
    }
    
    public NurProductionConfigure(final boolean usbTableReader, final boolean ethTableReader, final String serial, final String altSerial, final String name, final String fccId, final String hwVer) {
        this.usbTableReader = false;
        this.ethTableReader = false;
        this.serial = "";
        this.altSerial = "";
        this.name = "";
        this.fccId = "";
        this.hwVer = "";
        this.usbTableReader = usbTableReader;
        this.ethTableReader = ethTableReader;
        this.serial = serial;
        this.altSerial = altSerial;
        this.name = name;
        this.fccId = fccId;
        this.hwVer = hwVer;
    }
}
