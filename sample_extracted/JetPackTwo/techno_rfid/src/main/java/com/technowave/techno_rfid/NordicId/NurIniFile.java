// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

import java.util.Iterator;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.regex.Matcher;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class NurIniFile
{
    private Pattern mSectionPat;
    private Pattern mKeyValuePat;
    private Map<String, Map<String, String>> mEntries;
    
    public NurIniFile() {
        this.mSectionPat = Pattern.compile("\\s*\\[([^]]*)\\]\\s*");
        this.mKeyValuePat = Pattern.compile("\\s*([^=]*)=(.*)");
        this.mEntries = new HashMap<String, Map<String, String>>();
    }
    
    public NurIniFile(final String path) throws IOException {
        this.mSectionPat = Pattern.compile("\\s*\\[([^]]*)\\]\\s*");
        this.mKeyValuePat = Pattern.compile("\\s*([^=]*)=(.*)");
        this.mEntries = new HashMap<String, Map<String, String>>();
        this.load(path);
    }
    
    public void load(final String path) throws IOException {
        BufferedReader br = null;
        FileReader fr = null;
        try {
            fr = new FileReader(path);
            br = new BufferedReader(fr);
            String section = null;
            String line;
            while ((line = br.readLine()) != null) {
                Matcher m = this.mSectionPat.matcher(line);
                if (m.matches()) {
                    section = m.group(1).trim();
                    if (!section.equals("Nur")) {
                        continue;
                    }
                    section = "NUR";
                }
                else {
                    if (section == null) {
                        continue;
                    }
                    m = this.mKeyValuePat.matcher(line);
                    if (!m.matches()) {
                        continue;
                    }
                    final String key = m.group(1).trim();
                    final String value = m.group(2).trim();
                    this.put(section, key, value);
                }
            }
        }
        finally {
            if (br != null) {
                br.close();
            }
            if (fr != null) {
                fr.close();
            }
        }
    }
    
    public void save(final String path) throws IOException {
        BufferedWriter bw = null;
        FileWriter fw = null;
        try {
            fw = new FileWriter(path, false);
            bw = new BufferedWriter(fw);
            for (final Map.Entry<String, Map<String, String>> secEntry : this.mEntries.entrySet()) {
                final String section = secEntry.getKey();
                final Map<String, String> vals = secEntry.getValue();
                bw.write("[" + section + "]\n");
                for (final Map.Entry<String, String> valEntry : vals.entrySet()) {
                    bw.write(valEntry.getKey());
                    bw.write("=");
                    bw.write(valEntry.getValue());
                    bw.write("\n");
                }
            }
        }
        finally {
            if (bw != null) {
                bw.close();
            }
            if (fw != null) {
                fw.close();
            }
        }
    }
    
    public void put(final String section, final String key, final String value) {
        Map<String, String> kv = this.mEntries.get(section);
        if (kv == null) {
            this.mEntries.put(section, kv = new HashMap<String, String>());
        }
        kv.put(key, value);
    }
    
    public void put(final String section, final String key, final int value) {
        this.put(section, key, Integer.toString(value));
    }
    
    public void put(final String section, final String key, final double value) {
        this.put(section, key, Double.toString(value));
    }
    
    public String get(final String section, final String key) {
        return this.getString(section, key, null);
    }
    
    public String getString(final String section, final String key, final String defaultvalue) {
        final Map<String, String> kv = this.mEntries.get(section);
        if (kv == null) {
            return defaultvalue;
        }
        return kv.get(key);
    }
    
    public int getInt(final String section, final String key, final int defaultvalue) {
        final Map<String, String> kv = this.mEntries.get(section);
        if (kv == null) {
            return defaultvalue;
        }
        return Integer.parseInt(kv.get(key));
    }
    
    public float getFloat(final String section, final String key, final float defaultvalue) {
        final Map<String, String> kv = this.mEntries.get(section);
        if (kv == null) {
            return defaultvalue;
        }
        return Float.parseFloat(kv.get(key));
    }
    
    public double getDouble(final String section, final String key, final double defaultvalue) {
        final Map<String, String> kv = this.mEntries.get(section);
        if (kv == null) {
            return defaultvalue;
        }
        return Double.parseDouble(kv.get(key));
    }
}
