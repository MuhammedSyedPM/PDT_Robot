// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class CustomExchangeParams
{
    public static final int MIN_TIMEOUT = 20;
    public static final int MAX_TIMEOUT = 100;
    public int txLen;
    public int rxLen;
    public int rxTimeout;
    public boolean asWrite;
    public boolean appendHandle;
    public boolean xorRN16;
    public boolean txOnly;
    public boolean noTxCRC;
    public boolean noRxCRC;
    public boolean txCRC5;
    public boolean rxLenUnknown;
    public boolean rxStripHandle;
    public byte[] bitBuffer;
    
    public CustomExchangeParams() {
        this.txLen = 0;
        this.rxLen = 0;
        this.rxTimeout = 20;
        this.asWrite = false;
        this.appendHandle = true;
        this.xorRN16 = false;
        this.txOnly = false;
        this.noTxCRC = false;
        this.noRxCRC = false;
        this.txCRC5 = false;
        this.rxLenUnknown = false;
        this.rxStripHandle = false;
        this.bitBuffer = new byte[128];
    }
    
    public byte[] serialize(final NurApi api) throws Exception {
        int xFlags = 0;
        int offset = 0;
        final byte[] serialized = new byte[getByteLength(this)];
        if (this.asWrite) {
            xFlags |= 0x1;
        }
        if (this.appendHandle) {
            xFlags |= 0x2;
        }
        if (this.xorRN16) {
            xFlags |= 0x4;
        }
        if (this.txOnly) {
            xFlags |= 0x8;
        }
        if (this.noTxCRC) {
            xFlags |= 0x10;
        }
        if (this.noRxCRC) {
            xFlags |= 0x20;
        }
        if (this.txCRC5) {
            xFlags |= 0x40;
        }
        if (this.rxLenUnknown) {
            xFlags |= 0x80;
        }
        if (this.rxStripHandle) {
            xFlags |= 0x100;
        }
        if (api != null && api.getCustomReselect()) {
            xFlags |= 0x200;
        }
        offset += NurPacket.PacketWord(serialized, offset, xFlags);
        offset += NurPacket.PacketWord(serialized, offset, this.txLen);
        offset += NurPacket.PacketWord(serialized, offset, this.rxLen);
        offset += NurPacket.PacketByte(serialized, offset, this.rxTimeout);
        NurPacket.PacketBytes(serialized, offset, this.bitBuffer, NurApi.bitLenToByteLen(this.txLen));
        return serialized;
    }
    
    public static int getByteLength(final CustomExchangeParams params) throws Exception {
        return 7 + NurApi.bitLenToByteLen(params.txLen);
    }
}
