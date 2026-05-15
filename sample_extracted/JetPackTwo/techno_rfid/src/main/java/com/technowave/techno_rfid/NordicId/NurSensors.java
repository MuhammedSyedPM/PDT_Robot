// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurSensors
{
    public static final int GPIO_ACT_NONE = 0;
    public static final int GPIO_ACT_NOTIFY = 1;
    public static final int GPIO_ACT_SCANTAG = 2;
    public static final int GPIO_ACT_INVENTORY = 3;
    public static final int SENSOR_SRC_TAP = 0;
    public static final int SENSOR_SRC_LIGHT = 1;
    public boolean tapSensorEnabled;
    public int tapAction;
    public boolean lightSensorEnabled;
    public int lightAction;
    
    public NurSensors() {
    }
    
    public NurSensors(final boolean tapSensorEnabled, final int tapAction, final boolean lightSensorEnabled, final int lightAction) {
        this.tapSensorEnabled = tapSensorEnabled;
        this.tapAction = tapAction;
        this.lightSensorEnabled = lightSensorEnabled;
        this.lightAction = lightAction;
    }
}
