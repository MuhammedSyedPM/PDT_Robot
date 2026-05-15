// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdUnlockAll extends NurCmd
{
    public static final int CMD = 112;
    private int wLock;
    
    public int getResponse() {
        return this.wLock;
    }
    
    public NurCmdUnlockAll() {
        super(112);
    }
    
    @Override
    public void deserializePayload(final byte[] data, final int offset, final int dataLen) throws Exception {
        this.wLock = NurPacket.BytesToWord(data, offset);
    }
}
