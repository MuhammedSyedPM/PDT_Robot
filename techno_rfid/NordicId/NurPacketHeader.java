// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurPacketHeader
{
    int payloadLen;
    int flags;
    
    public NurPacketHeader() {
        this.payloadLen = 0;
        this.flags = 0;
    }
    
    public void copy(final NurPacketHeader hdr) {
        this.flags = hdr.flags;
        this.payloadLen = hdr.payloadLen;
    }
    
    public boolean compare(final NurPacketHeader hdr) {
        return hdr.flags == this.flags && hdr.payloadLen == this.payloadLen;
    }
    
    @Override
    public String toString() {
        return "HDR[" + this.flags + " " + this.payloadLen + "]";
    }
}
