// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdEpcEnum extends NurCmd
{
    public static final int CMD = 67;
    private static int errAnt;
    private static int errBlWrite;
    private static int errTidWords;
    private static int errEpcWords;
    private static int errBitLen;
    private static int errOutOfRange;
    private static int errTooBig;
    private NurEpcEnumParam mParams;
    private NurRespEpcEnum mResp;
    
    public NurCmdEpcEnum(final NurEpcEnumParam eeParam) {
        super(67, 0, 32);
        this.mParams = null;
        this.mResp = new NurRespEpcEnum();
        this.mParams = eeParam;
    }
    
    public NurRespEpcEnum getResponse() {
        return this.mResp;
    }
    
    private boolean IsErrorFlag(final int flags, final int mask) {
        return (flags & mask) != 0x0;
    }
    
    @Override
    public int serializePayload(final byte[] data, int offset) throws Exception {
        if (this.mParams != null) {
            offset += NurPacket.PacketByte(data, offset, this.mParams.antenna);
            offset += NurPacket.PacketByte(data, offset, this.mParams.twAddr);
            offset += NurPacket.PacketByte(data, offset, this.mParams.twLen);
            offset += NurPacket.PacketByte(data, offset, this.mParams.useBlWrite);
            System.arraycopy(this.mParams.startVal, 0, data, offset, 8);
            offset += 8;
            offset += NurPacket.PacketByte(data, offset, this.mParams.epcLen);
            offset += NurPacket.PacketByte(data, offset, this.mParams.modAddr);
            offset += NurPacket.PacketByte(data, offset, this.mParams.bitLen);
            offset += NurPacket.PacketByte(data, offset, this.mParams.bReset ? 1 : 0);
            System.arraycopy(this.mParams.baseEPC, 0, data, offset, 16);
        }
        else {
            for (int i = 0; i < 32; ++i) {
                data[offset++] = -1;
            }
        }
        return 32;
    }
    
    @Override
    public void deserializePayload(final byte[] data, final int offset, final int dataLen) throws Exception {
        if (this.mParams == null) {
            return;
        }
        final int stat = this.getStatus();
        if (stat != 0) {
            final int flags = NurPacket.BytesToByte(data, offset);
            this.mResp.wasError = true;
            this.mResp.noAntenna = this.IsErrorFlag(flags, NurCmdEpcEnum.errAnt);
            this.mResp.writeTypeError = this.IsErrorFlag(flags, NurCmdEpcEnum.errBlWrite);
            this.mResp.tidLenError = this.IsErrorFlag(flags, NurCmdEpcEnum.errTidWords);
            this.mResp.epcLenError = this.IsErrorFlag(flags, NurCmdEpcEnum.errEpcWords);
            this.mResp.bitLenError = this.IsErrorFlag(flags, NurCmdEpcEnum.errBitLen);
            this.mResp.modAddrError = this.IsErrorFlag(flags, NurCmdEpcEnum.errOutOfRange);
            this.mResp.modTooBig = this.IsErrorFlag(flags, NurCmdEpcEnum.errTooBig);
        }
    }
    
    static {
        NurCmdEpcEnum.errAnt = 1;
        NurCmdEpcEnum.errBlWrite = 2;
        NurCmdEpcEnum.errTidWords = 4;
        NurCmdEpcEnum.errEpcWords = 8;
        NurCmdEpcEnum.errBitLen = 16;
        NurCmdEpcEnum.errOutOfRange = 32;
        NurCmdEpcEnum.errTooBig = 64;
    }
}
