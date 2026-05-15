// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdCustomBlockWriteTag extends NurCmdCustomWriteTag
{
    public static final int CMD = 62;
    
    public NurCmdCustomBlockWriteTag(final int wrCmd, final int cmdBits, final int wrBank, final int bankBits, final int passwd, final boolean secured, final int sBank, final int sAddress, final int sMaskBitLength, final byte[] sMask, final int wrAddress, final int wrByteCount, final byte[] wrBuffer) throws NurApiException {
        super(62, wrCmd, cmdBits, wrBank, bankBits, passwd, secured, sBank, sAddress, sMaskBitLength, sMask, wrAddress, wrByteCount, wrBuffer);
    }
}
