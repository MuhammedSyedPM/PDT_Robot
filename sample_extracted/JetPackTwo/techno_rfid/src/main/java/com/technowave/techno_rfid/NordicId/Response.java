// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

import java.util.Iterator;
import java.util.Collections;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.net.DatagramPacket;
import java.util.List;

public class Response extends Message
{
    private final List<Question> questions;
    private final List<Record> records;
    private int numQuestions;
    private int numAnswers;
    private int numNameServers;
    private int numAdditionalRecords;
    private static final int QR_MASK = 32768;
    private static final int OPCODE_MASK = 30720;
    private static final int RCODE_MASK = 15;
    
    public static Response createFrom(final DatagramPacket packet) {
        final Response response = new Response(packet);
        response.parseRecords();
        return response;
    }
    
    private Response() {
        this.questions = new ArrayList<Question>();
        this.records = new ArrayList<Record>();
    }
    
    private Response(final DatagramPacket packet) {
        this();
        final byte[] dstBuffer = this.buffer.array();
        System.arraycopy(packet.getData(), packet.getOffset(), dstBuffer, 0, packet.getLength());
        this.buffer.limit(packet.getLength());
        this.buffer.position(0);
    }
    
    private void parseRecords() {
        this.parseHeader();
        for (int i = 0; i < this.numQuestions; ++i) {
            final Question question = Question.fromBuffer(this.buffer);
            this.questions.add(question);
        }
        for (int i = 0; i < this.numAnswers; ++i) {
            final Record record = Record.fromBuffer(this.buffer);
            this.records.add(record);
        }
        for (int i = 0; i < this.numNameServers; ++i) {
            final Record record = Record.fromBuffer(this.buffer);
            this.records.add(record);
        }
        for (int i = 0; i < this.numAdditionalRecords; ++i) {
            final Record record = Record.fromBuffer(this.buffer);
            this.records.add(record);
        }
    }
    
    private void parseHeader() {
        this.readUnsignedShort();
        final int codes = this.readUnsignedShort();
        if ((codes & 0x8000) != 0x8000) {
            throw new IllegalArgumentException("Packet is not a DNS response");
        }
        if ((codes & 0x7800) != 0x0) {
            throw new IllegalArgumentException("mDNS response packets can't have OPCODE values");
        }
        if ((codes & 0xF) != 0x0) {
            throw new IllegalArgumentException("mDNS response packets can't have RCODE values");
        }
        this.numQuestions = this.readUnsignedShort();
        this.numAnswers = this.readUnsignedShort();
        this.numNameServers = this.readUnsignedShort();
        this.numAdditionalRecords = this.readUnsignedShort();
    }
    
    public Set<Record> getRecords() {
        return new HashSet<Record>((Collection<? extends Record>)Collections.unmodifiableSet((Set<?>)new HashSet<Object>(this.records)));
    }
    
    public String getUserVisibleName() {
        for (final Record rec : this.records) {
            if (rec instanceof PtrRecord) {
                final PtrRecord ptr = (PtrRecord)rec;
                return ptr.getUserVisibleName();
            }
        }
        throw new IllegalStateException("Cannot call getUserVisibleName when no PTR record is available");
    }
    
    public boolean answers(final Set<Question> questions) {
        boolean match = false;
        final Iterator<Record> iterator = this.records.iterator();
        if (iterator.hasNext()) {
            final Record rec = iterator.next();
            final String name = rec.getName();
            for (final Question q : questions) {
                if (name.equals(q.getQName())) {
                    match = true;
                    break;
                }
            }
            return match;
        }
        return match;
    }
    
    @Override
    public String toString() {
        return "Response{questions=" + this.questions + ", records=" + this.records + ", numQuestions=" + this.numQuestions + ", numAnswers=" + this.numAnswers + ", numNameServers=" + this.numNameServers + ", numAdditionalRecords=" + this.numAdditionalRecords + '}';
    }
    
    int getNumQuestions() {
        return this.numQuestions;
    }
    
    int getNumAnswers() {
        return this.numAnswers;
    }
    
    int getNumNameServers() {
        return this.numNameServers;
    }
    
    int getNumAdditionalRecords() {
        return this.numAdditionalRecords;
    }
}
