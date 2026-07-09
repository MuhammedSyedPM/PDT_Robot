// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Map;
import java.net.InetAddress;
import java.util.Set;

class Instance
{
    private final String name;
    private final Set<InetAddress> addresses;
    private final int port;
    private final Map<String, String> attributes;
    
    static Instance createFromRecords(final PtrRecord ptr, final Set<Record> records) {
        final String name = ptr.getUserVisibleName();
        int port = 0;
        final List<InetAddress> addresses = new ArrayList<InetAddress>();
        Map<String, String> attributes = Collections.emptyMap();
        SrvRecord srv = null;
        ARecord arec = null;
        TxtRecord txt = null;
        for (final Record rec : records) {
            if (rec instanceof SrvRecord && rec.getName().equals(ptr.getPtrName())) {
                srv = (SrvRecord)rec;
                port = srv.getPort();
            }
        }
        for (final Record rec : records) {
            if (rec instanceof ARecord) {
                arec = (ARecord)rec;
                String sarec = arec.getName();
                final String starget = srv.getTarget();
                final String sname = srv.getName();
                final String sptr = ptr.getPtrName();
                if (!sarec.equals(starget) || !sname.equals(sptr)) {
                    continue;
                }
                addresses.add(arec.getAddress());
                sarec = "";
            }
        }
        for (final Record rec : records) {
            if (rec instanceof TxtRecord && rec.getName().equals(ptr.getPtrName())) {
                txt = (TxtRecord)rec;
                attributes = txt.getAttributes();
            }
        }
        return new Instance(name, addresses, port, attributes);
    }
    
    Instance(final String name, final List<InetAddress> addresses, final int port, final Map<String, String> attributes) {
        this.name = name;
        (this.addresses = new HashSet<InetAddress>()).addAll(addresses);
        this.port = port;
        this.attributes = attributes;
    }
    
    public String getName() {
        return this.name;
    }
    
    public Set<InetAddress> getAddresses() {
        return Collections.unmodifiableSet((Set<? extends InetAddress>)this.addresses);
    }
    
    public int getPort() {
        return this.port;
    }
    
    public boolean hasAttribute(final String attribute) {
        return this.attributes.containsKey(attribute);
    }
    
    public String lookupAttribute(final String attribute) {
        return this.attributes.get(attribute);
    }
    
    @Override
    public String toString() {
        return "Instance{name='" + this.name + '\'' + ", addresses=" + this.addresses + ", port=" + this.port + ", attributes=" + this.attributes + '}';
    }
    
    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + this.getName().hashCode();
        result = 31 * result + this.getPort();
        for (final InetAddress address : this.getAddresses()) {
            result = 31 * result + address.hashCode();
        }
        for (final Map.Entry<String, String> entry : this.attributes.entrySet()) {
            result = 31 * result + entry.hashCode();
        }
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof Instance)) {
            return false;
        }
        final Instance other = (Instance)obj;
        if (!this.getName().equals(other.getName())) {
            return false;
        }
        if (this.getPort() != other.getPort()) {
            return false;
        }
        for (final InetAddress address : this.getAddresses()) {
            if (!other.getAddresses().contains(address)) {
                return false;
            }
        }
        for (final InetAddress address : other.getAddresses()) {
            if (!this.getAddresses().contains(address)) {
                return false;
            }
        }
        for (final String key : this.attributes.keySet()) {
            if (!other.hasAttribute(key) || !other.lookupAttribute(key).equals(this.lookupAttribute(key))) {
                return false;
            }
        }
        for (final String key : other.attributes.keySet()) {
            if (!this.hasAttribute(key) || !this.lookupAttribute(key).equals(other.lookupAttribute(key))) {
                return false;
            }
        }
        return true;
    }
}
