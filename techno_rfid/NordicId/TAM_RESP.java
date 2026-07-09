// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class TAM_RESP
{
    public boolean response;
    public boolean ok;
    public int C_TAM;
    public int TRnd32;
    public byte[] firstBlock;
    public byte[] blockData;
    
    public TAM_RESP() {
        this.response = false;
        this.ok = false;
        this.C_TAM = 0;
        this.TRnd32 = 0;
        this.firstBlock = null;
        this.blockData = null;
    }
}
