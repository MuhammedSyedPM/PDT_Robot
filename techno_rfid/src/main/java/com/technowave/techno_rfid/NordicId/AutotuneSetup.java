// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class AutotuneSetup
{
    public static final int ATMODE_OFF = 0;
    public static final int ATMODE_EN = 1;
    public static final int ATMODE_THEN = 2;
    public int mode;
    public int thresholddBm;
    
    public AutotuneSetup() {
        this.mode = 0;
        this.thresholddBm = 0;
    }
    
    public AutotuneSetup(final int mode, final int thresh) {
        this.mode = 0;
        this.thresholddBm = 0;
        this.mode = mode;
        this.thresholddBm = thresh;
    }
}
