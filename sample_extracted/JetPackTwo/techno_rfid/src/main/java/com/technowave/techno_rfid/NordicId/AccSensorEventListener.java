// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public interface AccSensorEventListener
{
    void onSensorChanged(final AccSensorChanged p0);
    
    void onRangeData(final AccSensorRangeData p0);
}
