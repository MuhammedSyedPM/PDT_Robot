// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

import java.util.HashMap;
import java.util.Map;

public enum ACC_EVENT_TYPE
{
    None(0), 
    Barcode(1), 
    SensorChanged(2), 
    SensorRangeData(3), 
    SpeedTest(144);
    
    private int value;
    private static Map<Integer, ACC_EVENT_TYPE> map;
    
    public static ACC_EVENT_TYPE valueOf(final int ev) {
        return ACC_EVENT_TYPE.map.get(ev);
    }
    
    private ACC_EVENT_TYPE(final int value) {
        this.value = value;
    }
    
    public int getNumVal() {
        return this.value;
    }
    
    static {
        ACC_EVENT_TYPE.map = new HashMap<Integer, ACC_EVENT_TYPE>();
        for (final ACC_EVENT_TYPE ev : values()) {
            ACC_EVENT_TYPE.map.put(ev.value, ev);
        }
    }
}
