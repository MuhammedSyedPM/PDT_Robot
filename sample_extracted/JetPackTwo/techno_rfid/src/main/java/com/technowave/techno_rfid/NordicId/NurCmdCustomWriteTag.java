// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdCustomWriteTag extends NurCmd
{
    public static final int CMD = 61;
    private boolean mCbSecured;
    private int mCbPwd;
    private NurRWSingulationBlock mSb;
    private int mWbWordCount;
    private int mWbAddress;
    private byte[] mWbData;
    private int mCustomCmd;
    private int mCustomCmdbits;
    private int mCustomBank;
    private int mCustomBankbits;
    
    public NurCmdCustomWriteTag(final int newcmd, final int wrCmd, final int cmdBits, final int wrBank, final int bankBits, final int passwd, final boolean secured, final int sBank, final int sAddress, final int sMaskBitLength, final byte[] sMask, final int wrAddress, final int wrByteCount, final byte[] wrData) throws NurApiException {
        super(newcmd, 0, 28 + wrByteCount + sMaskBitLength);
        if (wrByteCount > 244) {
            throw new NurApiException(5);
        }
        if (wrByteCount % 2 != 0) {
            throw new NurApiException(4106);
        }
        this.mCbPwd = passwd;
        this.mCbSecured = secured;
        this.mSb = new NurRWSingulationBlock(sBank, sAddress, sMaskBitLength, sMask);
        this.mWbAddress = wrAddress;
        this.mWbWordCount = wrByteCount / 2;
        this.mWbData = wrData;
        this.mCustomCmd = wrCmd;
        this.mCustomCmdbits = cmdBits;
        this.mCustomBank = wrBank;
        this.mCustomBankbits = bankBits;
    }
    
    public NurCmdCustomWriteTag(final int wrCmd, final int cmdBits, final int wrBank, final int bankBits, final int passwd, final boolean secured, final int sBank, final int sAddress, final int sMaskBitLength, final byte[] sMask, final int wrAddress, final int wrByteCount, final byte[] wrBuffer) throws NurApiException {
        this(61, wrCmd, cmdBits, wrBank, bankBits, passwd, secured, sBank, sAddress, sMaskBitLength, sMask, wrAddress, wrByteCount, wrBuffer);
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
        int btf = this.mWbWordCount * 2 + 10;
        btf += (((cflags & 0x8) != 0x0) ? 9 : 5);
        offset += NurPacket.PacketByte(data, offset, btf);
        offset += NurPacket.PacketDword(data, offset, this.mCustomCmd);
        offset += NurPacket.PacketByte(data, offset, this.mCustomCmdbits);
        offset += NurPacket.PacketDword(data, offset, this.mCustomBank);
        offset += NurPacket.PacketByte(data, offset, this.mCustomBankbits);
        if ((cflags & 0x8) != 0x0) {
            throw new Exception("64 bit address is NYI!");
        }
        offset += NurPacket.PacketDword(data, offset, this.mWbAddress);
        offset += NurPacket.PacketByte(data, offset, this.mWbWordCount);
        offset += NurPacket.PacketBytes(data, offset, this.mWbData, this.mWbWordCount * 2);
        return offset - origOffset;
    }
}
