// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurPacketHandler
{
    public static final int IDLE = 0;
    public static final int HEADER = 1;
    public static final int PAYLOAD = 2;
    public static final int READY = 3;
    public static final int ERROR = 4;
    NurApi mOwner;
    int state;
    long lastReceived;
    NurPacketHeader header;
    byte[] headerData;
    int headerDataPos;
    byte[] payloadData;
    int payloadDataPos;
    RingBuffer mRingBuf;
    
    public NurPacketHandler(final NurApi owner) {
        this.state = 0;
        this.lastReceived = 0L;
        this.header = new NurPacketHeader();
        this.headerData = new byte[6];
        this.headerDataPos = 0;
        this.payloadData = null;
        this.payloadDataPos = 0;
        this.mRingBuf = new RingBuffer();
        this.mOwner = owner;
    }
    
    public synchronized boolean isEmpty() {
        return this.mRingBuf.isEmpty();
    }
    
    public int getState() {
        return this.state;
    }
    
    public NurPacketHeader getHeader() {
        return this.header;
    }
    
    public byte[] getPayload() {
        return this.payloadData;
    }
    
    synchronized void writeData(final byte[] data, final int dataLen) {
        this.mRingBuf.Write(data, dataLen);
    }
    
    synchronized int handleData() {
        int readLen = 0;
        int garbage = 0;
        if (this.mRingBuf.isEmpty()) {
            this.mOwner.Log(1, "handleData() EMPTY");
            return this.state;
        }
        if (this.state == 3 || this.state == 4) {
            this.state = 0;
        }
        else if (this.state != 0 && System.currentTimeMillis() > this.lastReceived + 2000L) {
            this.mOwner.Log(2, "handleData() Too much time since last data; " + (System.currentTimeMillis() - this.lastReceived));
            this.state = 0;
            this.headerDataPos = 0;
            this.payloadDataPos = 0;
        }
        this.lastReceived = System.currentTimeMillis();
        while (!this.mRingBuf.isEmpty()) {
            switch (this.state) {
                case 0: {
                    final int b = this.mRingBuf.PeekByte(0);
                    if (b == 165) {
                        if (garbage > 1) {
                            this.mOwner.Log(2, "Total garbage " + garbage);
                        }
                        garbage = 0;
                        this.state = 1;
                        this.headerDataPos = 0;
                        break;
                    }
                    if (garbage == 0) {
                        this.mOwner.Log(2, "Receiving garbage...");
                    }
                    ++garbage;
                    this.mRingBuf.DiscardBytes(1);
                    break;
                }
                case 1: {
                    readLen = 6 - this.headerDataPos;
                    if (readLen > this.mRingBuf.getCount()) {
                        readLen = this.mRingBuf.getCount();
                    }
                    this.mRingBuf.Read(this.headerData, this.headerDataPos, readLen);
                    this.headerDataPos += readLen;
                    if (this.headerDataPos < 6) {
                        break;
                    }
                    final int calculatedCs = NurPacket.calculateHeaderCheckSum(this.headerData);
                    final int headerCs = NurPacket.BytesToByte(this.headerData, 5);
                    if (calculatedCs != headerCs) {
                        this.mOwner.Log(2, "Invalid hdr CS");
                        return this.state = 4;
                    }
                    this.header.payloadLen = NurPacket.BytesToWord(this.headerData, 1);
                    this.header.flags = NurPacket.BytesToWord(this.headerData, 3);
                    this.state = 2;
                    this.payloadDataPos = 0;
                    if (this.payloadData == null || this.payloadData.length < this.header.payloadLen) {
                        this.payloadData = new byte[this.header.payloadLen];
                        break;
                    }
                    break;
                }
                case 2: {
                    readLen = this.header.payloadLen - this.payloadDataPos;
                    if (readLen > this.mRingBuf.getCount()) {
                        readLen = this.mRingBuf.getCount();
                    }
                    this.mRingBuf.Read(this.payloadData, this.payloadDataPos, readLen);
                    this.payloadDataPos += readLen;
                    if (this.payloadDataPos < this.header.payloadLen) {
                        break;
                    }
                    int packetCrc16 = 0;
                    packetCrc16 = (this.payloadData[this.payloadDataPos - 2] & 0xFF);
                    packetCrc16 |= (this.payloadData[this.payloadDataPos - 1] << 8 & 0xFF00);
                    packetCrc16 &= 0xFFFF;
                    final NurPacketHeader header = this.header;
                    header.payloadLen -= 2;
                    final int calculatedCrc16 = CRC16.calc(this.payloadData, 0, this.header.payloadLen);
                    if (packetCrc16 != calculatedCrc16) {
                        this.mOwner.Log(2, "Invalid payload CRC " + packetCrc16 + " != " + calculatedCrc16);
                        this.state = 4;
                        break;
                    }
                    this.state = 3;
                    break;
                }
            }
            if (this.state >= 3) {
                break;
            }
        }
        this.lastReceived = System.currentTimeMillis();
        return this.state;
    }
}
