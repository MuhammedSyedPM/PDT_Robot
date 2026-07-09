// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdReaderInfo extends NurCmd
{
    public static final int CMD = 9;
    private NurRespReaderInfo mResp;
    
    public NurRespReaderInfo getResponse() {
        return this.mResp;
    }
    
    public NurCmdReaderInfo() {
        super(9);
        this.mResp = new NurRespReaderInfo();
    }
    
    @Override
    public void deserializePayload(final byte[] data, int offset, final int dataLen) {
        this.mResp.szResponse = data.length;
        this.mResp.version = NurPacket.BytesToDword(data, offset);
        offset += 4;
        int strLen = NurPacket.BytesToByte(data, offset++);
        this.mResp.serial = NurPacket.BytesToString(data, offset, strLen);
        offset += strLen;
        strLen = NurPacket.BytesToByte(data, offset++);
        this.mResp.altSerial = NurPacket.BytesToString(data, offset, strLen);
        offset += strLen;
        strLen = NurPacket.BytesToByte(data, offset++);
        this.mResp.name = NurPacket.BytesToString(data, offset, strLen);
        offset += strLen;
        strLen = NurPacket.BytesToByte(data, offset++);
        this.mResp.fccId = NurPacket.BytesToString(data, offset, strLen);
        offset += strLen;
        strLen = NurPacket.BytesToByte(data, offset++);
        this.mResp.hwVersion = NurPacket.BytesToString(data, offset, strLen);
        offset += strLen;
        this.mResp.swVerMajor = NurPacket.BytesToByte(data, offset++);
        this.mResp.swVerMinor = NurPacket.BytesToByte(data, offset++);
        this.mResp.swVerDev = (char)NurPacket.BytesToByte(data, offset++);
        this.mResp.swVersion = String.format("%d.%d-%c", this.mResp.swVerMajor, this.mResp.swVerMinor, this.mResp.swVerDev);
        this.mResp.numGpio = NurPacket.BytesToByte(data, offset++);
        this.mResp.numSensors = NurPacket.BytesToByte(data, offset++);
        this.mResp.numRegions = NurPacket.BytesToByte(data, offset++);
        this.mResp.numAntennas = NurPacket.BytesToByte(data, offset++);
        if (offset < this.mResp.szResponse) {
            this.mResp.maxAntennas = NurPacket.BytesToByte(data, offset++);
        }
    }
}
