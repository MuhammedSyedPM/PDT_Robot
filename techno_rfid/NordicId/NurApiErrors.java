// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

import java.util.Hashtable;

public class NurApiErrors
{
    public static final int NUR_SUCCESS = 0;
    public static final int NUR_NO_ERROR = 0;
    public static final int INVALID_COMMAND = 1;
    public static final int INVALID_LENGTH = 2;
    public static final int PARAMETER_OUT_OF_RANGE = 3;
    public static final int RECEIVE_TIMEOUT = 4;
    public static final int INVALID_PARAMETER = 5;
    public static final int PROGRAM_FAILED = 6;
    public static final int PARAMETER_MISMATCH = 8;
    public static final int HW_MISMATCH = 8;
    public static final int RESERVED1 = 9;
    public static final int PAGE_PROGRAM = 10;
    public static final int CRC_CHECK = 11;
    public static final int CRC_MISMATCH = 12;
    public static final int NOT_READY = 13;
    public static final int APP_NOT_PRESENT = 14;
    public static final int GENERAL = 16;
    public static final int RESEND_PACKET = 17;
    public static final int NO_TAG = 32;
    public static final int RESP_AIR = 33;
    public static final int G2_SELECT = 34;
    public static final int MISSING_SELDATA = 35;
    public static final int G2_ACCESS = 36;
    public static final int G2_READ = 48;
    public static final int G2_RD_PART = 49;
    public static final int G2_WRITE = 64;
    public static final int G2_WR_PART = 65;
    public static final int G2_TAG_RESP = 66;
    public static final int G2_SPECIAL = 80;
    public static final int READER_HW = 96;
    public static final int BAD_ANTENNA = 97;
    public static final int LOW_VOLTAGE = 98;
    public static final int OVER_TEMP = 99;
    public static final int INVALID_HANDLE = 4096;
    public static final int TRANSPORT = 4097;
    public static final int TR_NOT_CONNECTED = 4098;
    public static final int TR_TIMEOUT = 4099;
    public static final int BUFFER_TOO_SMALL = 4100;
    public static final int NOT_SUPPORTED = 4101;
    public static final int NO_PAYLOAD = 4102;
    public static final int INVALID_PACKET = 4103;
    public static final int PACKET_TOO_LONG = 4104;
    public static final int PACKET_CS_ERROR = 4105;
    public static final int NOT_WORD_BOUNDARY = 4106;
    public static final int FILE_NOT_FOUND = 4107;
    public static final int FILE_INVALID = 4108;
    public static final int MCU_ARCH = 4109;
    public static final int G2_TAG_MEM_OVERRUN = 4110;
    public static final int G2_TAG_MEM_LOCKED = 4111;
    public static final int G2_TAG_INSUF_POWER = 4112;
    public static final int G2_TAG_NON_SPECIFIC = 4113;
    private static Hashtable<Integer, String> errors;
    
    public static String getErrorMessage(final int error) {
        if (NurApiErrors.errors.containsKey(error)) {
            return NurApiErrors.errors.get(error);
        }
        return "Unknown error.";
    }
    
    static {
        (NurApiErrors.errors = new Hashtable<Integer, String>()).put(0, "Call succeeded");
        NurApiErrors.errors.put(1, "Invalid command sent to module");
        NurApiErrors.errors.put(2, "Invalid packet length sent to module");
        NurApiErrors.errors.put(3, "Command parametr(s) out of range");
        NurApiErrors.errors.put(4, "Data receive timeout");
        NurApiErrors.errors.put(5, "Invalid command parameter(s); Invalid function parameter(s)");
        NurApiErrors.errors.put(6, "Programming failure");
        NurApiErrors.errors.put(8, "Parameter mismatch");
        NurApiErrors.errors.put(8, "HW mismatch");
        NurApiErrors.errors.put(9, "RESERVED1");
        NurApiErrors.errors.put(10, "Page programming failure");
        NurApiErrors.errors.put(11, "Memory check failed");
        NurApiErrors.errors.put(12, "CRC mismatch in parameter");
        NurApiErrors.errors.put(13, "Device not ready or region that is being programmed is not unlocked");
        NurApiErrors.errors.put(14, "Module application not present");
        NurApiErrors.errors.put(16, "Generic, non-interpreted / unexpected error");
        NurApiErrors.errors.put(17, "Device wants to have last packet again due to the transfer failure");
        NurApiErrors.errors.put(32, "No tag(s) found");
        NurApiErrors.errors.put(33, "Air error");
        NurApiErrors.errors.put(34, "G2 select error");
        NurApiErrors.errors.put(35, "G2 select data missing");
        NurApiErrors.errors.put(36, "G2 access error");
        NurApiErrors.errors.put(48, "G2 Read error, unspecified");
        NurApiErrors.errors.put(49, "G2 Partially successful read");
        NurApiErrors.errors.put(64, "G2 Write error, unspecified");
        NurApiErrors.errors.put(65, "G2 Partially successful write");
        NurApiErrors.errors.put(66, "G2 Tag read responded w/ error");
        NurApiErrors.errors.put(80, "Special error; Some additional debug data is returned with this error");
        NurApiErrors.errors.put(96, "HW error");
        NurApiErrors.errors.put(97, "Antenna too bad or disconnected");
        NurApiErrors.errors.put(98, "Reader low voltage");
        NurApiErrors.errors.put(99, "Reader temperature too high");
        NurApiErrors.errors.put(4096, "Invalid handle passed to function");
        NurApiErrors.errors.put(4097, "Transport error");
        NurApiErrors.errors.put(4098, "Transport not connected");
        NurApiErrors.errors.put(4099, "Transport timeout");
        NurApiErrors.errors.put(4100, "Buffer too small");
        NurApiErrors.errors.put(4101, "Functionality not supported");
        NurApiErrors.errors.put(4102, "Packet contains no payload");
        NurApiErrors.errors.put(4103, "Packet is invalid");
        NurApiErrors.errors.put(4104, "Packet too long");
        NurApiErrors.errors.put(4105, "Packet Checksum failure");
        NurApiErrors.errors.put(4106, "Data not in WORD boundary");
        NurApiErrors.errors.put(4107, "File not found");
        NurApiErrors.errors.put(4108, "File format is invalid");
        NurApiErrors.errors.put(4109, "MCU architecture mismatch");
        NurApiErrors.errors.put(4110, "The specified memory location does not exists or the EPC length field is not supported by the tag");
        NurApiErrors.errors.put(4111, "The specified memory location is locked and/or permalocked and is either not writeable or not readable");
        NurApiErrors.errors.put(4112, "The tag has insufficient power to perform the memory-write operation");
        NurApiErrors.errors.put(4113, "The tag does not support error-specific codes");
    }
}
