// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurRespTagTrackingStream
{
    public boolean stopped;
    public int roundsDone;
    public int collisions;
    public int Q;
    public int lastScanMask;
    public int antennaMask;
    public static final int SZ_TTSTREAM_HDR = 13;
    
    public NurRespTagTrackingStream(final byte[] headerData) throws NurApiException {
        this.stopped = false;
        this.roundsDone = 0;
        this.collisions = 0;
        this.Q = 0;
        this.lastScanMask = 0;
        this.antennaMask = 0;
        int srcOffset = 0;
        if (headerData == null || headerData.length < 13) {
            throw new NurApiException("NurRespTagTrackingStream: invalid length", 5);
        }
        this.stopped = (NurPacket.BytesToByte(headerData, srcOffset++) != 0);
        this.roundsDone = NurPacket.BytesToByte(headerData, srcOffset++);
        this.collisions = NurPacket.BytesToWord(headerData, srcOffset);
        srcOffset += 2;
        this.Q = NurPacket.BytesToByte(headerData, srcOffset++);
        this.lastScanMask = NurPacket.BytesToDword(headerData, srcOffset);
        srcOffset += 4;
        this.antennaMask = NurPacket.BytesToDword(headerData, srcOffset);
    }
}
