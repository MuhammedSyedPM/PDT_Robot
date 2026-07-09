// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdNxpEas extends NurCmdNxpReadProtect
{
    public static final int CMD = 81;
    
    public NurCmdNxpEas(final int passwd, final boolean set, final int sBank, final int sAddress, final int sMaskBitLength, final byte[] sMask) {
        super(81, passwd, set, sBank, sAddress, sMaskBitLength, sMask);
    }
}
