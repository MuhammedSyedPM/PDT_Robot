// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdCustomExchange extends NurCmd
{
    public static final int CMD = 63;
    private NurApi mApi;
    private CustomExchangeParams mParams;
    private NurRespCustomExchange mResponse;
    private boolean mCbSecured;
    private int mCbPwd;
    private NurRWSingulationBlock mSb;
    
    public NurRespCustomExchange getResponse() {
        return this.mResponse;
    }
    
    public NurCmdCustomExchange(final NurApi api, final int passwd, final boolean secured, final int sBank, final int sAddress, final int sMaskBitLength, final byte[] sMask, final CustomExchangeParams params) throws NurApiException, Exception {
        super(63, 0, 5 + CustomExchangeParams.getByteLength(params) + NurRWSingulationBlock.getByteLength(sMaskBitLength));
        this.mApi = null;
        this.mParams = null;
        this.mResponse = null;
        this.mCbSecured = false;
        this.mCbPwd = 0;
        this.mSb = null;
        this.mApi = api;
        this.mSb = new NurRWSingulationBlock(sBank, sAddress, sMaskBitLength, sMask);
        this.mCbPwd = passwd;
        this.mCbSecured = secured;
        this.mParams = params;
    }
    
    public NurCmdCustomExchange(final NurApi api, final int passwd, final boolean secured, final CustomExchangeParams params) throws NurApiException, Exception {
        super(63, 0, 5 + CustomExchangeParams.getByteLength(params));
        this.mApi = null;
        this.mParams = null;
        this.mResponse = null;
        this.mCbSecured = false;
        this.mCbPwd = 0;
        this.mSb = null;
        this.mApi = api;
        this.mSb = new NurRWSingulationBlock(0, 0, 0, null);
        this.mCbPwd = passwd;
        this.mCbSecured = secured;
        this.mParams = params;
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
        if (this.mParams != null) {
            final byte[] serialized = this.mParams.serialize(this.mApi);
            NurPacket.PacketBytes(data, offset, serialized);
            offset += serialized.length;
        }
        return offset - origOffset;
    }
    
    @Override
    public void deserializePayload(final byte[] data, final int offset, final int dataLen) throws Exception {
        final int status = this.getStatus();
        if (status == 66) {
            this.mResponse = new NurRespCustomExchange(data[0], null);
            this.status = 0;
        }
        else if (status == 0) {
            byte[] tmpData = null;
            if (dataLen > 0) {
                tmpData = new byte[dataLen];
                System.arraycopy(data, offset, tmpData, 0, dataLen);
            }
            else if (this.mParams.rxStripHandle) {
                tmpData = new byte[] { 0 };
            }
            this.mResponse = new NurRespCustomExchange(0, tmpData);
        }
    }
}
