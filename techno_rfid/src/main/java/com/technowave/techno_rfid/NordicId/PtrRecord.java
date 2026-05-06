// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

import java.nio.ByteBuffer;

class PtrRecord extends Record
{
    private final String userVisibleName;
    private final String ptrName;
    public static final String UNTITLED_NAME = "Untitled";
    
    public PtrRecord(final ByteBuffer buffer, final String name, final Class recordClass, final long ttl, final int rdLength) {
        super(name, recordClass, ttl);
        if (rdLength > 0) {
            this.ptrName = Record.readNameFromBuffer(buffer);
        }
        else {
            this.ptrName = "";
        }
        this.userVisibleName = this.buildUserVisibleName();
    }
    
    public String getPtrName() {
        return this.ptrName;
    }
    
    public String getUserVisibleName() {
        return this.userVisibleName;
    }
    
    private String buildUserVisibleName() {
        final String[] parts = this.ptrName.split("\\.");
        if (parts[0].length() > 0) {
            return parts[0];
        }
        return "Untitled";
    }
    
    @Override
    public String toString() {
        return "PtrRecord{name='" + this.name + '\'' + ", recordClass=" + this.recordClass + ", ttl=" + this.ttl + ", ptrName='" + this.ptrName + '\'' + '}';
    }
}
