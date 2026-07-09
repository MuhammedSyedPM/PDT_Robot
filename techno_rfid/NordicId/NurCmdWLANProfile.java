// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurCmdWLANProfile extends NurCmd
{
    public static final int CMD = 176;
    private static final int MIN_LENGTH_FOR_PROFILE_REPLY = 146;
    private static final int SZ_PROFILE_ADD = 66;
    private static final int SZ_ENTERPRISE_PROFILE_ADD = 130;
    private NurWLANProfile mReceivedProfile;
    private byte mTargetValue;
    private byte mCommandValue;
    private byte[] mParameterBytes;
    private String mCommandString;
    
    public NurWLANProfile getResponse() {
        return this.mReceivedProfile;
    }
    
    private void insertCharacters(final String str, int maxLength, final byte[] buffer, final int offset) {
        int count = 0;
        if (str == null || str.length() < 1) {
            for (count = 0; count < maxLength; ++count) {
                buffer[offset + count] = 0;
            }
            return;
        }
        if (maxLength < str.length()) {
            maxLength = str.length();
        }
        char[] chars;
        for (chars = str.toCharArray(), count = 0; count < maxLength && count < chars.length; ++count) {
            buffer[offset + count] = (byte)chars[count];
        }
        while (count < maxLength) {
            buffer[offset + count] = 0;
            ++count;
        }
    }
    
    private void preSerializeProfile(final String ssid, final byte secType, final String secKey, final byte priority) throws Exception {
        final String excPrefix = "NurCmdWLANProfile::add profile";
        if (ssid == null || ssid.length() < 1 || ssid.length() > 32) {
            throw new NurApiException(excPrefix + ": invalid ssid", 5);
        }
        if (secKey == null || secKey.length() < 1 || secKey.length() > 32) {
            throw new NurApiException(excPrefix + ": invalid security key", 5);
        }
        this.mParameterBytes = new byte[66];
        int offset = 0;
        this.insertCharacters(ssid, 32, this.mParameterBytes, offset);
        offset += 32;
        this.mParameterBytes[offset++] = secType;
        this.insertCharacters(secKey, 32, this.mParameterBytes, offset);
        offset += 32;
        this.mParameterBytes[offset] = priority;
        this.mCommandString = "add profile";
    }
    
    private void preSerializeEnterpriseProfile(final String ssid, final String secKey, final String user, final String anonuser, final byte eapmethod, final byte priority) throws Exception {
        final String excPrefix = "NurCmdWLANProfile::add enterprise profile";
        if (ssid == null || ssid.length() < 1 || ssid.length() > 32) {
            throw new NurApiException(excPrefix + ": invalid ssid", 5);
        }
        if (secKey == null || secKey.length() < 1 || secKey.length() > 32) {
            throw new NurApiException(excPrefix + ": invalid security key", 5);
        }
        this.mParameterBytes = new byte[130];
        int offset = 0;
        this.insertCharacters(ssid, 32, this.mParameterBytes, offset);
        offset += 32;
        this.insertCharacters(secKey, 32, this.mParameterBytes, offset);
        offset += 32;
        this.insertCharacters(user, 32, this.mParameterBytes, offset);
        offset += 32;
        this.insertCharacters(anonuser, 32, this.mParameterBytes, offset);
        offset += 32;
        this.mParameterBytes[offset++] = eapmethod;
        this.mParameterBytes[offset] = priority;
        this.mCommandString = "add enterprise profile";
    }
    
    public NurCmdWLANProfile(final String ssid, final byte secType, final String secKey, final byte priority) throws Exception {
        super(176, 0, 68);
        this.mReceivedProfile = null;
        this.mTargetValue = -1;
        this.mCommandValue = -1;
        this.mParameterBytes = null;
        this.mCommandString = "";
        this.preSerializeProfile(ssid, secType, secKey, priority);
        this.mTargetValue = 5;
        this.mCommandValue = 3;
    }
    
    public NurCmdWLANProfile(final String ssid, final String secKey, final String user, final String anonuser, final byte eapmethod, final byte priority) throws Exception {
        super(176, 0, 132);
        this.mReceivedProfile = null;
        this.mTargetValue = -1;
        this.mCommandValue = -1;
        this.mParameterBytes = null;
        this.mCommandString = "";
        this.preSerializeEnterpriseProfile(ssid, secKey, user, anonuser, eapmethod, priority);
        this.mTargetValue = 5;
        this.mCommandValue = 12;
    }
    
    public NurCmdWLANProfile(final int profileIndex, final boolean deleteProfile) throws Exception {
        super(176, 0, 3);
        this.mReceivedProfile = null;
        this.mTargetValue = -1;
        this.mCommandValue = -1;
        this.mParameterBytes = null;
        this.mCommandString = "";
        if (profileIndex < 0 || profileIndex > 7) {
            throw new NurApiException("WLAN profile delete: invalid profile index", 5);
        }
        this.mParameterBytes = new byte[] { (byte)profileIndex };
        this.mTargetValue = 5;
        if (deleteProfile) {
            this.mCommandValue = 4;
            this.mCommandString = "delete profile";
        }
        else {
            this.mCommandValue = 2;
            this.mCommandString = "get profile";
        }
    }
    
    private void processReply(final int error, final byte[] data, final int offset, final int dataLen) throws Exception {
        if (error != 0) {
            throw new NurApiException("NurCmdWlanProfile:" + this.mCommandString, error);
        }
        if (this.mCommandValue == 2) {
            this.processProfileReply(data, offset, dataLen);
        }
    }
    
    private void processProfileReply(final byte[] data, int offset, final int dataLen) throws Exception {
        if (dataLen < 146) {
            throw new NurApiException("NurCmdWLAN::getProfile", 4103);
        }
        final byte[] secKey = new byte[32];
        this.mReceivedProfile = new NurWLANProfile();
        this.mReceivedProfile.profileIndex = NurPacket.BytesToDword(data, offset);
        offset += 4;
        this.mReceivedProfile.ssid = NurPacket.BytesToString(data, offset, 32);
        offset += 32;
        System.arraycopy(data, offset, this.mReceivedProfile.mac, 0, 6);
        offset += 6;
        this.mReceivedProfile.secType = data[offset++];
        System.arraycopy(data, offset, secKey, 0, 32);
        offset += 32;
        this.mReceivedProfile.setSecurityKey(secKey);
        this.mReceivedProfile.extUser = NurPacket.BytesToString(data, offset, 32);
        offset += 32;
        this.mReceivedProfile.anonUser = NurPacket.BytesToString(data, offset, 32);
        offset += 32;
        this.mReceivedProfile.certificateIndex = data[offset++];
        this.mReceivedProfile.eapMethod = NurPacket.BytesToDword(data, offset);
        offset += 4;
        this.mReceivedProfile.priority = NurPacket.BytesToDword(data, offset);
    }
    
    @Override
    public int serializePayload(final byte[] data, final int offset) {
        int newOffset = offset;
        newOffset += NurPacket.PacketByte(data, newOffset, this.mTargetValue);
        newOffset += NurPacket.PacketByte(data, newOffset, this.mCommandValue);
        if (this.mParameterBytes != null) {
            newOffset += NurPacket.PacketBytes(data, newOffset, this.mParameterBytes);
        }
        return newOffset - offset;
    }
    
    @Override
    public void deserializePayload(final byte[] data, final int offset, final int dataLen) throws Exception {
        this.processReply(this.getStatus(), data, offset, dataLen);
    }
}
