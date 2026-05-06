// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class RingBuffer
{
    public static int DefaultBufferSize;
    private int mReadPos;
    private int mWritePos;
    private int mCount;
    private byte[] mData;
    private int mDataSize;
    
    public RingBuffer() {
        this(-1);
    }
    
    public RingBuffer(int size) {
        this.mReadPos = 0;
        this.mWritePos = 0;
        this.mCount = 0;
        if (size == -1) {
            size = RingBuffer.DefaultBufferSize;
        }
        this.mDataSize = size;
        this.mData = new byte[size];
    }
    
    public void Reset() {
        this.mCount = 0;
        this.mReadPos = 0;
        this.mWritePos = 0;
    }
    
    public byte[] getBuffer() {
        return this.mData;
    }
    
    public int getReadPos() {
        return this.mReadPos;
    }
    
    public int getWritePos() {
        return this.mWritePos;
    }
    
    public int getCount() {
        return this.mCount;
    }
    
    public int getDataSize() {
        return this.mDataSize;
    }
    
    public int isAvailable() {
        return this.mDataSize - this.mCount;
    }
    
    public boolean isEmpty() {
        return this.mReadPos == this.mWritePos && this.mCount == 0;
    }
    
    public boolean isFull() {
        return this.mReadPos == this.mWritePos && this.mCount > 0;
    }
    
    public void Write(final byte[] buffer) {
        this.Write(buffer, 0, buffer.length);
    }
    
    public void Write(final byte[] buffer, final int bufferLen) {
        this.Write(buffer, 0, bufferLen);
    }
    
    public void Write(final byte[] buffer, int bufferOffset, int bufferLen) {
        synchronized (this) {
            if (this.mCount + bufferLen > this.mDataSize) {
                throw new IndexOutOfBoundsException("Buffer Overflow! count, buf (total), size = " + this.mCount + ", " + bufferLen + " (" + (this.mCount + bufferLen) + "), " + this.mDataSize + ".");
            }
            if (this.mWritePos + bufferLen > this.mDataSize) {
                final int len = this.mDataSize - this.mWritePos;
                System.arraycopy(buffer, bufferOffset, this.mData, this.mWritePos, len);
                this.mCount += len;
                this.mWritePos = 0;
                bufferOffset += len;
                bufferLen -= len;
            }
            System.arraycopy(buffer, bufferOffset, this.mData, this.mWritePos, bufferLen);
            this.mCount += bufferLen;
            this.mWritePos = (this.mWritePos + bufferLen) % this.mDataSize;
        }
    }
    
    public void Read(final byte[] buffer) {
        this.Read(buffer, 0, buffer.length);
    }
    
    public void Read(final byte[] buffer, final int bufferLen) {
        this.Read(buffer, 0, bufferLen);
    }
    
    public void Read(final byte[] buffer, int bufferOffset, int bufferLen) {
        synchronized (this) {
            if (this.mCount + bufferLen > this.mDataSize) {
                throw new IndexOutOfBoundsException("Buffer Underflow!");
            }
            if (this.mReadPos + bufferLen > this.mDataSize) {
                final int len = this.mDataSize - this.mReadPos;
                System.arraycopy(this.mData, this.mReadPos, buffer, bufferOffset, len);
                this.mReadPos = 0;
                bufferOffset += len;
                bufferLen -= len;
                this.mCount -= len;
            }
            System.arraycopy(this.mData, this.mReadPos, buffer, bufferOffset, bufferLen);
            this.mCount -= bufferLen;
            this.mReadPos = (this.mReadPos + bufferLen) % this.mDataSize;
        }
    }
    
    public void Peek(final byte[] buffer) {
        this.Peek(buffer, 0, buffer.length, 0);
    }
    
    public void Peek(final byte[] buffer, final int bufferLen) {
        this.Peek(buffer, 0, bufferLen, 0);
    }
    
    public void Peek(final byte[] buffer, final int bufferOffset, final int bufferLen) {
        this.Peek(buffer, bufferOffset, bufferLen, 0);
    }
    
    public void Peek(final byte[] buffer, int bufferOffset, int bufferLen, final int srcOffset) {
        synchronized (this) {
            if (bufferLen + srcOffset > this.mCount) {
                throw new IndexOutOfBoundsException("Buffer Underflow!");
            }
            int rPos = (this.mReadPos + srcOffset) % this.mDataSize;
            if (rPos + bufferLen > this.mDataSize) {
                final int len = this.mDataSize - rPos;
                System.arraycopy(this.mData, rPos, buffer, bufferOffset, len);
                rPos = 0;
                bufferOffset += len;
                bufferLen -= len;
            }
            System.arraycopy(this.mData, rPos, buffer, bufferOffset, bufferLen);
        }
    }
    
    public void DiscardBytes(int val) {
        synchronized (this) {
            if (this.mCount < val) {
                val = this.mCount;
            }
            if (val > 0) {
                this.mCount -= val;
                this.mReadPos = (this.mReadPos + val) % this.mDataSize;
            }
        }
    }
    
    public int PeekByte(final int offset) {
        synchronized (this) {
            if (offset + 1 > this.mCount) {
                throw new IndexOutOfBoundsException("Buffer Underflow!");
            }
            final int rPos = (this.mReadPos + offset) % this.mDataSize;
            return NurPacket.BytesToByte(this.mData, rPos);
        }
    }
    
    public int PeekWord(final int offset) {
        synchronized (this) {
            return ((this.PeekByte(offset + 0) & 0xFF) | (this.PeekByte(offset + 1) & 0xFF) << 8) & 0xFFFF;
        }
    }
    
    public int PeekDword(final byte[] buf, final int offset) {
        synchronized (this) {
            return ((this.PeekByte(offset + 0) & 0xFF) | (this.PeekByte(offset + 1) & 0xFF) << 8 | (this.PeekByte(offset + 2) & 0xFF) << 16 | (this.PeekByte(offset + 3) & 0xFF) << 24) & -1;
        }
    }
    
    static {
        RingBuffer.DefaultBufferSize = 16384;
    }
}
