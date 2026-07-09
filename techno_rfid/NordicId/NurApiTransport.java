// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

import java.io.IOException;

public interface NurApiTransport
{
    int readData(final byte[] p0) throws IOException;
    
    int writeData(final byte[] p0, final int p1) throws IOException;
    
    void connect() throws Exception;
    
    void disconnect();
    
    boolean isConnected();
    
    boolean disableAck();
}
