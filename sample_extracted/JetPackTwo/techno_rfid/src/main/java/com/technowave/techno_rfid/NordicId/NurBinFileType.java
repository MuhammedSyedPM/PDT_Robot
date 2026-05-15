// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurBinFileType
{
    public static final int TYPE_INVALID = 0;
    public int fwType;
    public int versionNumber;
    public int versionMajor;
    public int versionMinor;
    public char build;
    
    public NurBinFileType() {
        this.fwType = 0;
        this.versionNumber = 0;
        this.versionMajor = 0;
        this.versionMinor = 0;
        this.build = '\0';
    }
    
    public String getVersion() {
        return String.valueOf(this.versionMajor) + "." + String.valueOf(this.versionMinor) + "-" + this.build;
    }
}
