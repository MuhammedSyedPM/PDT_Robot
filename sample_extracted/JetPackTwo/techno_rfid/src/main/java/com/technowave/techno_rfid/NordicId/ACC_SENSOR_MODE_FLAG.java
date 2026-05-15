// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

import java.util.HashMap;
import java.util.Map;

public enum ACC_SENSOR_MODE_FLAG
{
    Gpio(1), 
    Stream(2);
    
    private int value;
    private static Map<Integer, ACC_SENSOR_MODE_FLAG> map;
    
    public static ACC_SENSOR_MODE_FLAG valueOf(final int ev) {
        return ACC_SENSOR_MODE_FLAG.map.get(ev);
    }
    
    private ACC_SENSOR_MODE_FLAG(final int value) {
        this.value = value;
    }
    
    public int getNumVal() {
        return this.value;
    }
    
    public byte getByteVal() {
        return (byte)(this.value & 0xFF);
    }
    
    static {
        ACC_SENSOR_MODE_FLAG.map = new HashMap<Integer, ACC_SENSOR_MODE_FLAG>();
        for (final ACC_SENSOR_MODE_FLAG ev : values()) {
            ACC_SENSOR_MODE_FLAG.map.put(ev.value, ev);
        }
    }
}
