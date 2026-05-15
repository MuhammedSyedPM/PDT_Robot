// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurRespEpcEnum
{
    public boolean wasError;
    public boolean noAntenna;
    public boolean writeTypeError;
    public boolean tidLenError;
    public boolean epcLenError;
    public boolean bitLenError;
    public boolean modAddrError;
    public boolean modTooBig;
    
    public NurRespEpcEnum() {
        this.wasError = false;
        this.noAntenna = false;
        this.writeTypeError = false;
        this.tidLenError = false;
        this.epcLenError = false;
        this.bitLenError = false;
        this.modAddrError = false;
        this.modTooBig = false;
    }
}
