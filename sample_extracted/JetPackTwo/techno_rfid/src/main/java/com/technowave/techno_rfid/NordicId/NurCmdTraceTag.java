// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdTraceTag extends NurCmd
{
    public static final int CMD = 56;
    private int mFlags;
    private int mBank;
    private int mAddress;
    private int mMaskBitLength;
    private byte[] mMaskdata;
    private NurRespReadData mResp;
    
    public NurRespReadData getResponse() {
        return this.mResp;
    }
    
    public NurCmdTraceTag(final int flags, final int sBank, final int sAddress, final int sMaskBitLength, final byte[] sMaskData) throws NurApiException {
        super(56, 0, sMaskBitLength / 8 + ((sMaskBitLength % 8 != 0) ? 1 : 0) + 7);
        if (sMaskBitLength <= 0 || sMaskBitLength > 255 || sMaskData == null) {
            throw new NurApiException(5);
        }
        this.mFlags = (flags & 0xF);
        this.mBank = sBank;
        this.mAddress = sAddress;
        this.mMaskBitLength = sMaskBitLength;
        this.mMaskdata = sMaskData;
        this.mResp = new NurRespReadData();
    }
    
    @Override
    public int serializePayload(final byte[] data, int offset) throws Exception {
        final int origOffset = offset;
        int maskdataLen = 0;
        maskdataLen = this.mMaskBitLength / 8 + ((this.mMaskBitLength % 8 != 0) ? 1 : 0);
        offset += NurPacket.PacketByte(data, offset, this.mFlags);
        offset += NurPacket.PacketByte(data, offset, this.mBank);
        if ((this.mFlags & 0x4) != 0x0) {
            throw new Exception("64 bit address is NYI!");
        }
        offset += NurPacket.PacketDword(data, offset, this.mAddress);
        offset += NurPacket.PacketByte(data, offset, this.mMaskBitLength);
        offset += NurPacket.PacketBytes(data, offset, this.mMaskdata, maskdataLen);
        return offset - origOffset;
    }
    
    @Override
    public void deserializePayload(final byte[] data, int offset, final int dataLen) throws Exception {
        if (this.status == 0 && (this.flags & 0x1) == 0x0) {
            this.mResp.epcLen = dataLen - 3;
            this.mResp.rssi = data[offset++];
            this.mResp.scaledRssi = NurPacket.BytesToByte(data, offset++);
            this.mResp.antennaID = NurPacket.BytesToByte(data, offset++);
            if (this.mResp.epcLen > 0) {
                System.arraycopy(data, offset, this.mResp.epc = new byte[this.mResp.epcLen], 0, this.mResp.epcLen);
                this.mResp.epcStr = NurApi.byteArrayToHexString(this.mResp.epc);
            }
            else {
                this.mResp.epcStr = "";
            }
        }
        else {
            this.mResp.rssi = -127;
            this.mResp.scaledRssi = 0;
            this.mResp.antennaID = -1;
        }
    }
}
