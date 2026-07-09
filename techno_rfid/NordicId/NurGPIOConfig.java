// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurGPIOConfig
{
    public static final int GPIO_TYPE_OUTPUT = 0;
    public static final int GPIO_TYPE_INPUT = 1;
    public static final int GPIO_TYPE_RFIDON = 2;
    public static final int GPIO_TYPE_RFIDREAD = 3;
    public static final int GPIO_TYPE_BEEPER = 4;
    public static final int GPIO_TYPE_ANTCTL1 = 5;
    public static final int GPIO_TYPE_ANTCTL2 = 6;
    public static final int GPIO_EDGE_FALLING = 0;
    public static final int GPIO_EDGE_RISING = 1;
    public static final int GPIO_EDGE_BOTH = 2;
    public static final int GPIO_ACT_NONE = 0;
    public static final int GPIO_ACT_NOTIFY = 1;
    public static final int GPIO_ACT_SCANTAG = 2;
    public static final int GPIO_ACT_INVENTORY = 3;
    public boolean available;
    public boolean enabled;
    public int type;
    public int edge;
    public int action;
    
    public NurGPIOConfig() {
    }
    
    public NurGPIOConfig(final boolean available, final boolean enabled, final int type, final int edge, final int action) {
        this.available = available;
        this.enabled = enabled;
        this.type = type;
        this.edge = edge;
        this.action = action;
    }
}
