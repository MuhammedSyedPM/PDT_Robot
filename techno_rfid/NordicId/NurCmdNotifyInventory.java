// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdNotifyInventory extends NurCmd
{
    public static final int CMD = 130;
    private NurEventInventory mResp;
    
    public NurCmdNotifyInventory() {
        super(130);
        this.mResp = new NurEventInventory();
    }
    
    public NurEventInventory getResponse() {
        return this.mResp;
    }
    
    @Override
    public void deserializePayload(final byte[] data, int offset, final int dataLen) {
        this.mResp.stopped = (NurPacket.BytesToByte(data, offset++) == 1);
        this.mResp.roundsDone = NurPacket.BytesToByte(data, offset++);
        this.mResp.collisions = NurPacket.BytesToWord(data, offset);
        offset += 2;
        this.mResp.Q = NurPacket.BytesToByte(data, offset++);
        final int idBufLen = dataLen - 5;
        if (idBufLen > 0) {
            final boolean irDataPresent = (this.getFlags() & 0x2) != 0x0;
            this.mResp.tagsAdded = this.mOwner.getStorage().parseIdBuffer(this.mOwner, data, offset, idBufLen, true, irDataPresent);
        }
        else {
            this.mResp.tagsAdded = 0;
        }
    }
}
