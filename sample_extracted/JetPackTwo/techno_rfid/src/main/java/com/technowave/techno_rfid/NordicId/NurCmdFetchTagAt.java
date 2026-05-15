// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdFetchTagAt extends NurCmd
{
    public static final int CMD = 6;
    public static final int CMD_META = 7;
    private int mTagNum;
    private boolean mXpcRemove;
    private NurTag mTag;
    
    private void parseSingleResponse(final boolean isMeta, final boolean isIrData, final byte[] data, final int offset, final int dataLen) {
        NurApi.XPCSpec xpc = null;
        int blockLen = 0;
        int epcLen = 0;
        int irDataLen = 0;
        int timeStamp = 0;
        int rssi = 0;
        int scaledRssi = 0;
        int freq = 0;
        int pc = 0;
        int chan = 0;
        int antId = 0;
        byte[] epc = null;
        byte[] irData = null;
        int xpc_w1 = 0;
        int xpc_w2 = 0;
        int ptr = offset;
        blockLen = data[ptr++];
        if (!isMeta) {
            antId = data[ptr++];
            epcLen = blockLen - 1;
            epc = new byte[blockLen - 1];
            System.arraycopy(data, ptr, epc, 0, epcLen);
            this.mTag = new NurTag(this.mOwner, antId, epc);
        }
        else {
            rssi = data[ptr++];
            --blockLen;
            scaledRssi = data[ptr++];
            timeStamp = NurPacket.BytesToWord(data, ptr);
            ptr += 2;
            freq = NurPacket.BytesToDword(data, ptr);
            ptr += 4;
            if (isIrData) {
                irDataLen = data[ptr++];
                irData = new byte[irDataLen];
                blockLen -= 12;
            }
            else {
                blockLen -= 11;
            }
            pc = NurPacket.BytesToWord(data, ptr);
            ptr += 2;
            chan = data[ptr++];
            antId = data[ptr++];
            epcLen = blockLen - irDataLen;
            epc = new byte[epcLen];
            System.arraycopy(data, ptr, epc, 0, epcLen);
            if (this.mXpcRemove) {
                xpc = NurApi.getEpcXpcSpec(pc, epc);
                if (xpc != null) {
                    epc = xpc.modifiedEpc;
                    xpc_w1 = xpc.xpc_w1;
                    xpc_w2 = xpc.xpc_w2;
                }
            }
            if (irDataLen > 0) {
                ptr += epcLen;
                irData = new byte[irDataLen];
                System.arraycopy(data, ptr, irData, 0, irDataLen);
            }
            this.mTag = new NurTag(this.mOwner, timeStamp, rssi, scaledRssi, freq, pc, chan, antId, epc, irData, xpc_w1, xpc_w2);
        }
    }
    
    public NurCmdFetchTagAt(final boolean includeMeta, final int num, final boolean xpcRemove) {
        super(includeMeta ? 7 : 6, 0, 4);
        this.mTagNum = 0;
        this.mXpcRemove = true;
        this.mTag = null;
        this.mTagNum = num;
        this.mXpcRemove = xpcRemove;
    }
    
    public NurCmdFetchTagAt(final boolean includeMeta, final int num) {
        super(includeMeta ? 7 : 6, 0, 4);
        this.mTagNum = 0;
        this.mXpcRemove = true;
        this.mTag = null;
        this.mTagNum = num;
        this.mXpcRemove = true;
    }
    
    public NurTag getResponse() {
        return this.mTag;
    }
    
    @Override
    public int serializePayload(final byte[] data, final int offset) {
        return NurPacket.PacketDword(data, offset, this.mTagNum);
    }
    
    @Override
    public void deserializePayload(final byte[] data, final int offset, final int dataLen) {
        boolean isMeta = false;
        boolean isIrData = false;
        if (this.command == 7) {
            isMeta = true;
            if ((this.getFlags() & 0x2) != 0x0) {
                isIrData = true;
            }
        }
        this.parseSingleResponse(isMeta, isIrData, data, offset, dataLen);
    }
}
