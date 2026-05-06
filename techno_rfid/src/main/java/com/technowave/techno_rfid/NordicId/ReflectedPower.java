// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class ReflectedPower
{
    private int mFrequency;
    public float mReflectedPower;
    public int iPart;
    public int qPart;
    public int divider;
    
    private void calculateReflectedPower() {
        final double div = this.divider;
        double rf = this.iPart * this.iPart + this.qPart * this.qPart;
        rf = Math.sqrt(rf);
        if (!Double.isInfinite(rf) && !Double.isNaN(rf)) {
            rf /= div;
            if (rf <= 0.0) {
                rf = 0.0;
            }
            else {
                rf = Math.log10(rf) * 20.0;
            }
        }
        else {
            rf = 0.0;
        }
        this.mReflectedPower = (float)rf;
    }
    
    public ReflectedPower(final int I, final int Q, final int D, final int f) {
        this.mFrequency = 0;
        this.mReflectedPower = 0.0f;
        this.iPart = 0;
        this.qPart = 0;
        this.divider = 0;
        this.iPart = I;
        this.qPart = Q;
        this.divider = D;
        if (f >= 840000 && f <= 960000) {
            this.mFrequency = f;
        }
        this.calculateReflectedPower();
    }
    
    public String getFrequency() {
        if (this.mFrequency == 0) {
            return "N/A";
        }
        return Integer.toString(this.mFrequency);
    }
}
