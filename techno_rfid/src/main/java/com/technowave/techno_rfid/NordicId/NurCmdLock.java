// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdLock extends NurCmd
{
    public static final int CMD = 54;
    private int mPasswd;
    private boolean mNoAccessPwd;
    private NurRWSingulationBlock mSb;
    private int mMemoryMask;
    private int mAction;
    
    public NurCmdLock(final boolean secured, final int passwd, final int sBank, final int sAddress, final int sMaskBitLength, final byte[] sMask, final int memoryMask, final int action, final boolean raw) throws NurApiException {
        super(54, 0, 18 + sMaskBitLength);
        this.mNoAccessPwd = false;
        this.mMemoryMask = 0;
        this.mAction = 0;
        if (action > 3 || sMaskBitLength > 255) {
            throw new NurApiException(5);
        }
        this.mNoAccessPwd = !secured;
        if (raw) {
            this.mMemoryMask = memoryMask;
            this.mAction = action;
        }
        else {
            if ((memoryMask & 0x1) != 0x0) {
                this.mMemoryMask |= 0x3;
                this.mAction |= action << 0;
            }
            if ((memoryMask & 0x2) != 0x0) {
                this.mMemoryMask |= 0xC;
                this.mAction |= action << 2;
            }
            if ((memoryMask & 0x4) != 0x0) {
                this.mMemoryMask |= 0x30;
                this.mAction |= action << 4;
            }
            if ((memoryMask & 0x8) != 0x0) {
                this.mMemoryMask |= 0xC0;
                this.mAction |= action << 6;
            }
            if ((memoryMask & 0x10) != 0x0) {
                this.mMemoryMask |= 0x300;
                this.mAction |= action << 8;
            }
        }
        if (this.mMemoryMask == 0) {
            throw new NurApiException(5);
        }
        this.mPasswd = passwd;
        this.mSb = new NurRWSingulationBlock(sBank, sAddress, sMaskBitLength, sMask);
    }
    
    @Override
    public int serializePayload(final byte[] data, int offset) throws Exception {
        final int origOffset = offset;
        int flags = 0;
        if (!this.mNoAccessPwd) {
            flags |= 0x1;
        }
        flags |= this.mSb.getFlags();
        offset += NurPacket.PacketByte(data, offset, flags);
        if (!this.mNoAccessPwd) {
            offset += NurPacket.PacketDword(data, offset, this.mPasswd);
        }
        else {
            offset += NurPacket.PacketDword(data, offset, 0);
        }
        offset = this.mSb.serialize(data, offset);
        offset += NurPacket.PacketByte(data, offset, 4);
        offset += NurPacket.PacketWord(data, offset, this.mMemoryMask);
        offset += NurPacket.PacketWord(data, offset, this.mAction);
        return offset - origOffset;
    }
}
