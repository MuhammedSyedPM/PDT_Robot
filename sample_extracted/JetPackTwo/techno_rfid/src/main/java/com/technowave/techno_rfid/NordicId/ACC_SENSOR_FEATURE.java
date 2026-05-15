// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

import java.util.HashMap;
import java.util.Map;

public enum ACC_SENSOR_FEATURE
{
    Range(1), 
    StreamValue(2);
    
    private int value;
    private static Map<Integer, ACC_SENSOR_FEATURE> map;
    
    public static ACC_SENSOR_FEATURE valueOf(final int ev) {
        return ACC_SENSOR_FEATURE.map.get(ev);
    }
    
    private ACC_SENSOR_FEATURE(final int value) {
        this.value = value;
    }
    
    public int getNumVal() {
        return this.value;
    }
    
    public byte getByteVal() {
        return (byte)(this.value & 0xFF);
    }
    
    static {
        ACC_SENSOR_FEATURE.map = new HashMap<Integer, ACC_SENSOR_FEATURE>();
        for (final ACC_SENSOR_FEATURE ev : values()) {
            ACC_SENSOR_FEATURE.map.put(ev.value, ev);
        }
    }
}
