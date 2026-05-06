// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdNotifyDiagReport extends NurCmd
{
    public static final int CMD = 143;
    NurCmdDiagGetReport reportCmd;
    
    public NurRespDiagGetReport getResponse() {
        return this.reportCmd.getResponse();
    }
    
    protected NurCmdNotifyDiagReport() {
        super(143);
        this.reportCmd = new NurCmdDiagGetReport(0);
    }
    
    @Override
    public void deserializePayload(final byte[] data, final int offset, final int dataLen) {
        this.reportCmd.deserializePayload(data, offset, dataLen);
    }
}
