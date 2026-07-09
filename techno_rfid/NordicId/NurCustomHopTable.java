// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCustomHopTable
{
    public int nChan;
    public int chTime;
    public int pauseTime;
    public int lf;
    public int Tari;
    public int[] freqs;
    
    public NurCustomHopTable() {
    }
    
    public NurCustomHopTable(final int[] freqs, final int nChan, final int chTime, final int pauseTime, final int lf, final int Tari) {
        this.freqs = freqs;
        this.nChan = nChan;
        this.chTime = chTime;
        this.pauseTime = pauseTime;
        this.lf = lf;
        this.Tari = Tari;
    }
}
