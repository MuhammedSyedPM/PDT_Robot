// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.nio.ByteBuffer;
import java.util.Map;

class TxtRecord extends Record
{
    private Map<String, String> attributes;
    
    public TxtRecord(final ByteBuffer buffer, final String name, final Class recordClass, final long ttl, final int length) {
        super(name, recordClass, ttl);
        final List<String> strings = Record.readStringsFromBuffer(buffer, length);
        this.attributes = this.parseDataStrings(strings);
    }
    
    private Map<String, String> parseDataStrings(final List<String> strings) {
        final Map<String, String> pairs = new HashMap<String, String>();
        for (int x = 0; x < strings.size(); ++x) {
            final String[] parts = strings.get(x).split("=");
            if (parts.length > 1) {
                pairs.put(parts[0], parts[1]);
            }
        }
        return pairs;
    }
    
    public Map<String, String> getAttributes() {
        return Collections.unmodifiableMap((Map<? extends String, ? extends String>)this.attributes);
    }
    
    @Override
    public String toString() {
        return "TxtRecord{name='" + this.name + '\'' + ", recordClass=" + this.recordClass + ", ttl=" + this.ttl + ", attributes=" + this.attributes + '}';
    }
}
