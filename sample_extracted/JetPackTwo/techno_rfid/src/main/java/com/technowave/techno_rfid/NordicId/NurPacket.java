// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurPacket
{
    public static final int FLAG_UNSOL = 1;
    public static final int FLAG_IRDATA = 2;
    public static final int FLAG_ACK = 4;
    public static final int FLAG_ECHO1 = 128;
    public static final int FLAG_SEQNUM = 61440;
    
    public static int PacketBytes(final byte[] buf, final int offset, final byte[] val) {
        for (int n = 0; n < val.length; ++n) {
            buf[offset + n] = val[n];
        }
        return val.length;
    }
    
    public static int PacketBytes(final byte[] buf, final int offset, final byte[] val, final int len) {
        for (int n = 0; n < len; ++n) {
            buf[offset + n] = val[n];
        }
        return len;
    }
    
    public static int PacketByte(final byte[] buf, final int offset, final int val) {
        buf[offset + 0] = (byte)(val & 0xFF);
        return 1;
    }
    
    public static int PacketWord(final byte[] buf, final int offset, final int val) {
        buf[offset + 1] = (byte)(val >> 8 & 0xFF);
        buf[offset + 0] = (byte)(val & 0xFF);
        return 2;
    }
    
    public static int PacketDword(final byte[] buf, final int offset, final int val) {
        buf[offset + 3] = (byte)(val >> 24 & 0xFF);
        buf[offset + 2] = (byte)(val >> 16 & 0xFF);
        buf[offset + 1] = (byte)(val >> 8 & 0xFF);
        buf[offset + 0] = (byte)(val & 0xFF);
        return 4;
    }
    
    public static int PacketDword(final byte[] buf, final int offset, final long val) {
        buf[offset + 3] = (byte)(val >> 24 & 0xFFL);
        buf[offset + 2] = (byte)(val >> 16 & 0xFFL);
        buf[offset + 1] = (byte)(val >> 8 & 0xFFL);
        buf[offset + 0] = (byte)(val & 0xFFL);
        return 4;
    }
    
    public static int PacketFill(final byte[] buf, final int offset, final byte byteVal, final int len) {
        int n = len;
        int ptr = offset;
        n = 0;
        while (n < len) {
            buf[ptr++] = byteVal;
        }
        return (len > 0) ? len : 0;
    }
    
    public static int BytesToByte(final byte[] buf, final int offset) {
        return buf[offset + 0] & 0xFF;
    }
    
    public static int BytesToWord(final byte[] buf, final int offset) {
        return ((buf[offset + 0] & 0xFF) | (buf[offset + 1] & 0xFF) << 8) & 0xFFFF;
    }
    
    public static int BytesToDword(final byte[] buf, final int offset) {
        final int rc = ((buf[offset + 0] & 0xFF) | (buf[offset + 1] & 0xFF) << 8 | (buf[offset + 2] & 0xFF) << 16 | (buf[offset + 3] & 0xFF) << 24) & -1;
        return rc;
    }
    
    public static String BytesToString(final byte[] buf, final int offset, final int len) {
        final StringBuilder b = new StringBuilder(len);
        for (int n = 0; n < len; ++n) {
            final byte ch = buf[offset + n];
            if (ch < 32) {
                break;
            }
            b.append((char)buf[offset + n]);
        }
        return b.toString();
    }
    
    public static int calculateHeaderCheckSum(final byte[] buf) {
        final int len = 5;
        int checksum = 255;
        for (int i = 0; i < len; ++i) {
            checksum ^= buf[i];
        }
        return checksum & 0xFF;
    }
    
    public static int getPacketSize(final NurCmd cmd) {
        int totalLen = 6;
        totalLen = ++totalLen + cmd.getPayloadLength();
        totalLen += 2;
        return totalLen;
    }
    
    public static int serialize(final NurCmd cmd, final byte[] data) throws Exception {
        int pos = 0;
        pos += PacketByte(data, pos, 165);
        pos += PacketWord(data, pos, 0);
        pos += PacketWord(data, pos, cmd.getFlags());
        final int payloadPos;
        pos = (payloadPos = pos + PacketByte(data, pos, 0));
        pos += PacketByte(data, pos, cmd.getCommand());
        final int serializedPayloadLen = cmd.serializePayload(data, pos);
        pos += serializedPayloadLen;
        final int crc16 = CRC16.calc(data, payloadPos, pos - payloadPos);
        pos += PacketWord(data, pos, crc16);
        PacketWord(data, 1, pos - payloadPos);
        final int hdrCS = calculateHeaderCheckSum(data);
        PacketByte(data, 5, hdrCS);
        return pos;
    }
    
    public static int write(final NurApiTransport tr, final NurCmd cmd) throws Exception {
        if (cmd.getCommand() == 0) {
            return 0;
        }
        final byte[] data = new byte[getPacketSize(cmd)];
        final int serializedLen = serialize(cmd, data);
        if (cmd.getOwner() != null) {
            cmd.getOwner().doDataLog(data, serializedLen, false);
        }
        return tr.writeData(data, serializedLen);
    }
}
