// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.List;

class Domain
{
    private final String name;
    private final List<String> labels;
    public static final Domain LOCAL;
    private static final Pattern DOMAIN_PATTERN;
    
    public static Domain fromName(final String name) {
        final Matcher matcher = Domain.DOMAIN_PATTERN.matcher(name);
        if (matcher.matches()) {
            return new Domain(matcher.group(4));
        }
        throw new IllegalArgumentException("Name does not match domain syntax");
    }
    
    private Domain(final String name) {
        this.name = name;
        this.labels = Arrays.asList(name.split("\\."));
    }
    
    public String getName() {
        return this.name;
    }
    
    public List<String> getLabels() {
        return this.labels;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final Domain domain = (Domain)o;
        return this.name.equals(domain.name);
    }
    
    @Override
    public int hashCode() {
        return this.name.hashCode();
    }
    
    @Override
    public String toString() {
        return "Domain{name='" + this.name + '\'' + ", labels=" + this.labels + '}';
    }
    
    static {
        LOCAL = new Domain("local.");
        DOMAIN_PATTERN = Pattern.compile("((.*)_(tcp|udp)\\.)?(.*?)\\.?");
    }
}
