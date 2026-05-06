// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.Hashtable;

public class NurTagStorage
{
    private boolean mRemoveXPC;
    private NurApi mApi;
    Hashtable<String, NurTag> mTagHash;
    public ArrayList<NurTag> mTagList;
    
    public NurTagStorage() {
        this.mRemoveXPC = true;
        this.mApi = null;
        this.mTagHash = new Hashtable<String, NurTag>();
        this.mTagList = new ArrayList<NurTag>();
    }
    
    public void setAPI(final NurApi api) {
        this.mApi = api;
    }
    
    public boolean hasTag(final String epc) {
        return this.mTagHash.containsKey(epc);
    }
    
    public boolean hasTag(final byte[] epc) {
        return this.hasTag(NurApi.byteArrayToHexString(epc));
    }
    
    public boolean hasTag(final NurTag tag) {
        return this.hasTag(tag.getEpcString());
    }
    
    public NurTag getTag(final String epc) {
        return this.mTagHash.get(epc);
    }
    
    public NurTag getTag(final byte[] epc) {
        return this.mTagHash.get(NurApi.byteArrayToHexString(epc));
    }
    
    public NurTag get(final int index) {
        return this.mTagList.get(index);
    }
    
    public int indexOf(final String epc) {
        int i = 0;
        for (final NurTag t : this.mTagList) {
            if (t.getEpcString().equals(epc)) {
                return i;
            }
            ++i;
        }
        return -1;
    }
    
    public int indexOf(final byte[] epc) {
        int i = 0;
        for (final NurTag t : this.mTagList) {
            if (t.getEpcString().equals(NurApi.byteArrayToHexString(epc))) {
                return i;
            }
            ++i;
        }
        return -1;
    }
    
    public boolean removeTag(final String epc) {
        if (epc == null) {
            return false;
        }
        final NurTag t = this.mTagHash.get(epc);
        return t != null && this.remove(this.indexOf(epc));
    }
    
    public boolean removeTag(final byte[] epc) {
        return epc != null && this.remove(this.indexOf(NurApi.byteArrayToHexString(epc)));
    }
    
    public boolean remove(final int index) {
        boolean rc = false;
        synchronized (this) {
            if (index >= 0 && index < this.mTagList.size()) {
                this.mTagHash.remove(this.mTagList.get(index).getEpcString());
                this.mTagList.remove(index);
                rc = true;
            }
        }
        return rc;
    }
    
    public boolean addTag(final NurTag tag) {
        synchronized (this) {
            if (!this.hasTag(tag)) {
                if (tag.getAPI() == null && this.mApi != null) {
                    tag.setAPI(this.mApi);
                }
                this.mTagHash.put(tag.getEpcString(), tag);
                this.mTagList.add(tag);
                tag.tagUpdated = 1;
                tag.oldAntennaID = tag.mAntennaId;
                tag.oldRssi = tag.mRssi;
                tag.oldScaledRssi = tag.mScaledRssi;
                return true;
            }
            final NurTag upd = this.getTag(tag.getEpcString());
            upd.tagUpdated = 2;
            upd.oldAntennaID = upd.mAntennaId;
            upd.oldRssi = upd.mRssi;
            upd.oldScaledRssi = upd.mScaledRssi;
            upd.mAntennaId = tag.getAntennaId();
            upd.mChannel = tag.getChannel();
            upd.mFreq = tag.getFreq();
            upd.mRssi = tag.getRssi();
            upd.mScaledRssi = tag.getScaledRssi();
            upd.mTimestamp = tag.getTimestamp();
            if (tag.getIrData() == null) {
                upd.mIRData = null;
            }
            else {
                if (upd.mIRData == null) {
                    upd.mIRData = new byte[tag.getIrData().length];
                }
                else if (upd.mIRData.length != tag.getIrData().length) {
                    upd.mIRData = new byte[tag.getIrData().length];
                }
                System.arraycopy(tag.getIrData(), 0, upd.mIRData, 0, tag.getIrData().length);
            }
            final NurTag nurTag = upd;
            ++nurTag.mUpdateCount;
            return false;
        }
    }
    
    public int size() {
        return this.mTagList.size();
    }
    
    public void clear() {
        synchronized (this) {
            this.mTagHash.clear();
            this.mTagList.clear();
        }
    }
    
    public int parseIdBuffer(final NurApi api, final byte[] data, final int offset, final int dataLen, final boolean includeMeta, final boolean isIrData) {
        int tagsAdded = 0;
        int pos = 0;
        int timestamp = 0;
        int rssi = 0;
        int scaledRssi = 0;
        int freq = 0;
        int pc = 0;
        int channel = 0;
        int antennaId = 0;
        int epcLen = 0;
        int irDataLen = 0;
        int xpc_w1 = 0;
        int xpc_w2 = 0;
        NurApi.XPCSpec xpc = null;
        synchronized (this) {
            while (pos < dataLen) {
                int blockLen = NurPacket.BytesToByte(data, offset + pos);
                ++pos;
                if (includeMeta) {
                    if (isIrData && blockLen <= 13) {
                        break;
                    }
                    if (blockLen <= 12) {
                        break;
                    }
                    rssi = data[offset + pos];
                    ++pos;
                    scaledRssi = data[offset + pos];
                    ++pos;
                    timestamp = NurPacket.BytesToWord(data, offset + pos);
                    pos += 2;
                    freq = NurPacket.BytesToDword(data, offset + pos);
                    pos += 4;
                    if (isIrData) {
                        irDataLen = data[offset + pos];
                        ++pos;
                        blockLen -= 12;
                    }
                    else {
                        blockLen -= 11;
                    }
                    pc = NurPacket.BytesToWord(data, offset + pos);
                    pos += 2;
                }
                else if (blockLen <= 1) {
                    break;
                }
                if (includeMeta) {
                    channel = NurPacket.BytesToByte(data, offset + pos);
                    ++pos;
                }
                antennaId = NurPacket.BytesToByte(data, offset + pos);
                ++pos;
                epcLen = --blockLen - irDataLen;
                byte[] epcArray = new byte[epcLen];
                byte[] irDataArray = null;
                System.arraycopy(data, offset + pos, epcArray, 0, epcLen);
                if (this.mRemoveXPC) {
                    xpc = NurApi.getEpcXpcSpec(pc, epcArray);
                    if (xpc != null) {
                        epcArray = xpc.modifiedEpc;
                        xpc_w1 = xpc.xpc_w1;
                        xpc_w2 = xpc.xpc_w2;
                    }
                }
                if (irDataLen > 0) {
                    irDataArray = new byte[irDataLen];
                    System.arraycopy(data, offset + pos + epcLen, irDataArray, 0, irDataLen);
                }
                NurTag tag;
                if (includeMeta) {
                    tag = new NurTag(api, timestamp, rssi, scaledRssi, freq, pc, channel, antennaId, epcArray, irDataArray, xpc_w1, xpc_w2);
                }
                else {
                    tag = new NurTag(api, antennaId, epcArray);
                }
                if (this.addTag(tag)) {
                    ++tagsAdded;
                }
                pos += blockLen;
            }
        }
        return tagsAdded;
    }
    
    public boolean xpcRemovalEnabled() {
        return this.mRemoveXPC;
    }
    
    public void enableXPCRemoval(final boolean enable) {
        this.mRemoveXPC = enable;
    }
}
