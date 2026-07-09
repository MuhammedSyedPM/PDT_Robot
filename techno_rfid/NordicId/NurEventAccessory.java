// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurEventAccessory
{
    public byte source;
    public ACC_EVENT_TYPE type;
    public byte[] dataBuffer;
    
    public NurEventAccessory() {
        this.source = 0;
    }
}
