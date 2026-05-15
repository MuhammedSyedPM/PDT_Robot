// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

import java.nio.ByteBuffer;

abstract class Message
{
    protected final ByteBuffer buffer;
    public static final int MAX_LENGTH = 9000;
    private static final int USHORT_MASK = 65535;
    
    protected Message() {
        this.buffer = ByteBuffer.allocate(9000);
    }
    
    protected int readUnsignedShort() {
        return this.buffer.getShort() & 0xFFFF;
    }
    
    public String dumpBuffer() {
        final StringBuilder sb = new StringBuilder();
        int length = this.buffer.position();
        if (length == 0) {
            length = this.buffer.limit();
        }
        for (int i = 0; i < length; ++i) {
            sb.append(String.format("%02x", this.buffer.get(i)));
            if ((i + 1) % 8 == 0) {
                sb.append('\n');
            }
            else if ((i + 1) % 2 == 0) {
                sb.append(' ');
            }
        }
        return sb.toString();
    }
}
