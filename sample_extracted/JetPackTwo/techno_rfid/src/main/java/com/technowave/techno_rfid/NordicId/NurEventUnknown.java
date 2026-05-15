// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurEventUnknown
{
    private int cmd;
    private int flags;
    private int status;
    public byte[] mEventData;
    
    public NurEventUnknown() {
        this.cmd = 0;
        this.flags = 0;
        this.status = 0;
        this.mEventData = null;
    }
    
    public void setCommand(final int cmd) {
        this.cmd = cmd;
    }
    
    public void setStatus(final int flags, final int status) {
        this.flags = flags;
        this.status = status;
    }
    
    public int getCommand() {
        return this.cmd;
    }
    
    public int getStatus() {
        return this.status;
    }
    
    public int getFlags() {
        return this.flags;
    }
    
    public void setData(final byte[] data, final int offset, final int dataLen) {
        if (data != null) {
            System.arraycopy(data, offset, this.mEventData = new byte[dataLen], 0, dataLen);
        }
        else {
            this.mEventData = null;
        }
    }
    
    public byte[] getData() {
        return this.mEventData;
    }
    
    public byte[] getData(final int offset, final int dataLen) {
        final byte[] ret = new byte[dataLen];
        System.arraycopy(this.mEventData, offset, ret, 0, dataLen);
        return ret;
    }
    
    public byte[] getData(final int offset) {
        return this.getData(offset, this.mEventData.length - offset);
    }
}
