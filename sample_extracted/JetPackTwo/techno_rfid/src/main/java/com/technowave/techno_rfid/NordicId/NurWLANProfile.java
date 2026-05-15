// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurWLANProfile
{
    public static final int SECKEY_DATA_LENGTH = 32;
    public static final int INDEX_INVALID = -1;
    public static final int SECTYPE_NONE = -1;
    public static final int EAPMETHOD_NONE = -1;
    public static final int PRIORITY_NONE = -1;
    public int profileIndex;
    public String ssid;
    public byte[] mac;
    public int secType;
    private byte[] secKey;
    public String extUser;
    public String anonUser;
    public int certificateIndex;
    public int eapMethod;
    public int priority;
    
    public NurWLANProfile() {
        this.profileIndex = -1;
        this.ssid = "";
        this.mac = new byte[6];
        this.secType = -1;
        this.secKey = null;
        this.extUser = "";
        this.anonUser = "";
        this.certificateIndex = -1;
        this.eapMethod = -1;
        this.priority = -1;
    }
    
    public void setSecurityKey(final byte[] keyData) throws NurApiException {
        if (keyData != null) {
            if (keyData.length != 32) {
                throw new NurApiException("NurWLANProfile::setSecurityKey", 5);
            }
            System.arraycopy(keyData, 0, this.secKey = new byte[32], 0, 32);
        }
        else {
            this.secKey = null;
        }
    }
    
    public String macString(final boolean dots) {
        if (this.mac == null) {
            return "MAC: (null)";
        }
        if (this.mac.length != 6) {
            return "Invalid MAC length: " + this.mac.length;
        }
        String macStr = "";
        final String sep = dots ? "." : ":";
        macStr = String.format("%02X", this.mac[0]);
        for (int i = 0; i < 6; ++i) {
            macStr = macStr + sep + String.format("%02X", this.mac[0] & 0xFF);
        }
        return macStr;
    }
}
