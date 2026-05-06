// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdWLANFile extends NurCmd
{
    public static final int CMD = 176;
    
    private static int getCommandByteSize(final byte wlanCommand, final String strParam, final byte[] byteParam) throws Exception {
        return 0;
    }
    
    public NurCmdWLANFile(final String fileToDelete) throws Exception {
        super(176, 0, getCommandByteSize((byte)10, fileToDelete, null));
    }
}
