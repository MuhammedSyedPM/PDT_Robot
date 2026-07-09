// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

import java.util.HashMap;
import java.util.Map;

public enum ACC_SENSOR_SOURCE
{
    GpioPin1(0), 
    GpioPin2(1), 
    GpioPin3(2), 
    GpioPin4(3), 
    TapSensor(128), 
    UsbPort1(130), 
    UsbPort2(131), 
    UsbPort3(132), 
    UsbPort4(133), 
    ToFSensor(134);
    
    private int value;
    private static Map<Integer, ACC_SENSOR_SOURCE> map;
    
    public static ACC_SENSOR_SOURCE valueOf(final int ev) {
        return ACC_SENSOR_SOURCE.map.get(ev);
    }
    
    private ACC_SENSOR_SOURCE(final int value) {
        this.value = value;
    }
    
    public int getNumVal() {
        return this.value;
    }
    
    public byte getByteVal() {
        return (byte)(this.value & 0xFF);
    }
    
    static {
        ACC_SENSOR_SOURCE.map = new HashMap<Integer, ACC_SENSOR_SOURCE>();
        for (final ACC_SENSOR_SOURCE ev : values()) {
            ACC_SENSOR_SOURCE.map.put(ev.value, ev);
        }
    }
}
