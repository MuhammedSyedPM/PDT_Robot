// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdGen2V2 extends NurCmd
{
    public static final int CMD = 70;
    public static final int SUBCMD_UNTRACEABLE = 57856;
    public static final int SUBCMD_AUTHENTICATE = 213;
    public static final int SUBCMD_READBUFFER = 210;
    public static final int V2_RX_ATTN_BIT = 1;
    public static final int V2_RESELECT_BIT = 2;
    private int mSubCmd;
    private int mFlags;
    private int mSubCmdSize;
    private static final int SZ_GEN2_CMD_HEADER = 6;
    private static final int SZ_UNTRACEBALE_PARAM = 6;
    private static final int SZ_READBUFFER_PARAM = 6;
    private static final int SZ_AUTHPARAM_HEADER = 9;
    private int mPassword;
    private boolean mSecured;
    private NurUntraceableParam mUtraceParam;
    private NurAuthenticateParam mAuthParam;
    private NurV2ReadBufferParam mRdBufferParam;
    private NurAuthenticateResp mAuthResp;
    private NurRespV2ReadBuffer mRdBufferResp;
    private NurRWSingulationBlock mSb;
    
    public NurAuthenticateResp getAuthResponse() {
        return this.mAuthResp;
    }
    
    public NurRespV2ReadBuffer getRdBufferResp() {
        return this.mRdBufferResp;
    }
    
    private static final int getParameterSize(final int maskBitLength, final int subCommand) {
        int size = 17;
        if (subCommand == 213) {
            size += 137;
        }
        else if (subCommand == 57856) {
            size += 6;
        }
        else if (subCommand == 210) {
            size += 6;
        }
        if (maskBitLength > 0) {
            size += 8 + NurApi.bitLenToByteLen(maskBitLength);
        }
        return size;
    }
    
    private void setupSelection(final int selBank, final int selAddr, final int maskBitLen, final byte[] selMask) {
        this.mSb = new NurRWSingulationBlock(selBank, selAddr, maskBitLen, selMask);
    }
    
    private void setupParameters(final NurUntraceableParam utrParam) {
        this.mUtraceParam = new NurUntraceableParam(utrParam);
        if (utrParam.rxAttn) {
            this.mFlags |= 0x1;
        }
        this.mSubCmd = 57856;
        this.mSubCmdSize = 6;
    }
    
    private void setupParameters(final NurAuthenticateParam authParam) throws NurApiException {
        this.mAuthParam = new NurAuthenticateParam(authParam);
        if (authParam.rxAttn) {
            this.mFlags |= 0x1;
        }
        if (authParam.reSelect) {
            this.mFlags |= 0x2;
        }
        this.mSubCmd = 213;
        this.mSubCmdSize = 9 + NurApi.bitLenToByteLen(authParam.msgBitLength);
    }
    
    private void setupRdBufParameters(final int bitAddress, final int bitCount) throws NurApiException {
        this.mRdBufferParam = new NurV2ReadBufferParam(bitAddress, bitCount);
        this.mSubCmd = 213;
        this.mSubCmdSize = 6;
    }
    
    public NurCmdGen2V2(final int password, final NurUntraceableParam utrParam) throws NurApiException {
        super(70, 0, getParameterSize(0, 57856));
        this.mSubCmd = 0;
        this.mFlags = 0;
        this.mSubCmdSize = 0;
        this.mPassword = 0;
        this.mSecured = false;
        this.mUtraceParam = null;
        this.mAuthParam = null;
        this.mRdBufferParam = null;
        this.mAuthResp = null;
        this.mRdBufferResp = null;
        this.mSb = new NurRWSingulationBlock();
        this.setupParameters(utrParam);
        this.mPassword = password;
        this.mSecured = true;
    }
    
    public NurCmdGen2V2(final int password, final byte[] epc, final NurUntraceableParam utrParam) throws NurApiException {
        super(70, 0, getParameterSize(epc.length * 8, 57856));
        this.mSubCmd = 0;
        this.mFlags = 0;
        this.mSubCmdSize = 0;
        this.mPassword = 0;
        this.mSecured = false;
        this.mUtraceParam = null;
        this.mAuthParam = null;
        this.mRdBufferParam = null;
        this.mAuthResp = null;
        this.mRdBufferResp = null;
        this.mSb = new NurRWSingulationBlock();
        this.setupSelection(1, 32, epc.length * 8, epc);
        this.setupParameters(utrParam);
        this.mPassword = password;
        this.mSecured = true;
    }
    
    public NurCmdGen2V2(final int password, final int selBank, final int selAddr, final int maskBitLen, final byte[] selMask, final NurUntraceableParam utrParam) throws NurApiException {
        super(70, 0, getParameterSize(maskBitLen, 57856));
        this.mSubCmd = 0;
        this.mFlags = 0;
        this.mSubCmdSize = 0;
        this.mPassword = 0;
        this.mSecured = false;
        this.mUtraceParam = null;
        this.mAuthParam = null;
        this.mRdBufferParam = null;
        this.mAuthResp = null;
        this.mRdBufferResp = null;
        this.mSb = new NurRWSingulationBlock();
        this.setupSelection(selBank, selAddr, maskBitLen, selMask);
        this.setupParameters(utrParam);
        this.mPassword = password;
        this.mSecured = true;
    }
    
    public NurCmdGen2V2(final NurAuthenticateParam authParam) throws NurApiException {
        super(70, 0, getParameterSize(0, 213));
        this.mSubCmd = 0;
        this.mFlags = 0;
        this.mSubCmdSize = 0;
        this.mPassword = 0;
        this.mSecured = false;
        this.mUtraceParam = null;
        this.mAuthParam = null;
        this.mRdBufferParam = null;
        this.mAuthResp = null;
        this.mRdBufferResp = null;
        this.mSb = new NurRWSingulationBlock();
        this.setupParameters(authParam);
        this.mPassword = 0;
        this.mSecured = false;
    }
    
    public NurCmdGen2V2(final byte[] epc, final NurAuthenticateParam authParam) throws NurApiException {
        super(70, 0, getParameterSize(epc.length * 8, 213));
        this.mSubCmd = 0;
        this.mFlags = 0;
        this.mSubCmdSize = 0;
        this.mPassword = 0;
        this.mSecured = false;
        this.mUtraceParam = null;
        this.mAuthParam = null;
        this.mRdBufferParam = null;
        this.mAuthResp = null;
        this.mRdBufferResp = null;
        this.mSb = new NurRWSingulationBlock();
        this.setupSelection(1, 32, epc.length * 8, epc);
        this.setupParameters(authParam);
        this.mPassword = 0;
        this.mSecured = false;
    }
    
    public NurCmdGen2V2(final int selBank, final int selAddr, final int maskBitLen, final byte[] selMask, final NurAuthenticateParam authParam) throws NurApiException {
        super(70, 0, getParameterSize(maskBitLen, 213));
        this.mSubCmd = 0;
        this.mFlags = 0;
        this.mSubCmdSize = 0;
        this.mPassword = 0;
        this.mSecured = false;
        this.mUtraceParam = null;
        this.mAuthParam = null;
        this.mRdBufferParam = null;
        this.mAuthResp = null;
        this.mRdBufferResp = null;
        this.mSb = new NurRWSingulationBlock();
        this.setupSelection(selBank, selAddr, maskBitLen, selMask);
        this.setupParameters(authParam);
        this.mPassword = 0;
        this.mSecured = false;
    }
    
    public NurCmdGen2V2(final boolean secured, final int password, final int selBank, final int selAddr, final int maskBitLen, final byte[] selMask, final NurV2ReadBufferParam rdParam) throws NurApiException {
        super(70, 0, getParameterSize(maskBitLen, 210));
        this.mSubCmd = 0;
        this.mFlags = 0;
        this.mSubCmdSize = 0;
        this.mPassword = 0;
        this.mSecured = false;
        this.mUtraceParam = null;
        this.mAuthParam = null;
        this.mRdBufferParam = null;
        this.mAuthResp = null;
        this.mRdBufferResp = null;
        this.mSb = new NurRWSingulationBlock();
        this.setupSelection(selBank, selAddr, maskBitLen, selMask);
        this.setupRdBufParameters(rdParam.bitAddress, rdParam.bitCount);
        this.mPassword = password;
        this.mSecured = secured;
    }
    
    private int serializeSubCommandParameters(final byte[] data, final int srcOffset) throws NurApiException {
        int currentOffset = srcOffset;
        if (this.mSubCmd == 57856) {
            currentOffset += NurPacket.PacketByte(data, currentOffset, this.mUtraceParam.setU ? 1 : 0);
            currentOffset += NurPacket.PacketByte(data, currentOffset, this.mUtraceParam.hideEPC ? 1 : 0);
            currentOffset += NurPacket.PacketByte(data, currentOffset, this.mUtraceParam.epcWordLen);
            currentOffset += NurPacket.PacketByte(data, currentOffset, this.mUtraceParam.tidPolicy);
            currentOffset += NurPacket.PacketByte(data, currentOffset, this.mUtraceParam.hideUser ? 1 : 0);
            currentOffset += NurPacket.PacketByte(data, currentOffset, this.mUtraceParam.rangePolicy);
        }
        if (this.mSubCmd == 213) {
            currentOffset += NurPacket.PacketByte(data, currentOffset, this.mAuthParam.csi);
            currentOffset += NurPacket.PacketWord(data, currentOffset, this.mAuthParam.rxLength);
            currentOffset += NurPacket.PacketWord(data, currentOffset, this.mAuthParam.timeout);
            currentOffset += NurPacket.PacketWord(data, currentOffset, this.mAuthParam.preTxWait);
            currentOffset += NurPacket.PacketWord(data, currentOffset, this.mAuthParam.msgBitLength);
            currentOffset += NurPacket.PacketBytes(data, currentOffset, this.mAuthParam.getMessage());
        }
        if (this.mSubCmd == 210) {
            currentOffset += NurPacket.PacketWord(data, currentOffset, this.mRdBufferParam.bitAddress);
            currentOffset += NurPacket.PacketWord(data, currentOffset, this.mRdBufferParam.bitCount);
            currentOffset += NurPacket.PacketWord(data, currentOffset, this.mRdBufferParam.timeout);
        }
        return currentOffset - srcOffset;
    }
    
    @Override
    public int serializePayload(final byte[] data, int offset) throws NurApiException {
        int newSize = 0;
        final int oldSize = offset;
        int cflags = 0;
        if (this.mSecured) {
            cflags |= 0x1;
        }
        cflags |= this.mSb.getFlags();
        offset += NurPacket.PacketByte(data, offset, cflags);
        offset += NurPacket.PacketDword(data, offset, this.mPassword);
        offset = this.mSb.serialize(data, offset);
        offset += NurPacket.PacketWord(data, offset, this.mSubCmd);
        offset += NurPacket.PacketWord(data, offset, this.mFlags);
        offset += NurPacket.PacketWord(data, offset, this.mSubCmdSize);
        offset += this.serializeSubCommandParameters(data, offset);
        newSize = offset - oldSize;
        return newSize;
    }
    
    @Override
    public void deserializePayload(final byte[] data, final int offset, final int dataLen) {
        final int cmdStatus = this.getStatus();
        int srcOffset = offset;
        final int cmdEcho = NurPacket.BytesToWord(data, srcOffset);
        srcOffset += 2;
        if (cmdStatus == 0) {
            if (this.mSubCmd == 213) {
                byte tagReplyStatus = 0;
                int tagBitLen = 0;
                int actBitLen = 0;
                int copyLen = 0;
                this.mAuthResp = new NurAuthenticateResp();
                tagReplyStatus = data[srcOffset++];
                tagBitLen = NurPacket.BytesToWord(data, srcOffset);
                srcOffset += 2;
                actBitLen = NurPacket.BytesToWord(data, srcOffset);
                srcOffset += 2;
                if (tagReplyStatus == 0) {
                    this.mAuthResp.status = 0;
                    copyLen = NurApi.bitLenToByteLen(actBitLen);
                    this.mAuthResp.bitLength = actBitLen;
                }
                else if (tagReplyStatus == 2) {
                    final byte b = data[srcOffset];
                    this.mAuthResp.bitLength = 8;
                    this.mAuthResp.status = 2;
                    this.mAuthResp.tagError = b;
                    this.mAuthResp.reply = new byte[] { b };
                }
                else {
                    this.mAuthResp.reply = new byte[] { 0 };
                }
                if (copyLen > 1) {
                    System.arraycopy(data, srcOffset, this.mAuthResp.reply = new byte[copyLen], 0, copyLen);
                }
            }
            else if (this.mSubCmd == 210) {
                int byteLength = 0;
                this.mRdBufferResp = new NurRespV2ReadBuffer();
                this.mRdBufferResp.bitLength = NurPacket.BytesToWord(data, srcOffset);
                srcOffset += 2;
                byteLength = NurApi.bitLenToByteLen(this.mRdBufferResp.bitLength);
                if (byteLength > 0) {
                    System.arraycopy(data, srcOffset, this.mRdBufferResp.buffer = new byte[byteLength], 0, byteLength);
                }
                else {
                    this.mRdBufferResp.buffer = new byte[0];
                }
            }
        }
    }
}
