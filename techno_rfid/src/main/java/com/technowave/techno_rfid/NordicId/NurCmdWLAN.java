// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdWLAN extends NurCmd
{
    public static final int CMD = 176;
    private static final int MIN_LENGTH_FOR_STATUS_REPLY = 162;
    private NurWLANStatus mReceivedStatus;
    private byte mTargetValue;
    private byte mCommandValue;
    private byte[] mParameterBytes;
    
    public NurWLANStatus getStatusReply() {
        return this.mReceivedStatus;
    }
    
    private static int getRequiredBufferSize(final byte wlanCommand) throws NurApiException {
        switch (wlanCommand) {
            case 0:
            case 5: {
                return 3;
            }
            case 1: {
                return 2;
            }
            default: {
                throw new NurApiException("NurCmdWLAN::getRequiredBufferSize: invalid command + " + wlanCommand, 5);
            }
        }
    }
    
    private void evaluateBuildCommand(final byte wlanCommand, final byte byteParameter) throws NurApiException {
        this.mTargetValue = 5;
        switch (wlanCommand) {
            case 0:
            case 5: {
                (this.mParameterBytes = new byte[1])[1] = (byte)((byteParameter != 0) ? 1 : 0);
                break;
            }
            case 4: {
                if (byteParameter < 0 || byteParameter > 7) {
                    throw new NurApiException("WLAN profile delete: invalid profile index", 5);
                }
                this.mParameterBytes = new byte[] { byteParameter };
                break;
            }
        }
        this.mCommandValue = wlanCommand;
    }
    
    public NurCmdWLAN(final byte wlanCommand, final byte byteParameter) throws NurApiException {
        super(176, getRequiredBufferSize(wlanCommand));
        this.mReceivedStatus = null;
        this.mTargetValue = -1;
        this.mCommandValue = -1;
        this.mParameterBytes = null;
        this.evaluateBuildCommand(wlanCommand, byteParameter);
    }
    
    private void processStatusReply(final byte[] data, int offset, final int dataLen) throws Exception {
        if (dataLen < 162) {
            throw new NurApiException("NurCmdWLAN::getStatus", 4103);
        }
        this.mReceivedStatus = new NurWLANStatus();
        this.mReceivedStatus.wlanStatus = NurPacket.BytesToDword(data, offset);
        offset += 4;
        this.mReceivedStatus.role = NurPacket.BytesToDword(data, offset);
        offset += 4;
        this.mReceivedStatus.connectionStatus = NurPacket.BytesToDword(data, offset);
        offset += 4;
        this.mReceivedStatus.profileCount = NurPacket.BytesToDword(data, offset);
        offset += 4;
        this.mReceivedStatus.lastError = NurPacket.BytesToDword(data, offset);
        offset += 4;
        this.mReceivedStatus.chipId = NurPacket.BytesToDword(data, offset);
        offset += 4;
        this.mReceivedStatus.fwVersion = NurPacket.BytesToString(data, offset, 16);
        offset += 16;
        this.mReceivedStatus.phyVersion = NurPacket.BytesToString(data, offset, 16);
        offset += 16;
        this.mReceivedStatus.nwpVersion = NurPacket.BytesToString(data, offset, 16);
        offset += 16;
        this.mReceivedStatus.hostDrvVersion = NurPacket.BytesToString(data, offset, 16);
        offset += 16;
        this.mReceivedStatus.romVersion = NurPacket.BytesToDword(data, offset);
        offset += 4;
        this.mReceivedStatus.ip = NurPacket.BytesToDword(data, offset);
        offset += 4;
        this.mReceivedStatus.gwIP = NurPacket.BytesToDword(data, offset);
        offset += 4;
        System.arraycopy(data, offset, this.mReceivedStatus.mac, 0, 6);
        offset += 6;
        this.mReceivedStatus.ssid = NurPacket.BytesToString(data, offset, 32);
        offset += 32;
        System.arraycopy(data, offset, this.mReceivedStatus.connectionMAC, 0, 6);
        offset += 6;
        this.mReceivedStatus.scanTime = NurPacket.BytesToDword(data, offset);
        offset += 4;
        this.mReceivedStatus.rssiMgmnt = NurPacket.BytesToWord(data, offset);
        offset += 2;
        this.mReceivedStatus.rssiDataCtl = NurPacket.BytesToWord(data, offset);
        offset += 2;
        System.arraycopy(data, offset, this.mReceivedStatus.reserved, 0, 10);
    }
    
    private void processReply(final int error, final byte[] data, final int offset, final int dataLen) throws Exception {
        if (error != 0) {
            throw new NurApiException("NurCmdWLAN", error);
        }
        if (this.mTargetValue == 5) {
            if (this.mCommandValue == 0 || this.mCommandValue == 5 || this.mCommandValue == 4) {
                return;
            }
            if (this.mCommandValue == 1) {
                this.processStatusReply(data, offset, dataLen);
            }
        }
    }
    
    @Override
    public int serializePayload(final byte[] data, final int offset) {
        int newOffset = offset;
        newOffset += NurPacket.PacketByte(data, newOffset, this.mTargetValue);
        newOffset += NurPacket.PacketByte(data, newOffset, this.mCommandValue);
        if (this.mParameterBytes != null) {
            newOffset += NurPacket.PacketBytes(data, newOffset, this.mParameterBytes);
        }
        return newOffset - offset;
    }
    
    @Override
    public void deserializePayload(final byte[] data, final int offset, final int dataLen) throws Exception {
        this.processReply(this.getStatus(), data, offset, dataLen);
    }
}
