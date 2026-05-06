// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class RssiFilter
{
    public int min;
    public int max;
    
    public RssiFilter() {
    }
    
    public RssiFilter(final int min, final int max) {
        this.min = min;
        this.max = max;
    }
}
