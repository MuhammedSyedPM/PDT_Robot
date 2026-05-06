// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.net.InetAddress;

class ARecord extends Record
{
    private InetAddress address;
    
    public ARecord(final ByteBuffer buffer, final String name, final Class recordClass, final long ttl) throws UnknownHostException {
        super(name, recordClass, ttl);
        final byte[] addressBytes = new byte[4];
        buffer.get(addressBytes);
        this.address = InetAddress.getByAddress(addressBytes);
    }
    
    public InetAddress getAddress() {
        return this.address;
    }
    
    @Override
    public String toString() {
        return "ARecord{name='" + this.name + '\'' + ", recordClass=" + this.recordClass + ", ttl=" + this.ttl + ", address=" + this.address + '}';
    }
}
