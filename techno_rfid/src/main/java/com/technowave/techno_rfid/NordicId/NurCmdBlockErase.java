// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdBlockErase extends NurCmd
{
    public static final int CMD = 64;
    private byte[] mResponse;
    private boolean mCbSecured;
    private int mCbPwd;
    private NurRWSingulationBlock mSb;
    private int mErBank;
    private int mErAddress;
    private int mErWordCount;
    
    public NurCmdBlockErase(final int passwd, final boolean secured, final int sBank, final int sAddress, final int sMaskBitLength, final byte[] sMask, final int erBank, final int erAddress, final int erWords) throws NurApiException {
        super(64, 0, 20 + sMaskBitLength);
        if (erWords < 1 || erWords > 255 || erBank < 0 || erBank > 3) {
            throw new NurApiException(5);
        }
        this.mSb = new NurRWSingulationBlock(sBank, sAddress, sMaskBitLength, sMask);
        this.mCbPwd = passwd;
        this.mCbSecured = secured;
        this.mErBank = erBank;
        this.mErAddress = erAddress;
        this.mErWordCount = erWords;
    }
    
    public byte[] getResponse() {
        return this.mResponse;
    }
    
    @Override
    public void deserializePayload(final byte[] data, final int offset, final int dataLen) {
        final int status = this.getStatus();
        if (status == 0 || status == 66) {
            this.mResponse = new byte[1];
            if (status == 66) {
                this.mResponse[0] = data[offset];
            }
            else {
                this.mResponse[0] = 0;
            }
        }
        else {
            this.mResponse = null;
        }
    }
    
    @Override
    public int serializePayload(final byte[] data, int offset) throws Exception {
        final int origOffset = offset;
        int cflags = 0;
        int pw = 0;
        if (this.mCbSecured) {
            cflags |= 0x1;
            pw = this.mCbPwd;
        }
        cflags |= this.mSb.getFlags();
        offset += NurPacket.PacketByte(data, offset, cflags);
        offset += NurPacket.PacketDword(data, offset, pw);
        offset = this.mSb.serialize(data, offset);
        final int btf = ((cflags & 0x8) != 0x0) ? 10 : 6;
        offset += NurPacket.PacketByte(data, offset, btf);
        offset += NurPacket.PacketByte(data, offset, this.mErBank);
        if ((cflags & 0x8) != 0x0) {
            throw new Exception("64 bit address is NYI!");
        }
        offset += NurPacket.PacketDword(data, offset, this.mErAddress);
        offset += NurPacket.PacketByte(data, offset, this.mErWordCount);
        return offset - origOffset;
    }
}
