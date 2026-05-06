// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class AccVersionInfo
{
    static String TAG;
    private String mBootloaderVersion;
    private String mFullApplicationVersion;
    private String mApplicationVersion;
    
    public AccVersionInfo(final String version) {
        final String[] versions = version.split(";");
        this.mBootloaderVersion = ((versions.length > 1) ? versions[1] : "1");
        this.mFullApplicationVersion = versions[0];
        this.mApplicationVersion = versions[0].split(" ")[0].replaceAll("[^\\d.]", "");
    }
    
    public String getApplicationVersion() {
        return this.mApplicationVersion;
    }
    
    public String getFullApplicationVersion() {
        return this.mFullApplicationVersion;
    }
    
    public String getBootloaderVersion() {
        return this.mBootloaderVersion;
    }
    
    static {
        AccVersionInfo.TAG = "NurAccessoryVersionInfo";
    }
}
