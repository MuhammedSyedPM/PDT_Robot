// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdWriteTag extends NurCmd
{
    public static final int CMD = 52;
    private boolean mCbSecured;
    private int mCbPwd;
    private NurRWSingulationBlock mSb;
    private int mWbBank;
    private int mWbAddress;
    private int mWbWordCount;
    private byte[] mWbData;
    
    public NurCmdWriteTag(final int passwd, final boolean secured, final int sBank, final int sAddress, final int sMaskBitLength, final byte[] sMask, final int wrBank, final int wrAddress, final int wrByteCount, final byte[] wrData) throws NurApiException {
        this(52, passwd, secured, sBank, sAddress, sMaskBitLength, sMask, wrBank, wrAddress, wrByteCount, wrData);
    }
    
    public NurCmdWriteTag(final int newcmd, final int passwd, final boolean secured, final int sBank, final int sAddress, final int sMaskBitLength, final byte[] sMask, final int wrBank, final int wrAddress, final int wrByteCount, final byte[] wrData) throws NurApiException {
        super(newcmd, 0, 20 + sMaskBitLength + wrByteCount * 8);
        if (wrByteCount > 244) {
            throw new NurApiException(5);
        }
        if (wrByteCount % 2 != 0) {
            throw new NurApiException(4106);
        }
        this.mSb = new NurRWSingulationBlock(sBank, sAddress, sMaskBitLength, sMask);
        this.mCbPwd = passwd;
        this.mCbSecured = secured;
        this.mWbBank = wrBank;
        this.mWbAddress = wrAddress;
        this.mWbWordCount = wrByteCount / 2;
        this.mWbData = wrData;
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
        int btf = ((cflags & 0x8) != 0x0) ? 10 : 6;
        btf += this.mWbWordCount * 2;
        offset += NurPacket.PacketByte(data, offset, btf);
        offset += NurPacket.PacketByte(data, offset, this.mWbBank);
        if ((cflags & 0x8) != 0x0) {
            throw new Exception("64 bit address is NYI!");
        }
        offset += NurPacket.PacketDword(data, offset, this.mWbAddress);
        offset += NurPacket.PacketByte(data, offset, this.mWbWordCount);
        offset += NurPacket.PacketBytes(data, offset, this.mWbData, this.mWbWordCount * 2);
        return offset - origOffset;
    }
}
