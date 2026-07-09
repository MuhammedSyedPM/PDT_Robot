// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdBlockWrite extends NurCmdWriteTag
{
    public static final int CMD = 53;
    
    public NurCmdBlockWrite(final int passwd, final boolean secured, final int sBank, final int sAddress, final int sMaskBitLength, final byte[] sMask, final int wrBank, final int wrAddress, final int wrByteCount, final byte[] wrData) throws NurApiException {
        super(53, passwd, secured, sBank, sAddress, sMaskBitLength, sMask, wrBank, wrAddress, wrByteCount, wrData);
    }
}
