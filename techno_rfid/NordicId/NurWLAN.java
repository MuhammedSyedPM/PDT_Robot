// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurWLAN
{
    public static class Target
    {
        public static final byte EEPROM = 0;
        public static final byte GPIO = 1;
        public static final byte SYSTEM = 2;
        public static final byte NETWORK = 3;
        public static final byte FW = 4;
        public static final byte WLAN = 5;
    }
    
    public static class Command
    {
        public static final byte ENABLE = 0;
        public static final byte GET_STATUS = 1;
        public static final byte GET_PROFILE = 2;
        public static final byte ADD_PROFILE = 3;
        public static final byte DEL_PROFILE = 4;
        public static final byte SCAN_ENABLE = 5;
        public static final byte SET_MAC = 6;
        public static final byte FILEWRITE_BEGIN = 7;
        public static final byte FILEWRITE_DATA = 8;
        public static final byte FILEWRITE_END = 9;
        public static final byte FILEDELETE = 10;
        public static final byte FILEREAD = 11;
        public static final byte ADD_ENT_PROFILE = 12;
    }
    
    public static class EAPMethod
    {
        public static final byte METHOD_TLS = 0;
        public static final byte METHOD_TTLS_TLS = 1;
        public static final byte METHOD_TTLS_MSCHAPv2 = 2;
        public static final byte METHOD_TTLS_PSK = 3;
        public static final byte METHOD_PEAP0_TLS = 4;
        public static final byte METHOD_PEAP0_MSCHAPv2 = 5;
        public static final byte METHOD_PEAP0_PSK = 6;
        public static final byte METHOD_PEAP1_TLS = 7;
        public static final byte METHOD_PEAP1_MSCHAPv2 = 8;
        public static final byte METHOD_PEAP1_PSK = 9;
        public static final byte METHOD_FAST_AUTH_PROVISIONING = 10;
        public static final byte METHOD_FAST_UNAUTH_PROVISIONING = 11;
        public static final byte METHOD_FAST_NO_PROVISIONING = 12;
    }
    
    public static class WLANSecurity
    {
        public static final byte TYPE_OPEN = 0;
        public static final byte TYPE_WEP = 1;
        public static final byte TYPE_WPA = 2;
        public static final byte TYPE_WPA_WPA2 = 3;
        public static final byte TYPE_WPS_PBC = 4;
        public static final byte TYPE_WPS_PIN = 5;
        public static final byte TYPE_WPA_ENT = 6;
        public static final byte TYPE_P2P_PBC = 7;
        public static final byte TYPE_P2P_PIN_KEYPAD = 8;
        public static final byte TYPE_P2P_PIN_DISPLAY = 9;
        public static final byte TYPE_P2P_PIN_AUTO = 10;
    }
}
