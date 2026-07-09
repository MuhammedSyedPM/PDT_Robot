// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class AccSensorFilter
{
    protected int flags;
    public AccSensorFilterThreshold rangeThreshold;
    public AccSensorFilterThreshold timeThreshold;
    
    public AccSensorFilter() {
        this.rangeThreshold = new AccSensorFilterThreshold();
        this.timeThreshold = new AccSensorFilterThreshold();
    }
    
    public ACC_SENSOR_FILTER_FLAG[] getFilterFlags() {
        final int cnt = Integer.bitCount(this.flags);
        final ACC_SENSOR_FILTER_FLAG[] retFlags = new ACC_SENSOR_FILTER_FLAG[cnt];
        int x = 0;
        if ((this.flags & ACC_SENSOR_FILTER_FLAG.Range.getNumVal()) != 0x0) {
            retFlags[x++] = ACC_SENSOR_FILTER_FLAG.Range;
        }
        if ((this.flags & ACC_SENSOR_FILTER_FLAG.Time.getNumVal()) != 0x0) {
            retFlags[x++] = ACC_SENSOR_FILTER_FLAG.Time;
        }
        return retFlags;
    }
    
    public void setFilterFlag(final ACC_SENSOR_FILTER_FLAG flag) {
        this.flags |= flag.getNumVal();
    }
    
    public void clearFilterFlag(final ACC_SENSOR_FILTER_FLAG flag) {
        this.flags &= ~flag.getNumVal();
    }
}
