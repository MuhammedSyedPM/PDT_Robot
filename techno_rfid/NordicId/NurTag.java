// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurTag
{
    private NurApi mApi;
    int mTimestamp;
    int mRssi;
    int mScaledRssi;
    int mFreq;
    int mPc;
    int mChannel;
    int mAntennaId;
    int mUpdateCount;
    byte[] mEpc;
    String mEpcStr;
    int tagUpdated;
    int oldAntennaID;
    int oldRssi;
    int oldScaledRssi;
    byte[] mIRData;
    String mIRDataStr;
    Object mUserdata;
    int mXPC_W1;
    int mXPC_W2;
    
    public NurTag() {
        this.mApi = null;
        this.mTimestamp = 0;
        this.mRssi = 0;
        this.mScaledRssi = 0;
        this.mFreq = 0;
        this.mPc = 0;
        this.mChannel = 0;
        this.mAntennaId = 0;
        this.mUpdateCount = 1;
        this.mEpc = null;
        this.mEpcStr = "";
        this.tagUpdated = 0;
        this.oldAntennaID = 0;
        this.oldRssi = 0;
        this.oldScaledRssi = 0;
        this.mIRData = null;
        this.mIRDataStr = "";
        this.mUserdata = null;
        this.mXPC_W1 = 0;
        this.mXPC_W2 = 0;
    }
    
    public NurTag(final int timestamp, final int rssi, final int scaledRssi, final int freq, final int pc, final int channel, final int antennaId, final byte[] epc) {
        this.mApi = null;
        this.mTimestamp = 0;
        this.mRssi = 0;
        this.mScaledRssi = 0;
        this.mFreq = 0;
        this.mPc = 0;
        this.mChannel = 0;
        this.mAntennaId = 0;
        this.mUpdateCount = 1;
        this.mEpc = null;
        this.mEpcStr = "";
        this.tagUpdated = 0;
        this.oldAntennaID = 0;
        this.oldRssi = 0;
        this.oldScaledRssi = 0;
        this.mIRData = null;
        this.mIRDataStr = "";
        this.mUserdata = null;
        this.mXPC_W1 = 0;
        this.mXPC_W2 = 0;
        this.mTimestamp = timestamp;
        this.mRssi = rssi;
        this.mScaledRssi = scaledRssi;
        this.mFreq = freq;
        this.mPc = pc;
        this.mChannel = channel;
        this.mAntennaId = antennaId;
        this.mEpc = epc;
        this.mEpcStr = NurApi.byteArrayToHexString(this.mEpc);
    }
    
    public NurTag(final int timestamp, final int rssi, final int scaledRssi, final int freq, final int pc, final int channel, final int antennaId, final byte[] epc, final int xpc_w1, final int xpc_w2) {
        this(timestamp, rssi, scaledRssi, freq, pc, channel, antennaId, epc);
        this.mXPC_W1 = xpc_w1;
        this.mXPC_W2 = xpc_w2;
    }
    
    public NurTag(final int timestamp, final int rssi, final int scaledRssi, final int freq, final int pc, final int channel, final int antennaId, final byte[] epc, final byte[] irData) {
        this.mApi = null;
        this.mTimestamp = 0;
        this.mRssi = 0;
        this.mScaledRssi = 0;
        this.mFreq = 0;
        this.mPc = 0;
        this.mChannel = 0;
        this.mAntennaId = 0;
        this.mUpdateCount = 1;
        this.mEpc = null;
        this.mEpcStr = "";
        this.tagUpdated = 0;
        this.oldAntennaID = 0;
        this.oldRssi = 0;
        this.oldScaledRssi = 0;
        this.mIRData = null;
        this.mIRDataStr = "";
        this.mUserdata = null;
        this.mXPC_W1 = 0;
        this.mXPC_W2 = 0;
        this.mTimestamp = timestamp;
        this.mRssi = rssi;
        this.mScaledRssi = scaledRssi;
        this.mFreq = freq;
        this.mPc = pc;
        this.mChannel = channel;
        this.mAntennaId = antennaId;
        this.mEpc = epc;
        this.mIRData = irData;
        this.mEpcStr = NurApi.byteArrayToHexString(this.mEpc);
        if (this.mIRData != null) {
            this.mIRDataStr = NurApi.byteArrayToHexString(this.mIRData);
        }
    }
    
    public NurTag(final NurApi api, final int timestamp, final int rssi, final int scaledRssi, final int freq, final int pc, final int channel, final int antennaId, final byte[] epc, final byte[] irData) {
        this(timestamp, rssi, scaledRssi, freq, pc, channel, antennaId, epc, irData);
        this.mApi = api;
    }
    
    public NurTag(final int timestamp, final int rssi, final int scaledRssi, final int freq, final int pc, final int channel, final int antennaId, final byte[] epc, final byte[] irData, final int xpc_w1, final int xpc_w2) {
        this(timestamp, rssi, scaledRssi, freq, pc, channel, antennaId, epc, irData);
        this.mXPC_W1 = xpc_w1;
        this.mXPC_W2 = xpc_w2;
    }
    
    public NurTag(final NurApi api, final int timestamp, final int rssi, final int scaledRssi, final int freq, final int pc, final int channel, final int antennaId, final byte[] epc, final byte[] irData, final int xpc_w1, final int xpc_w2) {
        this(timestamp, rssi, scaledRssi, freq, pc, channel, antennaId, epc, irData);
        this.mApi = api;
        this.mXPC_W1 = xpc_w1;
        this.mXPC_W2 = xpc_w2;
    }
    
    public NurTag(final int antennaId, final byte[] epc) {
        this.mApi = null;
        this.mTimestamp = 0;
        this.mRssi = 0;
        this.mScaledRssi = 0;
        this.mFreq = 0;
        this.mPc = 0;
        this.mChannel = 0;
        this.mAntennaId = 0;
        this.mUpdateCount = 1;
        this.mEpc = null;
        this.mEpcStr = "";
        this.tagUpdated = 0;
        this.oldAntennaID = 0;
        this.oldRssi = 0;
        this.oldScaledRssi = 0;
        this.mIRData = null;
        this.mIRDataStr = "";
        this.mUserdata = null;
        this.mXPC_W1 = 0;
        this.mXPC_W2 = 0;
        this.mAntennaId = antennaId;
        this.mEpc = epc;
        this.mEpcStr = NurApi.byteArrayToHexString(this.mEpc);
        this.mIRData = null;
        this.mIRDataStr = "";
    }
    
    public NurTag(final NurApi api, final int antennaId, final byte[] epc) {
        this.mApi = null;
        this.mTimestamp = 0;
        this.mRssi = 0;
        this.mScaledRssi = 0;
        this.mFreq = 0;
        this.mPc = 0;
        this.mChannel = 0;
        this.mAntennaId = 0;
        this.mUpdateCount = 1;
        this.mEpc = null;
        this.mEpcStr = "";
        this.tagUpdated = 0;
        this.oldAntennaID = 0;
        this.oldRssi = 0;
        this.oldScaledRssi = 0;
        this.mIRData = null;
        this.mIRDataStr = "";
        this.mUserdata = null;
        this.mXPC_W1 = 0;
        this.mXPC_W2 = 0;
        this.mApi = api;
        this.mAntennaId = antennaId;
        this.mEpc = epc;
        this.mEpcStr = NurApi.byteArrayToHexString(this.mEpc);
        this.mIRData = null;
        this.mIRDataStr = "";
    }
    
    public void setAPI(final NurApi api) {
        this.mApi = api;
    }
    
    public NurApi getAPI() {
        return this.mApi;
    }
    
    public boolean isNull() {
        return this.mEpc == null;
    }
    
    public boolean hasMeta() {
        return this.mRssi != 0;
    }
    
    @Override
    public String toString() {
        return this.mEpcStr;
    }
    
    public String getEpcString() {
        return this.mEpcStr;
    }
    
    public String getDataString() {
        return this.mIRDataStr;
    }
    
    public int getTimestamp() {
        return this.mTimestamp;
    }
    
    public int getRssi() {
        return this.mRssi;
    }
    
    public int getScaledRssi() {
        return this.mScaledRssi;
    }
    
    public int getFreq() {
        return this.mFreq;
    }
    
    public int getPC() {
        return this.mPc;
    }
    
    public int getChannel() {
        return this.mChannel;
    }
    
    public int getAntennaId() {
        return this.mAntennaId;
    }
    
    public byte[] getEpc() {
        return this.mEpc;
    }
    
    public byte[] getIrData() {
        return this.mIRData;
    }
    
    public Object getUserdata() {
        return this.mUserdata;
    }
    
    public void setUserdata(final Object userdata) {
        this.mUserdata = userdata;
    }
    
    public int getUpdateCount() {
        return this.mUpdateCount;
    }
    
    public int getXPC_W1() {
        return this.mXPC_W1;
    }
    
    public int getXPC_W2() {
        return this.mXPC_W2;
    }
    
    public String getPhysicalAntenna() {
        if (this.mApi != null) {
            try {
                return this.mApi.antennaIdToPhysicalAntenna(this.mAntennaId);
            }
            catch (Exception ex) {}
        }
        return "Antenna #" + this.mAntennaId;
    }
    
    public byte[] readTag(final int rdBank, final int rdAddress, final int rdByteCount) throws Exception {
        if (this.mApi != null) {
            return this.mApi.readTagByEpc(this.mEpc, this.mEpc.length, rdBank, rdAddress, rdByteCount);
        }
        throw new Exception("readTag(): no API");
    }
    
    public byte[] readTag(final int passwd, final int rdBank, final int rdAddress, final int rdByteCount) throws Exception {
        if (this.mApi != null) {
            return this.mApi.readTagByEpc(passwd, this.mEpc, this.mEpc.length, rdBank, rdAddress, rdByteCount);
        }
        throw new Exception("readTag(): no API");
    }
    
    public void writeTag(final int wrBank, final int wrAddress, final byte[] wrData) throws Exception {
        if (this.mApi != null) {
            this.mApi.writeTagByEpc(this.mEpc, this.mEpc.length, wrBank, wrAddress, wrData.length, wrData);
            return;
        }
        throw new Exception("writeTag(): no API");
    }
    
    public void writeTag(final int passwd, final int wrBank, final int wrAddress, final byte[] wrData) throws Exception {
        if (this.mApi != null) {
            this.mApi.writeTagByEpc(passwd, this.mEpc, this.mEpc.length, wrBank, wrAddress, wrData.length, wrData);
            return;
        }
        throw new Exception("writeTag(): no API");
    }
    
    public void writeEpc(final int passwd, final byte[] newEpc) throws Exception {
        if (this.mApi != null) {
            this.writeTag(passwd, 1, 2, newEpc);
            return;
        }
        throw new Exception("writeEpc(): no API");
    }
    
    public void writeEpc(final byte[] newEpc) throws Exception {
        if (this.mApi != null) {
            this.writeTag(1, 2, newEpc);
            return;
        }
        throw new Exception("writeEpc(): no API");
    }
    
    public void lockTag(final int passwd, final int memoryMask, final int action) throws Exception {
        if (this.mApi != null) {
            this.mApi.setLockByEpc(passwd, this.mEpc, this.mEpc.length, memoryMask, action);
            return;
        }
        throw new Exception("lockTag(): no API");
    }
    
    public void killTag(final int passwd) throws Exception {
        if (this.mApi != null) {
            this.mApi.killTagByEpc(passwd, this.mEpc, this.mEpc.length);
            return;
        }
        throw new Exception("killTag(): no API");
    }
    
    public void setAccessPassword(final int newAccessPasswd) throws Exception {
        if (this.mApi != null) {
            final byte[] pwdData = { (byte)(newAccessPasswd >> 24 & 0xFF), (byte)(newAccessPasswd >> 16 & 0xFF), (byte)(newAccessPasswd >> 8 & 0xFF), (byte)(newAccessPasswd & 0xFF) };
            this.writeTag(0, 2, pwdData);
            return;
        }
        throw new Exception("setAccessPassword(): no API");
    }
    
    public void setAccessPassword(final int accessPasswd, final int newAccessPasswd) throws Exception {
        if (this.mApi != null) {
            final byte[] pwdData = { (byte)(newAccessPasswd >> 24 & 0xFF), (byte)(newAccessPasswd >> 16 & 0xFF), (byte)(newAccessPasswd >> 8 & 0xFF), (byte)(newAccessPasswd & 0xFF) };
            this.writeTag(accessPasswd, 0, 2, pwdData);
            return;
        }
        throw new Exception("setAccessPassword(): no API");
    }
    
    public void setKillPassword(final int newKillPasswd) throws Exception {
        if (this.mApi != null) {
            final byte[] pwdData = { (byte)(newKillPasswd >> 24 & 0xFF), (byte)(newKillPasswd >> 16 & 0xFF), (byte)(newKillPasswd >> 8 & 0xFF), (byte)(newKillPasswd & 0xFF) };
            this.writeTag(0, 0, pwdData);
            return;
        }
        throw new Exception("setAccessPassword(): no API");
    }
    
    public void setKillPassword(final int accessPwd, final int newKillPasswd) throws Exception {
        if (this.mApi != null) {
            final byte[] pwdData = { (byte)(newKillPasswd >> 24 & 0xFF), (byte)(newKillPasswd >> 16 & 0xFF), (byte)(newKillPasswd >> 8 & 0xFF), (byte)(newKillPasswd & 0xFF) };
            this.writeTag(accessPwd, 0, 0, pwdData);
            return;
        }
        throw new Exception("setAccessPassword(): no API");
    }
    
    public void traceTag() throws Exception {
        if (this.mApi != null) {
            this.mApi.traceTagByEpc(this.mEpc, this.mEpc.length, 3);
            return;
        }
        throw new Exception("traceTag(): no API");
    }
    
    public NurRespReadData queryTag() throws Exception {
        if (this.mApi != null) {
            return this.mApi.traceTagByEpc(this.mEpc, this.mEpc.length, 0);
        }
        throw new Exception("queryTag(): no API");
    }
    
    public void lockAccessPassword(final int accessPasswd) throws Exception {
        if (this.mApi != null) {
            this.mApi.setLockByEpc(accessPasswd, this.mEpc, this.mEpc.length, 8, 2);
            return;
        }
        throw new Exception("lockAccessPassword(): no API");
    }
    
    public void lockKillPassword(final int accessPasswd) throws Exception {
        if (this.mApi != null) {
            this.mApi.setLockByEpc(accessPasswd, this.mEpc, this.mEpc.length, 16, 2);
            return;
        }
        throw new Exception("lockKillPassword(): no API");
    }
    
    public void lockPasswords(final int accessPasswd) throws Exception {
        if (this.mApi != null) {
            final int mask = 24;
            this.mApi.setLockByEpc(accessPasswd, this.mEpc, this.mEpc.length, mask, 2);
            return;
        }
        throw new Exception("lockPasswords(): no API");
    }
    
    public NurRespCustomExchange customExchange(final int passwd, final boolean secured, final CustomExchangeParams params) throws Exception {
        return this.mApi.customExchangeByEpc(passwd, secured, this.mEpc, params);
    }
    
    public NurRespCustomExchange customExchange(final CustomExchangeParams params) throws Exception {
        return this.mApi.customExchangeByEpc(this.mEpc, params);
    }
}
