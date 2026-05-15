// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class AccBattery
{
    public static final int SZ_BATTERY_REPLY = 9;
    public static final int ACC_EXT_GET_BATT = 3;
    public static final int ACC_EXT_GET_BATT_INFO = 9;
    public int percentage;
    public int voltage;
    public int current;
    public int capacity;
    public boolean charging;
    private static final int FLAG_CHARGING = 1;
    
    public AccBattery() {
        this.percentage = -1;
        this.voltage = -1;
        this.current = 0;
        this.capacity = -1;
        this.charging = false;
    }
    
    public static AccBattery deserializeBatteryReply(final byte[] source) throws Exception {
        if (source == null || source.length < 9) {
            throw new NurApiException("Accessroy, battery:iinvalud reply");
        }
        final AccBattery batteryInfo = new AccBattery();
        int sourcePtr = 0;
        if ((NurPacket.BytesToWord(source, sourcePtr) & 0x1) != 0x0) {
            batteryInfo.charging = true;
        }
        sourcePtr += 2;
        batteryInfo.percentage = (source[sourcePtr++] & 0xFF);
        if (batteryInfo.percentage == 255) {
            batteryInfo.percentage = -1;
        }
        batteryInfo.voltage = (short)NurPacket.BytesToWord(source, sourcePtr);
        sourcePtr += 2;
        batteryInfo.current = (short)NurPacket.BytesToWord(source, sourcePtr);
        sourcePtr += 2;
        batteryInfo.capacity = (short)NurPacket.BytesToWord(source, sourcePtr);
        return batteryInfo;
    }
    
    public static byte[] getQueryCommand() {
        return new byte[] { 9 };
    }
    
    public String getPercentageString() {
        if (this.percentage < 0 || this.percentage > 100) {
            return "N/A";
        }
        return this.percentage + "%";
    }
    
    public String getVoltageString() {
        if (this.voltage < 0) {
            return "N/A";
        }
        return this.voltage + "mV";
    }
    
    public String getCurrentString() {
        return this.current + "mA";
    }
    
    public String getCapacityString() {
        if (this.capacity < 0) {
            return "N/A";
        }
        return this.capacity + "mAh";
    }
}
