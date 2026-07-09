// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

import java.net.DatagramPacket;
import java.io.IOException;
import java.net.UnknownHostException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.ByteBuffer;

class Question extends Message
{
    private final String qName;
    private final QType qType;
    private final QClass qClass;
    private static final short UNICAST_RESPONSE_BIT = Short.MIN_VALUE;
    
    public static Question fromBuffer(final ByteBuffer buffer) {
        final String name = Record.readNameFromBuffer(buffer);
        final QType type = QType.fromInt(buffer.getShort() & 0xFFFF);
        final QClass qClass = QClass.fromInt(buffer.getShort() & 0xFFFF);
        return new Question(name, type, qClass);
    }
    
    public Question(final String name, final QType type, final QClass qClass) {
        this.qName = name;
        this.qType = type;
        this.qClass = qClass;
        this.build();
    }
    
    public Question(final Service service, final Domain domain) {
        this.qName = service.getName() + "." + domain.getName();
        this.qType = QType.PTR;
        this.qClass = QClass.IN;
        this.build();
    }
    
    private void build() {
        this.buildHeader();
        for (final String label : this.qName.split("\\.")) {
            this.addLabelToBuffer(label);
        }
        this.addLabelToBuffer("");
        this.buffer.putShort((short)this.qType.asUnsignedShort());
        this.buffer.putShort((short)this.qClass.asUnsignedShort());
    }
    
    private void addLabelToBuffer(final String label) {
        final byte[] labelBytes = label.getBytes();
        this.buffer.put((byte)(labelBytes.length & 0xFF));
        this.buffer.put(labelBytes);
    }
    
    private void buildHeader() {
        this.buffer.putShort((short)0);
        this.buffer.put((byte)0);
        this.buffer.put((byte)0);
        this.buffer.putShort((short)1);
        this.buffer.putShort((short)0);
        this.buffer.putInt(0);
    }
    
    public void askOn(final MulticastSocket socket, final InetAddress group) throws IOException {
        try {
            this.askWithGroup(group, socket);
        }
        catch (UnknownHostException e) {
            System.err.println("UnknownHostException " + e);
        }
    }
    
    private void askWithGroup(final InetAddress group, final MulticastSocket socket) throws IOException {
        final DatagramPacket packet = new DatagramPacket(this.buffer.array(), this.buffer.position(), group, 5353);
        packet.setAddress(group);
        socket.send(packet);
    }
    
    public boolean answeredBy(final Record record) {
        return record.getName().equals(this.qName);
    }
    
    String getQName() {
        return this.qName;
    }
    
    QType getQType() {
        return this.qType;
    }
    
    QClass getQClass() {
        return this.qClass;
    }
    
    @Override
    public String toString() {
        return "Question{qName=" + this.qName + ", qType=" + this.qType + ", qClass=" + this.qClass + '}';
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final Question question = (Question)o;
        return this.qName.equals(question.qName) && this.qType == question.qType && this.qClass == question.qClass;
    }
    
    @Override
    public int hashCode() {
        int result = this.qName.hashCode();
        result = 31 * result + this.qType.hashCode();
        result = 31 * result + this.qClass.hashCode();
        return result;
    }
    
    public enum QType
    {
        A(1), 
        NS(2), 
        CNAME(5), 
        SOA(6), 
        MB(7), 
        MG(8), 
        MR(9), 
        NULL(10), 
        WKS(11), 
        PTR(12), 
        HINFO(13), 
        MINFO(14), 
        MX(15), 
        TXT(16), 
        AAAA(28), 
        SRV(33), 
        ANY(255);
        
        private final int value;
        
        public static QType fromInt(final int val) {
            for (final QType type : values()) {
                if (type.value == val) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Can't convert " + val + " to a QType");
        }
        
        private QType(final int value) {
            this.value = value;
        }
        
        public int asUnsignedShort() {
            return this.value & 0xFFFF;
        }
    }
    
    public enum QClass
    {
        IN(1), 
        ANY(255);
        
        private final int value;
        
        public static QClass fromInt(final int val) {
            for (final QClass c : values()) {
                if (c.value == (val & 0x7FFF)) {
                    return c;
                }
            }
            throw new IllegalArgumentException("Can't convert " + val + " to a QClass");
        }
        
        private QClass(final int value) {
            this.value = value;
        }
        
        public int asUnsignedShort() {
            return this.value & 0xFFFF;
        }
    }
}
