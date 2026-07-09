// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

import java.nio.ByteBuffer;

class UnknownRecord extends Record
{
    public UnknownRecord(final ByteBuffer buffer, final String name, final Class recordClass, final long ttl, final int length) {
        super(name, recordClass, ttl);
        final byte[] toSkip = new byte[length];
        buffer.get(toSkip);
    }
}
