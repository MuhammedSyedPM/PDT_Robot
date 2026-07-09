// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurTuneResponse
{
    public int antenna;
    public int frequency;
    public int I;
    public int Q;
    public double dBm;
    
    public NurTuneResponse(final int antenna, final int f, final int I, final int Q, final int idBm) {
        this.antenna = antenna;
        this.frequency = f;
        this.I = I;
        this.Q = Q;
        this.dBm = idBm;
        this.dBm /= 1000.0;
    }
}
