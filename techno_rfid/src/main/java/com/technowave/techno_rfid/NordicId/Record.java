// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.ArrayList;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

abstract class Record
{
    protected final String name;
    protected final long ttl;
    protected final Class recordClass;
    public static final int USHORT_MASK = 65535;
    public static final long UINT_MASK = 4294967295L;
    public static final String NAME_CHARSET = "UTF-8";
    
    public static Record fromBuffer(final ByteBuffer buffer) {
        final String name = readNameFromBuffer(buffer);
        final Type type = Type.fromInt(buffer.getShort() & 0xFFFF);
        final int tmp = buffer.getShort() & 0xFFFF;
        final boolean flushCache = (tmp & 0x8000) == 0x8000;
        final int rrClassByte = tmp & 0x7FFF;
        final Class recordClass = Class.fromInt(rrClassByte);
        final long ttl = (long)buffer.getInt() & 0xFFFFFFFFL;
        final int rdLength = buffer.getShort() & 0xFFFF;
        switch (type) {
            case A: {
                try {
                    return new ARecord(buffer, name, recordClass, ttl);
                }
                catch (UnknownHostException e) {
                    throw new IllegalArgumentException("Buffer does not represent a valid A record");
                }
            }
            case AAAA: {
                try {
                    return new AaaaRecord(buffer, name, recordClass, ttl);
                }
                catch (UnknownHostException e) {
                    throw new IllegalArgumentException("Buffer does not represent a valid AAAA record");
                }
            }
            case PTR: {
                return new PtrRecord(buffer, name, recordClass, ttl, rdLength);
            }
            case SRV: {
                return new SrvRecord(buffer, name, recordClass, ttl);
            }
            case TXT: {
                return new TxtRecord(buffer, name, recordClass, ttl, rdLength);
            }
            default: {
                return new UnknownRecord(buffer, name, recordClass, ttl, rdLength);
            }
        }
    }
    
    protected Record(final String name, final Class recordClass, final long ttl) {
        this.name = name;
        this.recordClass = recordClass;
        this.ttl = ttl;
    }
    
    public static String readNameFromBuffer(final ByteBuffer buffer) {
        final List<String> labels = new ArrayList<String>();
        int continueFrom = -1;
        int labelLength;
        do {
            buffer.mark();
            labelLength = (buffer.get() & 0xFF);
            if (isPointer(labelLength)) {
                buffer.reset();
                final int offset = buffer.getShort() & 0x3FFF;
                if (continueFrom < 0) {
                    continueFrom = buffer.position();
                }
                buffer.position(offset);
            }
            else {
                final String label = readLabel(buffer, labelLength);
                labels.add(label);
            }
        } while (labelLength != 0);
        if (continueFrom >= 0) {
            buffer.position(continueFrom);
        }
        String txt = "";
        for (int x = 0; x < labels.size(); ++x) {
            txt += labels.get(x);
            if (x + 1 == labels.size()) {
                break;
            }
            txt += ".";
        }
        return txt;
    }
    
    private static boolean isPointer(final int octet) {
        return (octet & 0xC0) == 0xC0;
    }
    
    private static String readLabel(final ByteBuffer buffer, final int length) {
        String label = "";
        if (length > 0) {
            final byte[] labelBuffer = new byte[length];
            buffer.get(labelBuffer);
            try {
                label = new String(labelBuffer, "UTF-8");
            }
            catch (UnsupportedEncodingException e) {
                System.err.println("UnsupportedEncoding: " + e);
            }
        }
        return label;
    }
    
    public static List<String> readStringsFromBuffer(final ByteBuffer buffer, final int length) {
        final List<String> strings = new ArrayList<String>();
        int bytesRead = 0;
        do {
            final int stringLength = buffer.get() & 0xFF;
            final String label = readLabel(buffer, stringLength);
            bytesRead += label.length() + 1;
            strings.add(label);
        } while (bytesRead < length);
        return strings;
    }
    
    public String getName() {
        return this.name;
    }
    
    public long getTTL() {
        return this.ttl;
    }
    
    @Override
    public String toString() {
        return "Record{name='" + this.name + '\'' + ", recordClass=" + this.recordClass + ", ttl=" + this.ttl + '}';
    }
    
    enum Type
    {
        UNSUPPORTED(0), 
        A(1), 
        NS(2), 
        CNAME(5), 
        SOA(6), 
        NULL(10), 
        WKS(11), 
        PTR(12), 
        HINFO(13), 
        MINFO(14), 
        MX(15), 
        TXT(16), 
        AAAA(28), 
        SRV(33);
        
        private final int value;
        
        public static Type fromInt(final int val) {
            for (final Type type : values()) {
                if (type.value == val) {
                    return type;
                }
            }
            return Type.UNSUPPORTED;
        }
        
        private Type(final int value) {
            this.value = value;
        }
        
        public int asUnsignedShort() {
            return this.value & 0xFFFF;
        }
    }
    
    enum Class
    {
        IN(1);
        
        private final int value;
        
        public static Class fromInt(final int val) {
            for (final Class c : values()) {
                if (c.value == val) {
                    return c;
                }
            }
            throw new IllegalArgumentException(String.format("Can't convert 0x%04x to a Class", val));
        }
        
        private Class(final int value) {
            this.value = value;
        }
        
        public int asUnsignedShort() {
            return this.value & 0xFFFF;
        }
    }
}
