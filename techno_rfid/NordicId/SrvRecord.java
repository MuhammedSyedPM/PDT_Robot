// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

import java.nio.ByteBuffer;

class SrvRecord extends Record
{
    private final int priority;
    private final int weight;
    private final int port;
    private final String target;
    
    public SrvRecord(final ByteBuffer buffer, final String name, final Class recordClass, final long ttl) {
        super(name, recordClass, ttl);
        this.priority = (buffer.getShort() & 0xFFFF);
        this.weight = (buffer.getShort() & 0xFFFF);
        this.port = (buffer.getShort() & 0xFFFF);
        this.target = Record.readNameFromBuffer(buffer);
    }
    
    public int getPriority() {
        return this.priority;
    }
    
    public int getWeight() {
        return this.weight;
    }
    
    public int getPort() {
        return this.port;
    }
    
    public String getTarget() {
        return this.target;
    }
    
    @Override
    public String toString() {
        return "SrvRecord{name='" + this.name + '\'' + ", recordClass=" + this.recordClass + ", ttl=" + this.ttl + ", priority=" + this.priority + ", weight=" + this.weight + ", port=" + this.port + ", target='" + this.target + '\'' + '}';
    }
}
