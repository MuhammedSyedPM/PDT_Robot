// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurWLANStatus
{
    public static final int ROLE_NONE = -1;
    public static final int ROLE_STA = 0;
    public static final int ROLE_AP = 2;
    public static final int ROLE_P2P = 3;
    public static final int ROLE_STA_ERR = -1;
    public static final int ROLE_AP_ERR = -2;
    public static final int ROLE_P2P_ERR = -3;
    public static final int STATUS_DISABLED = 0;
    public static final int STATUS_ENABLED = 1;
    public static final int STATUS_INITIALIZING = 2;
    public static final int STATUS_BIT_CONNECTION = 1;
    public static final int STATUS_BIT_STA_CONNECTED = 2;
    public static final int STATUS_BIT_IP_ACQUIRED = 4;
    public static final int STATUS_BIT_IP_LEASED = 8;
    public static final int STATUS_BIT_CONNECTION_FAILED = 16;
    public int wlanStatus;
    public int role;
    public int connectionStatus;
    public int profileCount;
    public int lastError;
    public int chipId;
    public String fwVersion;
    public String phyVersion;
    public String nwpVersion;
    public String hostDrvVersion;
    public int romVersion;
    public int ip;
    public int gwIP;
    public byte[] mac;
    public String ssid;
    public byte[] connectionMAC;
    public int scanTime;
    public int rssiMgmnt;
    public int rssiDataCtl;
    public byte[] reserved;
    
    public NurWLANStatus() {
        this.wlanStatus = 0;
        this.role = -1;
        this.connectionStatus = 0;
        this.profileCount = 0;
        this.lastError = 0;
        this.chipId = 0;
        this.fwVersion = "";
        this.phyVersion = "";
        this.nwpVersion = "";
        this.hostDrvVersion = "";
        this.romVersion = 0;
        this.ip = 0;
        this.gwIP = 0;
        this.mac = new byte[6];
        this.ssid = "";
        this.connectionMAC = null;
        this.scanTime = 0;
        this.rssiMgmnt = 0;
        this.rssiDataCtl = 0;
        this.reserved = new byte[10];
    }
    
    private String macString(final byte[] macBytes, final boolean dots) {
        if (macBytes == null) {
            return "MAC: (null)";
        }
        if (macBytes.length != 6) {
            return "Invalid MAC length: " + macBytes.length;
        }
        String macStr = "";
        final String sep = dots ? "." : ":";
        macStr = String.format("%02X", macBytes[0]);
        for (int i = 0; i < 6; ++i) {
            macStr = macStr + sep + String.format("%02X", macBytes[0] & 0xFF);
        }
        return macStr;
    }
    
    public String getMACString(final boolean dots) {
        return this.macString(this.mac, dots);
    }
    
    public String getRemoteMACString(final boolean dots) {
        return this.macString(this.connectionMAC, dots);
    }
}
