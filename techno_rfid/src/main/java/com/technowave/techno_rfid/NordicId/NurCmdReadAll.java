// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdReadAll extends NurCmd
{
    public static final int CMD = 151;
    
    public NurCmdReadAll() {
        super(151);
    }
    
    @Override
    public void deserializePayload(final byte[] data, final int offset, final int dataLen) {
    }
}
