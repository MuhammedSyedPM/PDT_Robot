// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

import java.util.Collections;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.List;

class Service
{
    private final String name;
    private final List<String> labels;
    private static final Pattern SERVICE_PATTERN;
    
    public static Service fromName(final String name) {
        final Matcher matcher = Service.SERVICE_PATTERN.matcher(name);
        if (matcher.find()) {
            return new Service(matcher.group(1));
        }
        throw new IllegalArgumentException("Name does not match service syntax");
    }
    
    private Service(final String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("A Service's name can't be null or empty");
        }
        this.name = name;
        this.labels = Arrays.asList(name.split("\\."));
    }
    
    public String getName() {
        return this.name;
    }
    
    public List<String> getLabels() {
        return Collections.unmodifiableList((List<? extends String>)this.labels);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final Service service = (Service)o;
        return this.name.equals(service.name);
    }
    
    @Override
    public int hashCode() {
        return this.name.hashCode();
    }
    
    @Override
    public String toString() {
        return "Service{name='" + this.name + '\'' + ", labels=" + this.labels + '}';
    }
    
    static {
        SERVICE_PATTERN = Pattern.compile("^((_[a-zA-Z0-9_\\-]+\\.)?_(tcp|udp))\\.?|$");
    }
}
