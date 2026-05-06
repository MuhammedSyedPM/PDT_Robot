// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCustomHopTableEx
{
    public int nChan;
    public int chTime;
    public int pauseTime;
    public int lf;
    public int Tari;
    public int lbtThresh;
    public int TxLevel;
    public int[] freqs;
    
    public NurCustomHopTableEx() {
    }
    
    public NurCustomHopTableEx(final int[] freqs, final int chTime, final int pauseTime, final int lf, final int Tari, final int lbtThresh, final int TxLevel) throws NurApiException {
        if (freqs == null || freqs.length > 100) {
            throw new NurApiException(5);
        }
        this.freqs = freqs;
        this.nChan = freqs.length;
        this.chTime = chTime;
        this.pauseTime = pauseTime;
        this.lf = lf;
        this.Tari = Tari;
        this.lbtThresh = lbtThresh;
        this.TxLevel = TxLevel;
    }
}
