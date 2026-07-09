// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

import java.util.HashMap;
import java.util.Map;

public enum ACC_SENSOR_TYPE
{
    ULTRASONIC_MAXSONAR(0), 
    DEVICE_GPIO(1), 
    DEVICE_TAP(2), 
    DEVICE_TOF(3);
    
    private int value;
    private static Map<Integer, ACC_SENSOR_TYPE> map;
    
    public static ACC_SENSOR_TYPE valueOf(final int ev) {
        return ACC_SENSOR_TYPE.map.get(ev);
    }
    
    private ACC_SENSOR_TYPE(final int value) {
        this.value = value;
    }
    
    public int getNumVal() {
        return this.value;
    }
    
    public byte getByteVal() {
        return (byte)(this.value & 0xFF);
    }
    
    static {
        ACC_SENSOR_TYPE.map = new HashMap<Integer, ACC_SENSOR_TYPE>();
        for (final ACC_SENSOR_TYPE ev : values()) {
            ACC_SENSOR_TYPE.map.put(ev.value, ev);
        }
    }
}
