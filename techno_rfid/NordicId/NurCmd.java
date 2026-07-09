// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmd
{
    protected int command;
    protected int flags;
    protected int status;
    protected int payloadLen;
    protected NurApi mOwner;
    protected int timeout;
    
    protected NurCmd(final int cmd, final int flags, final int payLen) {
        this.command = 0;
        this.flags = 0;
        this.status = 0;
        this.payloadLen = 0;
        this.mOwner = null;
        this.timeout = 0;
        this.command = cmd;
        this.payloadLen = payLen;
    }
    
    protected NurCmd(final int cmd, final int flags) {
        this(cmd, flags, 0);
    }
    
    protected NurCmd(final int cmd) {
        this(cmd, 0, 0);
    }
    
    public NurApi getOwner() {
        return this.mOwner;
    }
    
    public void setOwner(final NurApi api) {
        this.mOwner = api;
    }
    
    public int getCommand() {
        return this.command;
    }
    
    public int getFlags() {
        return this.flags;
    }
    
    public int getStatus() {
        return this.status;
    }
    
    public int getPayloadLength() {
        return this.payloadLen;
    }
    
    public int getTimeout() {
        return this.timeout;
    }
    
    public void setTimeout(final int ms) {
        this.timeout = ms;
    }
    
    public void setStatus(final int flags, final int status) {
        this.flags = flags;
        this.status = status;
    }
    
    public int serializePayload(final byte[] data, final int offset) throws Exception {
        return 0;
    }
    
    public void deserializePayload(final byte[] data, final int offset, final int dataLen) throws Exception {
    }
    
    static NurCmd createCommand(final NurApi owner, final int cmd) {
        NurCmd ret = null;
        switch (cmd) {
            case 128: {
                ret = new NurCmdNotifyBoot();
                break;
            }
            case 130: {
                ret = new NurCmdNotifyInventory();
                break;
            }
            case 129: {
                ret = new NurCmdNotifyIOChange();
                break;
            }
            case 132: {
                ret = new NurCmdNotifyTraceTag();
                break;
            }
            case 133: {
                ret = new NurCmdNotifyTriggeredRead();
                break;
            }
            case 134: {
                ret = new NurCmdNotifyFrequencyHop();
                break;
            }
            case 136: {
                ret = new NurCmdNotifyInventoryEx();
                break;
            }
            case 135: {
                ret = new NurCmdNotifyDebugMsg();
                break;
            }
            case 137: {
                ret = new NurCmdNotifyNxpAlarm();
                break;
            }
            case 138: {
                ret = new NurCmdNotifyEpcEnum();
                break;
            }
            case 141: {
                ret = new NurCmdNotifyAutotune();
                break;
            }
            case 143: {
                ret = new NurCmdNotifyDiagReport();
                break;
            }
            case 144: {
                if (owner.mAccessoryEventListener != null) {
                    ret = new NurCmdNotifyAccessory();
                    break;
                }
                ret = new NurCmdUnknownEvent(cmd);
                break;
            }
            default: {
                ret = new NurCmdUnknownEvent(cmd);
                break;
            }
        }
        if (ret != null) {
            ret.setOwner(owner);
        }
        return ret;
    }
}
