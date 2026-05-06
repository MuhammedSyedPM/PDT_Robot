// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class AccSensorConfig
{
    public ACC_SENSOR_SOURCE source;
    public ACC_SENSOR_TYPE type;
    protected byte feature;
    protected byte mode;
    
    public boolean hasMode(final ACC_SENSOR_MODE_FLAG flag) {
        return (this.mode & flag.getByteVal()) != 0x0;
    }
    
    public void setModeFlag(final ACC_SENSOR_MODE_FLAG flag) {
        this.mode |= flag.getByteVal();
    }
    
    public void clearModeFlag(final ACC_SENSOR_MODE_FLAG flag) {
        this.mode &= (byte)~flag.getNumVal();
    }
    
    public boolean hasFeature(final ACC_SENSOR_FEATURE flag) {
        return (this.feature & flag.getByteVal()) != 0x0;
    }
    
    public ACC_SENSOR_MODE_FLAG[] getModeFlags() {
        final int cnt = Integer.bitCount(this.mode);
        if (cnt == 0) {
            return null;
        }
        final ACC_SENSOR_MODE_FLAG[] flags = new ACC_SENSOR_MODE_FLAG[cnt];
        int x = 0;
        if ((this.mode & ACC_SENSOR_MODE_FLAG.Gpio.getByteVal()) != 0x0) {
            flags[x++] = ACC_SENSOR_MODE_FLAG.Gpio;
        }
        if ((this.mode & ACC_SENSOR_MODE_FLAG.Stream.getByteVal()) != 0x0) {
            flags[x++] = ACC_SENSOR_MODE_FLAG.Stream;
        }
        return flags;
    }
    
    public ACC_SENSOR_FEATURE[] getFeatureFlags() {
        final int cnt = Integer.bitCount(this.feature);
        if (cnt == 0) {
            return null;
        }
        final ACC_SENSOR_FEATURE[] flags = new ACC_SENSOR_FEATURE[cnt];
        int x = 0;
        if ((this.feature & ACC_SENSOR_FEATURE.Range.getByteVal()) != 0x0) {
            flags[x++] = ACC_SENSOR_FEATURE.Range;
        }
        if ((this.feature & ACC_SENSOR_FEATURE.StreamValue.getByteVal()) != 0x0) {
            flags[x++] = ACC_SENSOR_FEATURE.StreamValue;
        }
        return flags;
    }
    
    public AccSensorConfig() {
        this.mode = 0;
    }
    
    protected void deserialize(final byte[] buffer, final int offset) {
        this.source = ACC_SENSOR_SOURCE.valueOf(buffer[offset + 0] & 0xFF);
        this.type = ACC_SENSOR_TYPE.valueOf(buffer[offset + 1] & 0xFF);
        this.feature = buffer[offset + 2];
        this.mode = buffer[offset + 4];
    }
}
