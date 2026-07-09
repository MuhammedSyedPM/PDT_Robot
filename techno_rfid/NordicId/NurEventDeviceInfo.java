// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurEventDeviceInfo
{
    public int status;
    public int boardID;
    public int boardType;
    public double temp;
    public int lightADC;
    public int tapADC;
    public int IOstate;
    public int upTime;
    String nurVer;
    String serial;
    String altSerial;
    String nurName;
    String hwVer;
    public int nurAntNum;
    public NurEthConfig eth;
    
    public NurEventDeviceInfo() {
        this.eth = new NurEthConfig();
    }
}
