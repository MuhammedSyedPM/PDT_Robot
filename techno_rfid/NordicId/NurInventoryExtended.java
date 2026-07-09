// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurInventoryExtended
{
    public int Q;
    public int session;
    public int rounds;
    public int transitTime;
    public int inventoryTarget;
    public int inventorySelState;
    
    public NurInventoryExtended() {
        this.transitTime = 0;
        this.inventoryTarget = 0;
        this.inventorySelState = 0;
    }
    
    public NurInventoryExtended(final int Q, final int session, final int rounds) {
        this.transitTime = 0;
        this.inventoryTarget = 0;
        this.inventorySelState = 0;
        this.Q = Q;
        this.session = session;
        this.rounds = rounds;
    }
}
