// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdSensors extends NurCmd
{
    public static final int CMD = 18;
    public static final int NUR_SENSOR_TAP = 1;
    public static final int NUR_SENSOR_LIGHT = 2;
    private NurSensors mRespSensors;
    private NurSensors mParamSensors;
    private boolean mGetSensors;
    
    public NurSensors getResponse() {
        return this.mRespSensors;
    }
    
    public NurCmdSensors(final NurSensors sensors) throws NurApiException {
        super(18, 0, 5);
        this.mGetSensors = false;
        if (sensors.lightAction < 0 || sensors.lightAction > 3 || sensors.tapAction < 0 || sensors.tapAction > 3) {
            throw new NurApiException(5);
        }
        this.mRespSensors = new NurSensors();
        this.mParamSensors = sensors;
    }
    
    public NurCmdSensors() {
        super(18);
        this.mGetSensors = false;
        this.mRespSensors = new NurSensors();
        this.mGetSensors = true;
    }
    
    @Override
    public int serializePayload(final byte[] data, int offset) throws Exception {
        if (this.mGetSensors) {
            return 0;
        }
        final int setupFlags = 3;
        final int origOffset = offset;
        offset += NurPacket.PacketByte(data, offset, setupFlags);
        offset += NurPacket.PacketByte(data, offset, this.mParamSensors.tapSensorEnabled ? 1 : 0);
        offset += NurPacket.PacketByte(data, offset, this.mParamSensors.tapAction);
        offset += NurPacket.PacketByte(data, offset, this.mParamSensors.lightSensorEnabled ? 1 : 0);
        offset += NurPacket.PacketByte(data, offset, this.mParamSensors.lightAction);
        return offset - origOffset;
    }
    
    @Override
    public void deserializePayload(final byte[] data, int offset, final int dataLen) {
        final int retFlags = NurPacket.BytesToByte(data, offset++);
        if ((retFlags & 0x1) != 0x0) {
            this.mRespSensors.tapSensorEnabled = (NurPacket.BytesToByte(data, offset++) != 0);
            this.mRespSensors.tapAction = NurPacket.BytesToByte(data, offset++);
        }
        if ((retFlags & 0x2) != 0x0) {
            this.mRespSensors.lightSensorEnabled = (NurPacket.BytesToByte(data, offset++) != 0);
            this.mRespSensors.lightAction = NurPacket.BytesToByte(data, offset++);
        }
    }
}
