// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

import java.util.HashMap;
import java.util.Map;

public enum ACC_WIRELESS_CHARGE_STATUS
{
    Off(0), 
    On(1), 
    Refused(-1), 
    Fail(-2), 
    NotSupported(-3);
    
    private int value;
    private static Map<Integer, ACC_WIRELESS_CHARGE_STATUS> map;
    
    public static ACC_WIRELESS_CHARGE_STATUS valueOf(final int ev) {
        return ACC_WIRELESS_CHARGE_STATUS.map.get(ev);
    }
    
    private ACC_WIRELESS_CHARGE_STATUS(final int value) {
        this.value = value;
    }
    
    public int getNumVal() {
        return this.value;
    }
    
    static {
        ACC_WIRELESS_CHARGE_STATUS.map = new HashMap<Integer, ACC_WIRELESS_CHARGE_STATUS>();
        for (final ACC_WIRELESS_CHARGE_STATUS ev : values()) {
            ACC_WIRELESS_CHARGE_STATUS.map.put(ev.value, ev);
        }
    }
}
