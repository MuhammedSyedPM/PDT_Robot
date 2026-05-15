// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class AntennaMapping
{
    public int antennaId;
    public String name;
    public static final int MAX_MAPPINGLEN = 16;
    
    public AntennaMapping() {
        this.antennaId = -1;
        this.name = "";
    }
    
    public AntennaMapping(final int id, final String name) throws NurApiException {
        this.antennaId = -1;
        this.name = "";
        if (id < 0 || id > 32 || name == null || name.length() < 1 || name.length() > 16) {
            throw new NurApiException("AntennaMapping: invalid construction.");
        }
        this.antennaId = id;
        this.name = name;
    }
}
