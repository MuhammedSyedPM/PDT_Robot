// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurRespGPIOStatus
{
    public static final int GPIO_TYPE_OUTPUT = 0;
    public static final int GPIO_TYPE_INPUT = 1;
    public static final int GPIO_TYPE_RFIDON = 2;
    public static final int GPIO_TYPE_RFIDREAD = 3;
    public static final int GPIO_TYPE_BEEPER = 4;
    public static final int GPIO_TYPE_ANTCTL1 = 5;
    public static final int GPIO_TYPE_ANTCTL2 = 6;
    public boolean enabled;
    public int type;
    public boolean state;
}
