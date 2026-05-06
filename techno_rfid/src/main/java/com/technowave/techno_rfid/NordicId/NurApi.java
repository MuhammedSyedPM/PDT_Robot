// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

import java.nio.charset.StandardCharsets;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.util.Arrays;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.Iterator;
import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.Hashtable;
import java.util.LinkedList;

public class NurApi
{
    private static final String apiVersion = "1.9.1.3";
    private volatile boolean mXPCRemovalEnabled;
    private static final int DEFAULT_TIMEOUT = 8000;
    private static final int FLASH_PAGE_SIZE = 256;
    private static final int NUR_APP_FIRST_PAGE = 256;
    private static final int NUR_BL_FIRST_PAGE = 0;
    private static final int NUR_BL_LAST_PAGE = 255;
    private static final int SEC_CHIP_APP_FIRST_PAGE = 10064;
    private static final int SEC_CHIP_APP_LAST_PAGE = 10892;
    private static final int NUR_PROGRAM_RETRIES = 3;
    private static final int NUR_HEADER_OK = 0;
    private static final int NUR_HEADER_INVALID = 1;
    private static final int NUR_NO_HEADER = 2;
    private static final int NUR_FLASH_PAGE_SIZE = 256;
    private static final int N_RESERVED_DW = 16;
    private static final int NUR_FILE_CS_SIZE = 16;
    private static final int NUR_BINARY_HEADER_SIZE = 124;
    private static final int NUR_BINARY_PAGE_SIZE = 260;
    private static final int NUR_BINFILE_ID = 1112692046;
    private static final int NUR_FILE_LDR = -2114012070;
    private static final int NUR_FILE_APP = 886863401;
    public static final int NUR_MIN_APPSIZE = 32768;
    public static final int NUR_MIN_BLSIZE = 16384;
    public static final int NUR_MAX_BLSIZE = 65536;
    public static final int NUR_MAX_APPSIZE = 180224;
    private static final int NUR_MCUARCH_SAM7 = 794702455;
    private static final int NUR_MCUARCH_3S = -1996702370;
    public static final int BC_HOST_TO_DEV = 255;
    public static final int BC_DEV_TO_HOST = 254;
    public static final int BC_DEVTYPE = 2;
    public static final int BC_COMPANY = 161;
    public static final int LOG_VERBOSE = 1;
    public static final int LOG_ERROR = 2;
    public static final int LOG_USER = 4;
    public static final int LOG_DATA = 8;
    public static final int BAUDRATE_115200 = 0;
    public static final int BAUDRATE_230400 = 1;
    public static final int BAUDRATE_500000 = 2;
    public static final int BAUDRATE_1000000 = 3;
    public static final int BAUDRATE_1500000 = 4;
    public static final int BAUDRATE_38400 = 5;
    public static final int LOCK_ACTION_OPEN = 0;
    public static final int LOCK_ACTION_PERMAWRITE = 1;
    public static final int LOCK_ACTION_SECURED = 2;
    public static final int LOCK_ACTION_PERMALOCK = 3;
    public static final int LOCK_MEMORY_USERMEM = 1;
    public static final int LOCK_MEMORY_TIDMEM = 2;
    public static final int LOCK_MEMORY_EPCMEM = 4;
    public static final int LOCK_MEMORY_ACCESSPWD = 8;
    public static final int LOCK_MEMORY_KILLPWD = 16;
    public static final int BANK_PASSWD = 0;
    public static final int BANK_EPC = 1;
    public static final int BANK_TID = 2;
    public static final int BANK_USER = 3;
    public static final int TRACETAG_NO_EPC = 1;
    public static final int TRACETAG_START_CONTINUOUS = 2;
    public static final int TRACETAG_STOP_CONTINUOUS = 8;
    public static final int SETUP_LINKFREQ = 1;
    public static final int SETUP_RXDEC = 2;
    public static final int SETUP_TXLEVEL = 4;
    public static final int SETUP_TXMOD = 8;
    public static final int SETUP_REGION = 16;
    public static final int SETUP_INVQ = 32;
    public static final int SETUP_INVSESSION = 64;
    public static final int SETUP_INVROUNDS = 128;
    public static final int SETUP_ANTMASK = 256;
    public static final int SETUP_SCANSINGLETO = 512;
    public static final int SETUP_INVENTORYTO = 1024;
    public static final int SETUP_SELECTEDANT = 2048;
    public static final int SETUP_OPFLAGS = 4096;
    public static final int SETUP_INVTARGET = 8192;
    public static final int NUR_MINIMUM_SETUP_MASK = 16383;
    public static final int SETUP_INVEPCLEN = 16384;
    public static final int SETUP_READRSSIFILTER = 32768;
    public static final int SETUP_WRITERSSIFILTER = 65536;
    public static final int SETUP_INVRSSIFILTER = 131072;
    public static final int SETUP_READTIMEOUT = 262144;
    public static final int SETUP_WRITETIMEOUT = 524288;
    public static final int SETUP_LOCKTIMEOUT = 1048576;
    public static final int SETUP_KILLTIMEOUT = 2097152;
    public static final int SETUP_AUTOPERIOD = 4194304;
    public static final int SETUP_PERANTPOWER = 8388608;
    public static final int SETUP_PERANTOFFSET = 16777216;
    public static final int SETUP_ANTMASKEX = 33554432;
    public static final int SETUP_AUTOTUNE = 67108864;
    public static final int SETUP_PERANTPOWER_EX = 134217728;
    public static final int SETUP_RXSENS = 268435456;
    public static final int SETUP_RFPROFILE = 536870912;
    public static final int SETUP_ALL = 1073741823;
    public static final int SETUP_MASK_FOR_05W = 524287;
    public static final int SETUP_BASIC = 262143;
    public static final int OPFLAGS_EN_HOPEVENTS = 1;
    public static final int OPFLAGS_INVSTREAM_ZEROS = 2;
    public static final int OPFLAGS_TUNE_EVENT = 4096;
    public static final int OPFLAGS_EN_EXACT_BLF = 8192;
    public static final int OPFLAGS_EN_TAG_PHASE = 16384;
    public static final int MIN_CUSTFREQ = 840000;
    public static final int MAX_PATH = 1024;
    public static final int MAX_EPC_LENGTH = 62;
    public static final int MAX_IR_WORDS = 32;
    public static final int MAX_SELMASK = 62;
    public static final int MAX_EE_EPCLEN = 16;
    public static final int MAX_BITSTR_BITS = 1028;
    public static final int MAX_GPIO = 8;
    public static final int MAXN_ANTMAPPINGS = 32;
    public static final int MAX_FILTERS = 8;
    public static final int MAX_CUSTOM_FREQS = 100;
    public static final int MAX_FREQUENCY = 960000;
    public static final int MIN_FREQUENCY = 840000;
    public static final int MAX_PAUSETIME = 1000;
    public static final int MIN_LBTTHRESH = -90;
    public static final int CUSTHT_TARI_125 = 1;
    public static final int CUSTHT_TARI_25 = 2;
    public static final int MAX_SERIAL_LENGTH = 16;
    public static final int MAX_NAME_LENGTH = 16;
    public static final int MAX_FCCID_LENGTH = 48;
    public static final int MAX_HWVER_LENGTH = 8;
    public static final int DEFAULT_BAUDRATE = 115200;
    public static final int GPIO1 = 0;
    public static final int GPIO2 = 1;
    public static final int GPIO3 = 2;
    public static final int GPIO4 = 3;
    public static final int GPIO5 = 4;
    public static final int GPIO6 = 5;
    public static final int GPIO7 = 6;
    public static final int GPIO8 = 7;
    public static final int GPIO_TYPE_OUTPUT = 0;
    public static final int GPIO_TYPE_INPUT = 1;
    public static final int GPIO_TYPE_RFIDON = 2;
    public static final int GPIO_TYPE_RFIDREAD = 3;
    public static final int GPIO_TYPE_BEEPER = 4;
    public static final int GPIO_TYPE_ANTCTL1 = 5;
    public static final int GPIO_TYPE_ANTCTL2 = 6;
    public static final int GPIO_EDGE_FALLING = 0;
    public static final int GPIO_EDGE_RISING = 1;
    public static final int GPIO_EDGE_BOTH = 2;
    public static final int TID_HIDE_NONE = 0;
    public static final int TID_HIDE_SOME = 1;
    public static final int TID_HIDE_ALL = 2;
    public static final int UTRACE_RANGE_NORMAL = 0;
    public static final int UTRACE_RANGE_TOGGLE = 1;
    public static final int UTRACE_RANGE_REDUCE = 2;
    public static final int GEN2V2_MAX_AUTHBYTES = 128;
    public static final int GEN2V2_MAX_AUTHBITS = 1024;
    public static final int GEN2V2_MIN_AUTHBITS = 1;
    public static final int MAX_AUTHRESPBITS = 1008;
    public static final int TAM_KEYLEN = 16;
    public static final int TAM_MAXBLOCKS = 4;
    public static final int SZ_TAM2_BLOCK = 8;
    public static final int MAX_TAM_OFFSET = 4095;
    public static final int GPIO_ACT_NONE = 0;
    public static final int GPIO_ACT_NOTIFY = 1;
    public static final int GPIO_ACT_SCANTAG = 2;
    public static final int GPIO_ACT_INVENTORY = 3;
    public static final int REGIONID_EU = 0;
    public static final int REGIONID_FCC = 1;
    public static final int REGIONID_PRC = 2;
    public static final int REGIONID_MALAYSIA = 3;
    public static final int REGIONID_BRAZIL = 4;
    public static final int REGIONID_AUSTRALIA = 5;
    public static final int REGIONID_NEWZEALAND = 6;
    public static final int REGIONID_JA250MW = 7;
    public static final int REGIONID_JA500MW = 8;
    public static final int REGIONID_KOREA_LBT = 9;
    public static final int REGIONID_INDIA = 10;
    public static final int REGIONID_RUSSIA = 11;
    public static final int REGIONID_VIETNAM = 12;
    public static final int REGIONID_SINGAPORE = 13;
    public static final int REGIONID_THAILAND = 14;
    public static final int REGIONID_PHILIPPINES = 15;
    public static final int REGIONID_MOROCCO = 16;
    public static final int REGIONID_PERU = 17;
    public static final int REGIONID_ISRAEL = 18;
    public static final int REGIONID_HONGKONG = 19;
    public static final int REGIONID_LAST = 19;
    public static final int REGIONID_CUSTOM = 254;
    public static final int RXDECODING_FM0 = 0;
    public static final int RXDECODING_M2 = 1;
    public static final int RXDECODING_M4 = 2;
    public static final int RXDECODING_M8 = 3;
    public static final int TXMODULATION_ASK = 0;
    public static final int TXMODULATION_PRASK = 1;
    public static final int MAX_TXLEVEL = 0;
    public static final int MIN_TXLEVEL = 19;
    public static final int TXLEVEL_27 = 0;
    public static final int TXLEVEL_26 = 1;
    public static final int TXLEVEL_25 = 2;
    public static final int TXLEVEL_24 = 3;
    public static final int TXLEVEL_23 = 4;
    public static final int TXLEVEL_22 = 5;
    public static final int TXLEVEL_21 = 6;
    public static final int TXLEVEL_20 = 7;
    public static final int TXLEVEL_19 = 8;
    public static final int TXLEVEL_18 = 9;
    public static final int TXLEVEL_17 = 10;
    public static final int TXLEVEL_16 = 11;
    public static final int TXLEVEL_15 = 12;
    public static final int TXLEVEL_14 = 13;
    public static final int TXLEVEL_13 = 14;
    public static final int TXLEVEL_12 = 15;
    public static final int TXLEVEL_11 = 16;
    public static final int TXLEVEL_10 = 17;
    public static final int TXLEVEL_9 = 18;
    public static final int TXLEVEL_8 = 19;
    public static final int ANTENNAID_AUTOSELECT = -1;
    public static final int ANTENNAID_1 = 0;
    public static final int ANTENNAID_2 = 1;
    public static final int ANTENNAID_3 = 2;
    public static final int ANTENNAID_4 = 3;
    public static final int ANTENNAID_5 = 4;
    public static final int ANTENNAID_6 = 5;
    public static final int ANTENNAID_7 = 6;
    public static final int ANTENNAID_8 = 7;
    public static final int ANTENNAID_9 = 8;
    public static final int ANTENNAID_10 = 9;
    public static final int ANTENNAID_11 = 10;
    public static final int ANTENNAID_12 = 11;
    public static final int ANTENNAID_13 = 12;
    public static final int ANTENNAID_14 = 13;
    public static final int ANTENNAID_15 = 14;
    public static final int ANTENNAID_16 = 15;
    public static final int ANTENNAID_17 = 16;
    public static final int ANTENNAID_18 = 17;
    public static final int ANTENNAID_19 = 18;
    public static final int ANTENNAID_20 = 19;
    public static final int ANTENNAID_21 = 20;
    public static final int ANTENNAID_22 = 21;
    public static final int ANTENNAID_23 = 22;
    public static final int ANTENNAID_24 = 23;
    public static final int ANTENNAID_25 = 24;
    public static final int ANTENNAID_26 = 25;
    public static final int ANTENNAID_27 = 26;
    public static final int ANTENNAID_28 = 27;
    public static final int ANTENNAID_29 = 28;
    public static final int ANTENNAID_30 = 29;
    public static final int ANTENNAID_31 = 30;
    public static final int ANTENNAID_32 = 31;
    public static final int LAST_ANTENNAID = 31;
    public static final int MAX_ANTENNAS = 4;
    public static final int MAX_ANTENNAS_EX = 32;
    public static final int ANTENNAMASK_1 = 1;
    public static final int ANTENNAMASK_2 = 2;
    public static final int ANTENNAMASK_3 = 4;
    public static final int ANTENNAMASK_4 = 8;
    public static final int ANTENNAMASK_5 = 16;
    public static final int ANTENNAMASK_6 = 32;
    public static final int ANTENNAMASK_7 = 64;
    public static final int ANTENNAMASK_8 = 128;
    public static final int ANTENNAMASK_9 = 256;
    public static final int ANTENNAMASK_10 = 512;
    public static final int ANTENNAMASK_11 = 1024;
    public static final int ANTENNAMASK_12 = 2048;
    public static final int ANTENNAMASK_13 = 4096;
    public static final int ANTENNAMASK_14 = 8192;
    public static final int ANTENNAMASK_15 = 16384;
    public static final int ANTENNAMASK_16 = 32768;
    public static final int ANTENNAMASK_17 = 65536;
    public static final int ANTENNAMASK_18 = 131072;
    public static final int ANTENNAMASK_19 = 262144;
    public static final int ANTENNAMASK_20 = 524288;
    public static final int ANTENNAMASK_21 = 1048576;
    public static final int ANTENNAMASK_22 = 2097152;
    public static final int ANTENNAMASK_23 = 4194304;
    public static final int ANTENNAMASK_24 = 8388608;
    public static final int ANTENNAMASK_25 = 16777216;
    public static final int ANTENNAMASK_26 = 33554432;
    public static final int ANTENNAMASK_27 = 67108864;
    public static final int ANTENNAMASK_28 = 134217728;
    public static final int ANTENNAMASK_29 = 268435456;
    public static final int ANTENNAMASK_30 = 536870912;
    public static final int ANTENNAMASK_31 = 1073741824;
    public static final int ANTENNAMASK_32 = Integer.MIN_VALUE;
    public static final int ANTENNAMASK_ALL = -1;
    public static final int AT_NR_OF_BANDS = 6;
    public static final int AT_LOWEST_BANDNUM = 0;
    public static final int AT_LAST_BAND = 5;
    public static final int AT_BAND0 = 850000;
    public static final int AT_BAND1 = 870000;
    public static final int AT_BAND2 = 890000;
    public static final int AT_BAND3 = 910000;
    public static final int AT_BAND4 = 930000;
    public static final int AT_BAND5 = 950000;
    public static final int AT_BANDWIDTH = 20000;
    public static final int TUNEBAND_EU = 1;
    public static final int TUNEBAND_FCC1 = 3;
    public static final int TUNEBAND_FCC2 = 4;
    public static final int AT_FAST = 0;
    public static final int AT_MEDIUM = 1;
    public static final int AT_WIDE = 2;
    public static final int AT_FULL = 3;
    public static final int TUNE_REV_USER = 0;
    public static final int TUNE_REV_FACTORY = 1;
    public static final int TUNE_REV_OVERRIDE = 2;
    public static final int STORE_RF = 1;
    public static final int STORE_GPIO = 2;
    public static final int STORE_BAUDRATE = 4;
    public static final int STORE_OPFLAGS = 8;
    public static final int STORE_ALL = 15;
    public static final int SENSOR_TAP = 0;
    public static final int SENSOR_LIGHT = 1;
    public static final int RW_SEC = 1;
    public static final int RW_SBP = 2;
    public static final int RW_EA1 = 4;
    public static final int RW_EA2 = 8;
    public static final int SESSION_S0 = 0;
    public static final int SESSION_S1 = 1;
    public static final int SESSION_S2 = 2;
    public static final int SESSION_S3 = 3;
    public static final int SESSION_SL = 4;
    public static final int FILTER_ACTION_0 = 0;
    public static final int FILTER_ACTION_1 = 1;
    public static final int FILTER_ACTION_2 = 2;
    public static final int FILTER_ACTION_3 = 3;
    public static final int FILTER_ACTION_4 = 4;
    public static final int FILTER_ACTION_5 = 5;
    public static final int FILTER_ACTION_6 = 6;
    public static final int FILTER_ACTION_7 = 7;
    public static final int LINK_FREQUENCY_160000 = 160000;
    public static final int LINK_FREQUENCY_256000 = 256000;
    public static final int LINK_FREQUENCY_320000 = 320000;
    public static final int INVSELSTATE_ALL = 0;
    public static final int INVSELSTATE_NOTSL = 2;
    public static final int INVSELSTATE_SL = 3;
    public static final int INVTARGET_A = 0;
    public static final int INVTARGET_B = 1;
    public static final int INVTARGET_AB = 2;
    public static final int IRTYPE_EPCDATA = 0;
    public static final int IRTYPE_DATAONLY = 1;
    public static final String DEFAULT_BC_ADDRESS = "255.255.255.255";
    String mBroadcastAddress;
    public static final int BC_FILTER_TYPE_NONE = 0;
    public static final int BC_FILTER_TYPE_MAC = 1;
    public static final int BC_FILTER_TYPE_IP = 2;
    public static final int BC_FILTER_TYPE_VERSION = 3;
    public static final int BC_FILTER_TYPE_SERVERPORT = 4;
    public static final int BC_FILTER_TYPE_ADDRTYPE = 5;
    public static final int BC_FILTER_TYPE_MODE = 6;
    public static final int BC_FILTER_TYPE_NURVER = 7;
    public static final int BC_FILTER_TYPE_STATUS = 8;
    public static final int BC_FILTER_TYPE_NAME = 9;
    public static final int BC_GET_DEV_INFO = 1;
    public static final int BC_GET_ETHCONFIG = 2;
    public static final int BC_GET_GPIO = 3;
    public static final int BC_SET_ETHCONFIG = 4;
    public static final int BC_BEEP = 5;
    public static final int BC_FILTER_OP_EQUAL = 0;
    public static final int BC_FILTER_OP_HIGHER = 1;
    public static final int BC_FILTER_OP_LOWER = 2;
    public static final int BC_FILTER_OP_LIKE = 3;
    public static final int TRANSPORT_NUR = 0;
    public static final int TRANSPORT_BROADCAST = 1;
    public static final int ADDRESS_TYPE_DHCP = 0;
    public static final int ADDRESS_TYPE_STATIC = 1;
    public static final int HOST_MODE_SERVER = 0;
    public static final int HOST_MODE_CLIENT = 1;
    public static final int DEFAULT_MAX_CLIENTS = 10;
    public static final int XPC_W1_MASK = 512;
    public static final int XPC_XEB_MASK = 32768;
    public static final int RXSENS_LOW = 0;
    public static final int RXSENS_NOMINAL = 1;
    public static final int RXSENS_HIGH = 2;
    public static final int RFPROFILE_ROBUST = 0;
    public static final int RFPROFILE_NOMINAL = 1;
    public static final int RFPROFILE_HIGHSPEED = 2;
    public static final int CXF_ASWRITE = 1;
    public static final int CXF_USEHANDLE = 2;
    public static final int CXF_XORRN16 = 4;
    public static final int CXF_TXONLY = 8;
    public static final int CXF_NOTXCRC = 16;
    public static final int CXF_NORXCRC = 32;
    public static final int CXF_CRC5 = 64;
    public static final int CXF_NORXLEN = 128;
    public static final int CXF_STRIPHND = 256;
    public static final int CXF_SKIPRESEL = 512;
    public static final int NUR_TTEV_NONE = 0;
    public static final int NUR_TTEV_VISIBILITY = 1;
    public static final int NUR_TTEV_ANTENNA = 2;
    public static final int NUR_TTEV_RSSI = 4;
    public static final int NUR_TTEV_POSITION = 8;
    public static final int NUR_TTEV_SECTOR = 16;
    public static final int NUR_TTEV_SCAN = 32;
    public static final int NUR_TTEV_INOUT = 64;
    public static final int NUR_TTFL_NONE = 0;
    public static final int NUR_TTFL_FULLROUNDREPORT = 1;
    public static final int NUR_TTFL_USESIMPLEFILTER = 2;
    public static final int NUR_TTFL_USECOMPLEXFILTER = 4;
    public static final int NUR_TTIO_DIRECTION_NONE = 0;
    public static final int NUR_TTIO_DIRECTION_INTOOUT = 1;
    public static final int NUR_TTIO_DIRECTION_OUTTOIN = 2;
    public static final int MAX_WLAN_PROFILE_INDEX = 7;
    public static final int NUR_BINTYPE_L2LOADER = 1;
    public static final int NUR_BINTYPE_L2APP = 2;
    public static final int DIAG_REPORT_PERIODIC = 1;
    public static final int DIAG_REPORT_TEMP_HIGH = 2;
    public static final int DIAG_REPORT_TEMP_OVER = 4;
    public static final int DIAG_REPORT_LOWVOLT = 8;
    public static final int DIAG_GETREPORT_NONE = 0;
    public static final int DIAG_GETREPORT_RESET_STATS = 1;
    public static final int DIAG_CFG_NOTIFY_NONE = 0;
    public static final int DIAG_CFG_NOTIFY_PERIODIC = 1;
    public static final int DIAG_CFG_NOTIFY_WARN = 2;
    public static final int DIAG_CFG_FW_ERROR_LOG = 4;
    public static final int DIAG_CFG_FW_DEBUG_LOG = 8;
    public static final int NUR_HOSTFLAGS_EN_UNSOL_ACK = 1;
    public static final int AUTOPER_OFF = 0;
    public static final int AUTOPER_25 = 1;
    public static final int AUTOPER_33 = 2;
    public static final int AUTOPER_50 = 3;
    private volatile boolean mXchSkipReselect;
    private final boolean INTERNALS_OK = true;
    private final boolean INTERNALS_FAILED = false;
    private volatile int mChangedSetupFlags;
    private volatile int mHostFlags;
    private volatile boolean mLogToStdout;
    private volatile NurApiTransport mTransport;
    private Thread mReadThread;
    private Thread mWriteThread;
    private volatile boolean mConnEventSent;
    private volatile boolean mConnected;
    private Thread mPacketHandlerThread;
    Object mPacketHandlerLock;
    private Thread mNotifyThread;
    private volatile LinkedList<AsyncNotification> mNotifyQueue;
    public volatile NurApiListener mListener;
    public volatile NurDiagReportListener mDiagReportListener;
    public volatile AccEventListener mAccessoryEventListener;
    private volatile LinkedList<NurApiUnknownEventListener> mUnknownEventListeners;
    private volatile NurPacketHandler mPacketHandler;
    private volatile NurCmd expectedCmd;
    private volatile NurCmd retCmd;
    private volatile boolean retInvalid;
    private volatile int mSentSetupFlags;
    private Object readLock;
    private volatile boolean readLockCond;
    private volatile NurCmdNotifyBoot receivedBootEvent;
    private Object writeLock;
    private volatile boolean writeLockCond;
    private volatile boolean mDisposed;
    private volatile NurTagStorage mStorage;
    private volatile NurApiUiThreadRunner mUiThreadRunner;
    private volatile int mSetupRestriction;
    private volatile boolean mIsL2Module;
    private volatile NurRespReaderInfo mReaderInfo;
    private volatile boolean mAntennaMapPresent;
    private volatile AntennaMapping[] mAntennaMap;
    private final int NUR_FW_VER1_9_A;
    private final int NUR_FW_VER2_1_A;
    private final int NUR_FW_VER2_4_B;
    private final int NUR_FW_VER3_3_C;
    private final int NUR_FW_VER4_0_A;
    private final int NUR_FW_VER4_9_H;
    private volatile NurSetup mCurrentSetup;
    private volatile NurRespDevCaps mDeviceCaps;
    private volatile boolean mValidDevCaps;
    private volatile NurTagStorage mTagTrackingStorage;
    private volatile Hashtable<byte[], NurTTHashObject> mTagTrackingHash;
    private volatile NurTagTrackingConfig mTTConfig;
    private volatile boolean mValidTTConfig;
    private volatile boolean mTagTrackingActive;
    private Object mTagTrackingSync;
    private volatile int mTTHashChangedCount;
    private volatile int mScanUntilNewTagsCount;
    private static final int SZ_TTSCAN_DATA = 5;
    private volatile NurInventoryExtended mInventoryExtended;
    private volatile NurInventoryExtendedFilter[] mInventoryExtendedFilters;
    private volatile boolean mInventoryExtendedStreamRunning;
    private volatile boolean mInventoryStreamRunning;
    private volatile NurRWSingulationBlock mTraceTagSb;
    private volatile int mTraceTagFlags;
    private volatile boolean mTraceRunning;
    private volatile boolean mNxpAlarmStreamRunning;
    private volatile boolean mEpcEnumStreamRunning;
    private volatile boolean syncReadThreadStarted;
    Object syncReadThreadObj;
    private volatile boolean syncWriteThreadStarted;
    Object syncWriteThreadObj;
    private volatile BlockingQueue<NurCmd> mWriteThreadQueue;
    protected int mLogLevel;
    Runnable mNotifyRunnable;
    private boolean mConnectCalled;
    Runnable mPacketHandlerRunnable;
    private static final int BEAM_COUNT = 10;
    private static final int INTERNAL_BEAM_COUNT = 13;
    private static final float CA = 0.05f;
    private static final float[][] BeamCoords;
    private static final int[] BeamNumbers;
    private static final float[][] SectorPos;
    static final byte[] ackBuf;
    NurPacketHeader mLastHdr;
    long mLastHdrTime;
    Runnable mReadRunnable;
    Runnable mWriteRunnable;
    private volatile boolean writeFail;
    private Thread mEthQueryThread;
    private SendBroadcastMessage mSyncEthQueryBroadcast;
    private Object mSyncEthQueryMutex;
    private Thread mGenericBCastThread;
    
    public void setHostFlags(final int flags) throws Exception {
        this.mHostFlags = flags;
        if (this.isConnected()) {
            this.ping();
        }
    }
    
    public int getHostFlags() {
        return this.mHostFlags;
    }
    
    public void disableCustomReselect() {
        this.mXchSkipReselect = true;
    }
    
    public boolean getCustomReselect() {
        final boolean rc = this.mXchSkipReselect;
        this.mXchSkipReselect = false;
        return rc;
    }
    
    protected void sendDeviceSearchEvent(final NurEventDeviceInfo deviceInfo) {
        if (this.mListener != null) {
            this.asyncNotification(new Runnable() {
                @Override
                public void run() {
                    NurApi.this.mListener.deviceSearchEvent(deviceInfo);
                }
            });
        }
    }
    
    private void sendProgrammingEvent(final NurEventProgrammingProgress pp) {
        if (this.mListener != null) {
            this.asyncNotification(new Runnable() {
                @Override
                public void run() {
                    NurApi.this.mListener.programmingProgressEvent(pp);
                }
            });
        }
    }
    
    public static final int makeVersion(final int maj, final int min, final int bld) {
        int ret = maj & 0xFF;
        ret <<= 8;
        ret |= (min & 0xFF);
        ret <<= 8;
        ret |= (bld & 0xFF);
        return ret;
    }
    
    public NurApi(final NurApiTransport tr) {
        this.mXPCRemovalEnabled = true;
        this.mBroadcastAddress = "255.255.255.255";
        this.mXchSkipReselect = false;
        this.mChangedSetupFlags = 0;
        this.mHostFlags = 1;
        this.mLogToStdout = false;
        this.mTransport = null;
        this.mReadThread = null;
        this.mWriteThread = null;
        this.mConnEventSent = false;
        this.mConnected = false;
        this.mPacketHandlerThread = null;
        this.mPacketHandlerLock = new Object();
        this.mNotifyThread = null;
        this.mNotifyQueue = new LinkedList<AsyncNotification>();
        this.mListener = null;
        this.mDiagReportListener = null;
        this.mAccessoryEventListener = null;
        this.mUnknownEventListeners = new LinkedList<NurApiUnknownEventListener>();
        this.mPacketHandler = new NurPacketHandler(this);
        this.expectedCmd = null;
        this.retCmd = null;
        this.retInvalid = false;
        this.readLock = new Object();
        this.readLockCond = false;
        this.receivedBootEvent = null;
        this.writeLock = new Object();
        this.writeLockCond = false;
        this.mDisposed = false;
        this.mStorage = new NurTagStorage();
        this.mUiThreadRunner = null;
        this.mSetupRestriction = 1073741823;
        this.mIsL2Module = false;
        this.mReaderInfo = null;
        this.mAntennaMapPresent = false;
        this.mAntennaMap = null;
        this.NUR_FW_VER1_9_A = makeVersion(1, 9, 65);
        this.NUR_FW_VER2_1_A = makeVersion(2, 1, 65);
        this.NUR_FW_VER2_4_B = makeVersion(2, 4, 66);
        this.NUR_FW_VER3_3_C = makeVersion(3, 3, 67);
        this.NUR_FW_VER4_0_A = makeVersion(4, 0, 65);
        this.NUR_FW_VER4_9_H = makeVersion(4, 9, 72);
        this.mCurrentSetup = new NurSetup();
        this.mDeviceCaps = null;
        this.mValidDevCaps = false;
        this.mTagTrackingStorage = new NurTagStorage();
        this.mTagTrackingHash = new Hashtable<byte[], NurTTHashObject>();
        this.mTTConfig = null;
        this.mValidTTConfig = false;
        this.mTagTrackingActive = false;
        this.mTagTrackingSync = new Object();
        this.mTTHashChangedCount = 0;
        this.mScanUntilNewTagsCount = 0;
        this.mInventoryExtended = null;
        this.mInventoryExtendedFilters = null;
        this.mInventoryExtendedStreamRunning = false;
        this.mInventoryStreamRunning = false;
        this.mTraceTagSb = null;
        this.mTraceTagFlags = 0;
        this.mTraceRunning = false;
        this.mNxpAlarmStreamRunning = false;
        this.mEpcEnumStreamRunning = false;
        this.syncReadThreadStarted = false;
        this.syncReadThreadObj = null;
        this.syncWriteThreadStarted = false;
        this.syncWriteThreadObj = null;
        this.mWriteThreadQueue = new LinkedBlockingQueue<NurCmd>();
        this.mLogLevel = 2;
        this.mNotifyRunnable = new Runnable() {
            @Override
            public void run() {
                NurApi.this.VLog("NotifyThread ENTER");
                while (!NurApi.this.mDisposed) {
                    try {
                        synchronized (NurApi.this.mNotifyQueue) {
                            if (NurApi.this.mNotifyQueue.isEmpty()) {
                                NurApi.this.mNotifyQueue.wait();
                            }
                        }
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    while (!NurApi.this.mDisposed && !NurApi.this.mNotifyQueue.isEmpty()) {
                        final AsyncNotification notification;
                        synchronized (NurApi.this.mNotifyQueue) {
                            notification = NurApi.this.mNotifyQueue.remove();
                        }
                        if (NurApi.this.mListener != null) {
                            if (NurApi.this.mUiThreadRunner == null) {
                                notification.runFunc.run();
                            }
                            else {
                                NurApi.this.mUiThreadRunner.runOnUiThread(notification.runFunc);
                            }
                        }
                    }
                }
                NurApi.this.VLog("NotifyThread EXIT");
            }
        };
        this.mConnectCalled = false;
        this.mPacketHandlerRunnable = new Runnable() {
            @Override
            public void run() {
                NurApi.this.VLog("PacketHandlerThread ENTER");
                while (!NurApi.this.mDisposed) {
                    if (NurApi.this.mPacketHandler.isEmpty()) {
                        synchronized (NurApi.this.mPacketHandlerLock) {
                            try {
                                NurApi.this.mPacketHandlerLock.wait();
                            }
                            catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    if (!NurApi.this.mDisposed) {
                        NurApi.this.runPacketHandler();
                    }
                }
                NurApi.this.VLog("PacketHandlerThread EXIT");
            }
        };
        this.mLastHdr = new NurPacketHeader();
        this.mLastHdrTime = 0L;
        this.mReadRunnable = new Runnable() {
            @Override
            public void run() {
                NurApi.this.readThread();
            }
        };
        this.mWriteRunnable = new Runnable() {
            @Override
            public void run() {
                NurApi.this.writeThread();
            }
        };
        this.writeFail = false;
        this.mEthQueryThread = null;
        this.mSyncEthQueryBroadcast = null;
        this.mSyncEthQueryMutex = new Object();
        this.mGenericBCastThread = null;
        this.mTransport = tr;
        (this.mNotifyThread = new Thread(this.mNotifyRunnable)).start();
        (this.mPacketHandlerThread = new Thread(this.mPacketHandlerRunnable)).start();
        this.syncReadThreadObj = new Object();
        this.syncWriteThreadObj = new Object();
    }
    
    public NurApi() {
        this(null);
    }
    
    public void dispose() {
        this.mDisposed = true;
        try {
            this.mTransport.disconnect();
        }
        catch (Exception ex) {}
        try {
            if (this.mReadThread != null) {
                this.mReadThread.join(1000L);
                this.mReadThread = null;
            }
            synchronized (this.writeLock) {
                this.mWriteThreadQueue.put(new NurCmd(0));
            }
            if (this.mWriteThread != null) {
                this.mWriteThread.join(1000L);
                this.mWriteThread = null;
            }
            synchronized (this.mNotifyQueue) {
                this.mNotifyQueue.notifyAll();
            }
            if (this.mNotifyThread != null) {
                this.mNotifyThread.join(1000L);
                this.mNotifyThread = null;
            }
            synchronized (this.mPacketHandlerLock) {
                this.mPacketHandlerLock.notifyAll();
            }
            synchronized (this.mUnknownEventListeners) {
                try {
                    this.mUnknownEventListeners.removeAll(null);
                }
                catch (Exception ex2) {}
            }
            if (this.mPacketHandlerThread != null) {
                this.mPacketHandlerThread.join(1000L);
                this.mPacketHandlerThread = null;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public int getLogLevel() {
        return this.mLogLevel;
    }
    
    public void setLogLevel(final int newLevel) {
        this.mLogLevel = newLevel;
    }
    
    public void setLogToStdout(final boolean enable) {
        this.mLogToStdout = enable;
    }
    
    public boolean isDisposed() {
        return this.mDisposed;
    }
    
    protected void asyncNotification(final Runnable runFunc, final NurCmd cmd, final boolean replace) {
        if (this.mDisposed || this.mListener == null) {
            return;
        }
        synchronized (this.mNotifyQueue) {
            if (cmd != null && replace) {
                final Iterator<AsyncNotification> iter = this.mNotifyQueue.iterator();
                while (iter.hasNext()) {
                    final AsyncNotification ob = iter.next();
                    if (ob.cmd != null && ob.cmd.getCommand() == cmd.getCommand()) {
                        if (cmd.getCommand() == 130) {
                            final NurCmdNotifyInventory tmp = (NurCmdNotifyInventory)cmd;
                            final NurCmdNotifyInventory tmp2 = (NurCmdNotifyInventory)ob.cmd;
                            final NurEventInventory response = tmp.getResponse();
                            response.tagsAdded += tmp2.getResponse().tagsAdded;
                        }
                        iter.remove();
                        break;
                    }
                }
            }
            this.mNotifyQueue.add(new AsyncNotification(cmd, runFunc));
            this.mNotifyQueue.notifyAll();
        }
    }
    
    public void asyncNotification(final Runnable runFunc) {
        this.asyncNotification(runFunc, null, false);
    }
    
    void syncNotification(final Runnable runFunc) {
        if (this.mListener != null) {
            if (this.mUiThreadRunner == null) {
                runFunc.run();
            }
            else {
                this.mUiThreadRunner.runOnUiThread(runFunc);
            }
        }
    }
    
    private int SwapEndian(final int value) {
        int ret = value << 24;
        ret |= (value & 0xFF00) << 8;
        ret |= (value & 0xFF0000) >> 8;
        ret |= (value >> 24 & 0xFF);
        return ret;
    }
    
    public static String byteArrayToHexString(final byte[] b, final int offset, final int len) {
        final StringBuffer sb = new StringBuffer(len * 2);
        for (int i = 0; i < len; ++i) {
            final int v = b[offset + i] & 0xFF;
            if (v < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(v));
        }
        return sb.toString().toUpperCase();
    }
    
    public static String byteArrayToHexString(final byte[] b) {
        return byteArrayToHexString(b, 0, b.length);
    }
    
    public static String byteArrayToHexString(final byte[] b, final char delim) {
        final int len = b.length;
        final StringBuffer sb = new StringBuffer(len * 2);
        for (int i = 0; i < len; ++i) {
            final int v = b[i] & 0xFF;
            if (v < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(v));
            sb.append(delim);
        }
        return sb.toString().toUpperCase();
    }
    
    public static byte[] hexStringToByteArray(final String s) throws Exception {
        final int len = s.length();
        if (len % 2 != 0) {
            throw new NurApiException(4106);
        }
        final byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte)((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
    
    public void addUnknownEventListener(final NurApiUnknownEventListener l) {
        this.mUnknownEventListeners.add(l);
    }
    
    public void removeUnknownEventListener(final NurApiUnknownEventListener l) {
        if (this.mUnknownEventListeners.size() > 0) {
            final Iterator<NurApiUnknownEventListener> iter = this.mUnknownEventListeners.iterator();
            while (iter.hasNext()) {
                if (iter.next() == l) {
                    this.mUnknownEventListeners.remove(l);
                }
            }
        }
    }
    
    public void setListener(final NurApiListener l) {
        this.mListener = l;
    }
    
    public NurApiListener getListener() {
        return this.mListener;
    }
    
    public void setDiagnosticsListener(final NurDiagReportListener l) {
        this.mDiagReportListener = l;
    }
    
    public NurDiagReportListener getDiagnosticsListener() {
        return this.mDiagReportListener;
    }
    
    public void setAccessoryEventListener(final AccEventListener l) {
        this.mAccessoryEventListener = l;
    }
    
    public AccEventListener getAccessoryEventListener() {
        return this.mAccessoryEventListener;
    }
    
    public NurApiTransport getTransport() {
        return this.mTransport;
    }
    
    public void setTransport(final NurApiTransport tr) throws Exception {
        if (this.mTransport != null) {
            this.disconnect();
        }
        this.mTransport = tr;
    }
    
    public void setUiThreadRunner(final NurApiUiThreadRunner runner) {
        this.mUiThreadRunner = runner;
    }
    
    public NurApiUiThreadRunner getUiThreadRunner() {
        return this.mUiThreadRunner;
    }
    
    public void Log(final int level, final String txt) {
        if ((level & this.mLogLevel) != 0x0 && !this.mDisposed && (this.mListener != null || this.mLogToStdout)) {
            final int lvl = level;
            final String str = System.currentTimeMillis() + " " + txt;
            if (this.mLogToStdout) {
                System.out.println("Log[" + level + "]: " + str);
            }
            if (this.mListener != null) {
                this.asyncNotification(new Runnable() {
                    @Override
                    public void run() {
                        NurApi.this.mListener.logEvent(lvl, "(" + lvl + "/" + NurApi.this.mLogLevel + ") " + str);
                    }
                });
            }
        }
    }
    
    public void ULog(final String txt) {
        this.Log(4, txt);
    }
    
    public void VLog(final String txt) {
        this.Log(1, txt);
    }
    
    public void ELog(final String txt) {
        this.Log(2, txt);
    }
    
    public void DLog(final String txt) {
        this.Log(8, txt);
    }
    
    private boolean restoreInternals() {
        boolean rc = true;
        if (this.mChangedSetupFlags != 0) {
            try {
                this.setModuleSetup(this.mCurrentSetup, this.mChangedSetupFlags);
            }
            catch (Exception e) {
                rc = false;
            }
        }
        return rc;
    }
    
    private boolean updateInternals(String mode) {
        this.VLog("updateInternals() mode: " + mode);
        this.mCurrentSetup = new NurSetup();
        this.mReaderInfo = null;
        this.mAntennaMapPresent = false;
        try {
            String fccId = "";
            this.mReaderInfo = this.getReaderInfo();
            fccId = this.mReaderInfo.fccId.toUpperCase();
            this.mIsL2Module = (fccId.toUpperCase().contains("05WL2") || fccId.contains("10W"));
        }
        catch (Exception ex) {
            this.ELog("updateInternals() getReaderInfo error: " + ex.getMessage());
            return false;
        }
        this.mSetupRestriction = 0;
        try {
            if (!mode.equals("A") && !mode.equals("B")) {
                mode = this.getMode();
            }
            if (mode.equals("A")) {
                this.getModuleSetup(0);
            }
            this.mSetupRestriction = this.mCurrentSetup.flags;
        }
        catch (Exception ex) {
            this.mSetupRestriction = 1073741823;
            this.ELog("updateInternals() getModuleSetup error: " + ex.getMessage());
            return false;
        }
        if (mode.equals("A")) {
            try {
                this.getAntennaMapping();
            }
            catch (Exception e) {
                this.mAntennaMapPresent = false;
            }
        }
        if (mode.equals("A")) {
            try {
                this.getDeviceCaps();
            }
            catch (Exception ex2) {}
        }
        return true;
    }
    
    public void connect() throws Exception {
        boolean connFailedBailout = false;
        int connFailedAt = 0;
        if (this.mTransport == null) {
            throw new NurApiException("No transport");
        }
        if (this.mDisposed) {
            throw new NurApiException("Object disposed");
        }
        if (this.mReadThread != null) {
            throw new NurApiException("ReadThread already created!");
        }
        if (this.mWriteThread != null) {
            throw new NurApiException("WriteThread already created!");
        }
        if (this.isConnected()) {
            return;
        }
        this.mConnected = false;
        this.mConnEventSent = false;
        this.mConnectCalled = true;
        this.mChangedSetupFlags = 0;
        try {
            if (this.mTransport.disableAck()) {
                this.mHostFlags &= 0xFFFFFFFE;
                this.VLog("Transport ACK disabled");
            }
        }
        catch (AbstractMethodError ex) {
            this.VLog("Old transport library detected, missing disableAck() method. ACK will remain enabled");
        }
        try {
            this.mTransport.connect();
            this.VLog("Transport connect() called");
            this.syncReadThreadStarted = false;
            (this.mReadThread = new Thread(this.mReadRunnable)).start();
            this.syncWriteThreadStarted = false;
            (this.mWriteThread = new Thread(this.mWriteRunnable)).start();
        }
        catch (Exception ex2) {
            connFailedAt = 1;
            connFailedBailout = true;
            this.ELog("Transport connect failed");
        }
        if (!connFailedBailout) {
            try {
                connFailedAt = 2;
                synchronized (this.syncReadThreadObj) {
                    while (!this.syncReadThreadStarted) {
                        this.syncReadThreadObj.wait(5000L);
                    }
                }
                synchronized (this.syncWriteThreadObj) {
                    while (!this.syncWriteThreadStarted) {
                        this.syncWriteThreadObj.wait(5000L);
                    }
                }
                connFailedAt = 3;
                this.ping();
                connFailedAt = 0;
            }
            catch (Exception e) {
                connFailedBailout = true;
                if (connFailedAt == 2) {
                    this.ELog("Sync with threads failed.");
                }
                else {
                    this.ELog("Connection verification failed.");
                }
            }
        }
        this.mConnected = true;
        if (!connFailedBailout) {
            connFailedBailout = !this.updateInternals("");
        }
        this.mConnectCalled = false;
        if (connFailedBailout) {
            try {
                this.mTransport.disconnect();
            }
            catch (Exception ex3) {}
            try {
                this.disconnect();
            }
            catch (Exception ex4) {}
            if (connFailedAt == 1) {
                this.ELog("NurApi.connect() : failure.");
            }
            else if (connFailedAt == 2) {
                this.ELog("NurApi.connect() : read thread sync failed.");
            }
            else if (connFailedAt == 3) {
                this.ELog("NurApi.connect() : connection verification failed.");
            }
            throw new Exception("NurApi.connect() : connection failed. connFailedAt " + connFailedAt);
        }
        if (this.mListener != null) {
            this.asyncNotification(new Runnable() {
                @Override
                public void run() {
                    NurApi.this.mListener.connectedEvent();
                }
            });
        }
        this.mConnEventSent = true;
    }
    
    public void setTTScanUntilNewTagsCount(final int scanUntilNewTagsCount) {
        this.mScanUntilNewTagsCount = scanUntilNewTagsCount;
    }
    
    public int getTTScanUntilNewTagsCount() {
        return this.mScanUntilNewTagsCount;
    }
    
    private void Hook() {
    }
    
    public boolean isL2() {
        return this.mIsL2Module;
    }
    
    public void disconnect() throws Exception {
        if (this.mTransport == null) {
            return;
        }
        if (this.mDisposed) {
            throw new NurApiException("Object disposed");
        }
        this.VLog("disconnect() isconnected " + this.mTransport.isConnected());
        if (this.mTransport.isConnected()) {
            try {
                this.stopAllContinuousCommands();
            }
            catch (Exception ex) {}
            try {
                this.mTransport.disconnect();
            }
            catch (Exception e) {
                this.ELog("NurApi::disconnect error: " + e.getMessage());
            }
        }
        if (this.mReadThread != null) {
            this.mReadThread.join(1000L);
            this.mReadThread = null;
        }
        synchronized (this.writeLock) {
            this.mWriteThreadQueue.put(new NurCmd(0));
        }
        if (this.mWriteThread != null) {
            this.mWriteThread.join(1000L);
            this.mWriteThread = null;
        }
        synchronized (this) {
            this.mAntennaMapPresent = false;
            this.mReaderInfo = null;
            this.mCurrentSetup = null;
            this.mValidDevCaps = false;
            this.mDeviceCaps = null;
            this.mChangedSetupFlags = 0;
            this.mStorage.clear();
            this.mTagTrackingStorage.clear();
            this.mTagTrackingHash.clear();
            this.mInventoryExtendedStreamRunning = false;
            this.mInventoryStreamRunning = false;
            this.mTraceRunning = false;
            this.mNxpAlarmStreamRunning = false;
            this.mEpcEnumStreamRunning = false;
            this.mTagTrackingActive = false;
        }
        this.VLog("disconnect() done");
    }
    
    public boolean isConnected() {
        return !this.mDisposed && this.mConnected && this.mTransport != null && this.mTransport.isConnected();
    }
    
    private int Position2SectorNum(final float[] pos) {
        if (pos[0] == NurApi.BeamCoords[9][0] && pos[1] == NurApi.BeamCoords[9][1]) {
            return 10;
        }
        if (pos[0] < 0.0f) {
            pos[0] = 0.0f;
        }
        else if (pos[0] > 1.0f) {
            pos[0] = 1.0f;
        }
        if (pos[1] < 0.0f) {
            pos[1] = 0.0f;
        }
        else if (pos[1] > 1.0f) {
            pos[1] = 1.0f;
        }
        for (int n = 0; n < 9; ++n) {
            if (pos[0] >= NurApi.SectorPos[n][0] && pos[0] <= NurApi.SectorPos[n][0] + 0.334f && pos[1] >= NurApi.SectorPos[n][1] && pos[1] <= NurApi.SectorPos[n][1] + 0.334f) {
                return n + 1;
            }
        }
        return 0;
    }
    
    private void handleTagTrackingTag(final NurRespTagTrackingStream data, final NurTTHashObject obj, final int beam, final long epochTime) {
        boolean updateTime = false;
        boolean forceUpdate = false;
        final boolean positionCalc = (this.mTTConfig.events & 0x18) != 0x0;
        final int antMask = 1;
        final int antShiftMult = 1;
        final int beamMask = 3;
        final int beamShiftMult = 2;
        final int antCount = 32;
        final int beamCount = 13;
        final byte[] epc = obj.ttTag.epc;
        for (int i = 0; i < antCount; ++i) {
            final boolean antEnabled = antMask << i * antShiftMult != 0;
            if (antEnabled) {
                forceUpdate = true;
                break;
            }
        }
        if (!obj.seenFlag) {
            for (int n = 0; n < antCount; n += antShiftMult) {
                if ((data.lastScanMask & antMask << n) != 0x0 && obj.scaledRssi[n] != 0.0f) {
                    obj.scaledRssi[n] = 0.0f;
                    obj.ttTag.rssi[n] = -128;
                    forceUpdate = true;
                }
            }
            if (obj.beamRssi[beam] != 0.0f) {
                obj.beamRssi[beam] = 0.0f;
                forceUpdate = true;
            }
        }
        if (obj.seenFlag || forceUpdate) {
            int maxRssi = -128;
            int maxScaledRssi = 0;
            int maxRssiAnt = -1;
            for (int i = 0; i < antCount; ++i) {
                final boolean antEnabled = antMask << i * antShiftMult != 0;
                if (!antEnabled) {
                    obj.ttTag.rssi[i] = -128;
                    obj.scaledRssi[i] = 0.0f;
                }
                else if (obj.ttTag.rssi[i] > maxRssi) {
                    maxRssi = obj.ttTag.rssi[i];
                    maxScaledRssi = (int)obj.scaledRssi[i];
                    maxRssiAnt = i;
                }
            }
            for (int i = 0; i < beamCount; ++i) {
                final boolean beamEnabled = (beamMask << i * beamShiftMult & data.antennaMask) != 0x0;
                if (!beamEnabled) {
                    obj.beamRssi[i] = 0.0f;
                }
            }
            if ((this.mTTConfig.events & 0x4) != 0x0) {
                final int deltaRssi = Math.abs(Math.abs(maxRssi) - Math.abs(obj.ttTag.maxRssi));
                if (deltaRssi > this.mTTConfig.rssiDeltaFilter) {
                    final NurTTTag ttTag = obj.ttTag;
                    ttTag.changedEvents |= 0x4;
                    updateTime = true;
                }
            }
            obj.ttTag.maxRssi = maxRssi;
            obj.ttTag.maxScaledRssi = maxScaledRssi;
            if ((this.mTTConfig.events & 0x2) != 0x0 && obj.ttTag.maxRssiAnt != maxRssiAnt) {
                final NurTTTag ttTag2 = obj.ttTag;
                ttTag2.changedEvents |= 0x2;
                updateTime = true;
            }
            obj.ttTag.maxRssiAnt = maxRssiAnt;
            if (positionCalc) {
                final float[] tPos = { 0.0f, 0.0f };
                final float[] delta = { 0.0f, 0.0f };
                float rssiSum = 0.0f;
                boolean onlyFarBeam = false;
                for (int i = 0; i < beamCount; ++i) {
                    final boolean beamEnabled = (beamMask << i * beamShiftMult & data.antennaMask) != 0x0;
                    if (beamEnabled) {
                        if (i >= 9 && rssiSum <= 0.0f) {
                            onlyFarBeam = true;
                        }
                        rssiSum += obj.beamRssi[i];
                    }
                }
                if (rssiSum == 0.0f) {
                    for (int j = 0; j < obj.weightedRssi.length; ++j) {
                        obj.weightedRssi[j] = 0.0f;
                    }
                    final NurTTTag ttTag3 = obj.ttTag;
                    final NurTTTag ttTag4 = obj.ttTag;
                    final float n2 = -1.0f;
                    ttTag4.Y = n2;
                    ttTag3.X = n2;
                    return;
                }
                for (int i = 0; i < beamCount; ++i) {
                    final boolean beamEnabled = (beamMask << i * beamShiftMult & data.antennaMask) != 0x0;
                    if (beamEnabled) {
                        if (obj.beamRssi[i] != 0.0f && rssiSum > 0.0f) {
                            obj.weightedRssi[i] = obj.beamRssi[i] / rssiSum;
                        }
                        else {
                            obj.weightedRssi[i] = 0.0f;
                        }
                    }
                }
                if (onlyFarBeam) {
                    tPos[0] = NurApi.BeamCoords[NurApi.BeamNumbers[9]][0];
                    tPos[1] = NurApi.BeamCoords[NurApi.BeamNumbers[9]][1];
                }
                else {
                    for (int i = 0; i < 9; ++i) {
                        final boolean antEnabled = antMask << i * antShiftMult != 0;
                        if (antEnabled) {
                            final float[] array = tPos;
                            final int n3 = 0;
                            array[n3] += NurApi.BeamCoords[NurApi.BeamNumbers[i]][0] * obj.weightedRssi[i];
                            final float[] array2 = tPos;
                            final int n4 = 1;
                            array2[n4] += NurApi.BeamCoords[NurApi.BeamNumbers[i]][1] * obj.weightedRssi[i];
                        }
                    }
                }
                if ((this.mTTConfig.events & 0x8) != 0x0) {
                    delta[0] = tPos[0] - obj.ttTag.X;
                    delta[1] = tPos[1] - obj.ttTag.Y;
                    final float vectorLen = (float)Math.sqrt(delta[0] * delta[0] + delta[1] * delta[1]);
                    if (obj.ttTag.X < 0.0f || vectorLen > this.mTTConfig.positionDeltaFilter) {
                        final NurTTTag ttTag5 = obj.ttTag;
                        ttTag5.changedEvents |= 0x8;
                        obj.ttTag.X = tPos[0];
                        obj.ttTag.Y = tPos[1];
                        updateTime = true;
                    }
                }
                if ((this.mTTConfig.events & 0x10) != 0x0) {
                    final int sector = this.Position2SectorNum(tPos);
                    if (sector != obj.ttTag.sector) {
                        obj.ttTag.prevSector = obj.ttTag.sector;
                        obj.ttTag.sector = sector;
                        final NurTTTag ttTag6 = obj.ttTag;
                        ttTag6.changedEvents |= 0x10;
                        updateTime = true;
                    }
                }
                if ((this.mTTConfig.events & 0x40) != 0x0) {
                    final int mask = 1 << obj.ttTag.maxRssiAnt;
                    final boolean thisIn = (this.mTTConfig.inAntennaMask & mask) != 0x0;
                    final boolean thisOut = (this.mTTConfig.outAntennaMask & mask) != 0x0;
                    boolean firstIn = false;
                    boolean firstOut = false;
                    if (obj.ttTag.firstTTIOReadSource != -1) {
                        firstIn = ((this.mTTConfig.inAntennaMask & 1 << obj.ttTag.firstTTIOReadSource) != 0x0);
                        firstOut = ((this.mTTConfig.outAntennaMask & 1 << obj.ttTag.firstTTIOReadSource) != 0x0);
                    }
                    if (thisIn || thisOut) {
                        if (obj.ttTag.firstTTIOReadSource == -1) {
                            obj.ttTag.firstTTIOReadSource = obj.ttTag.maxRssiAnt;
                        }
                        else if (obj.ttTag.secondTTIOReadSource == -1) {
                            if ((firstIn && thisOut) || (!firstIn && !thisOut)) {
                                obj.ttTag.secondTTIOReadSource = obj.ttTag.maxRssiAnt;
                            }
                        }
                        else if ((thisOut && firstOut) || (thisIn && firstIn)) {
                            obj.ttTag.secondTTIOReadSource = -1;
                        }
                        else {
                            obj.ttTag.secondTTIOReadSource = obj.ttTag.maxRssiAnt;
                        }
                    }
                }
            }
        }
        if (updateTime) {
            obj.ttTag.lastUpdateTime = epochTime;
        }
    }
    
    private void handleTagTrackingChangeEvent(final byte[] eventByteData, final boolean isIrData) {
        final int epcStrPos = 0;
        int n = 0;
        int beam = 0;
        final boolean positionCalc = (this.mTTConfig.events & 0x18) != 0x0;
        int changedMask = 0;
        final int beamMask = 3;
        final int beamShift = 2;
        final long lastSeen = System.currentTimeMillis();
        if (!this.mTagTrackingActive) {
            return;
        }
        int dataLen = eventByteData.length;
        dataLen -= 13;
        NurRespTagTrackingStream data;
        try {
            data = new NurRespTagTrackingStream(eventByteData);
        }
        catch (Exception e) {
            this.ELog("handleTagTrackingChangeEvent: invalid header!");
            return;
        }
        synchronized (this.mTagTrackingSync) {
            this.mTagTrackingStorage.parseIdBuffer(this, eventByteData, 13, dataLen, true, isIrData);
            this.mTTHashChangedCount = 0;
            if (data.lastScanMask == 0) {
                beam = -1;
            }
            else {
                for (n = 0; n < 32; n += beamShift, ++beam) {
                    if ((data.lastScanMask & beamMask << n) != 0x0) {
                        break;
                    }
                }
            }
            for (int tagIndex = 0; tagIndex < this.mTagTrackingStorage.size(); ++tagIndex) {
                final NurTag currentTag = this.mTagTrackingStorage.get(tagIndex);
                final NurTTHashObject tagHashObject = this.addTagToTrackingHash(currentTag);
                if (tagHashObject != null && currentTag.tagUpdated != 0) {
                    tagHashObject.seenFlag = true;
                    tagHashObject.lastSeenTimestamp = System.currentTimeMillis();
                    tagHashObject.ttTag.lastSeenTime = lastSeen;
                    tagHashObject.scaledRssi[currentTag.mAntennaId] = (float)currentTag.mScaledRssi;
                    tagHashObject.ttTag.rssi[currentTag.mAntennaId] = currentTag.mRssi;
                    tagHashObject.beamRssi[beam] = (float)currentTag.mScaledRssi;
                    tagHashObject.antennaID = currentTag.mAntennaId;
                }
                currentTag.tagUpdated = 0;
            }
            for (int tagIndex = 0; tagIndex < this.mTagTrackingStorage.size(); ++tagIndex) {
                final NurTag currentTag = this.mTagTrackingStorage.get(tagIndex);
                final NurTTHashObject tagHashObject = this.mTagTrackingHash.get(currentTag.mEpc);
                if (tagHashObject != null) {
                    this.handleTagTrackingTag(data, tagHashObject, beamShift, lastSeen);
                }
            }
            if (data.stopped) {
                this.mTagTrackingActive = false;
            }
            if (!data.stopped && (this.mTTConfig.flags & 0x1) != 0x0) {
                changedMask = 0;
            }
            if (changedMask != 0 || data.stopped) {
                final NurEventTagTrackingChange event = new NurEventTagTrackingChange();
                event.readSource = beam;
                event.changedCount = this.mTTHashChangedCount;
                event.changedEventMask = changedMask;
                event.stopped = data.stopped;
                this.mListener.tagTrackingChangeEvent(event);
            }
            this.mTagTrackingSync.notifyAll();
        }
    }
    
    private NurTTHashObject addTagToTrackingHash(final NurTag tag) {
        NurTTHashObject obj = this.mTagTrackingHash.get(tag.mEpc);
        if (obj == null) {
            obj = new NurTTHashObject(tag);
            return this.mTagTrackingHash.put(tag.mEpc, obj);
        }
        return obj;
    }
    
    private void handleTagTrackingScanDataEvent(final byte[] eventByteData) {
        final NurEventTagTrackingData eventData = new NurEventTagTrackingData();
        eventData.started = (eventByteData[0] != 0);
        eventData.antennaMask = NurPacket.BytesToDword(eventByteData, 1);
        this.mListener.tagTrackingScanEvent(eventData);
    }
    
    private void handleTagTrackingEvent(final byte[] eventByteData, final boolean isIrData) {
        if (eventByteData != null) {
            if (eventByteData.length != 5) {
                this.handleTagTrackingChangeEvent(eventByteData, isIrData);
            }
            else {
                this.handleTagTrackingScanDataEvent(eventByteData);
            }
        }
    }
    
    private void bootSetup(final boolean app) {
        if (this.mTransport == null || !this.mTransport.isConnected()) {
            return;
        }
        this.VLog("bootSetup() app " + app + "; ConnectCalled " + this.mConnectCalled);
        try {
            Thread.sleep(200L);
        }
        catch (InterruptedException e1) {
            e1.printStackTrace();
            return;
        }
        if (!app) {
            this.updateInternals("B");
            return;
        }
        this.restoreInternals();
        this.updateInternals("A");
        if (this.mInventoryStreamRunning) {
            try {
                this.startInventoryStream();
            }
            catch (Exception e2) {
                this.mInventoryStreamRunning = false;
                this.ELog("Failed to start inventory streaming on boot.");
            }
        }
        if (this.mInventoryExtendedStreamRunning) {
            try {
                this.startInventoryExtendedStream(this.mInventoryExtended, this.mInventoryExtendedFilters);
            }
            catch (Exception e2) {
                this.mInventoryExtendedStreamRunning = false;
                this.ELog("Failed to start inventory extended streaming on boot.");
            }
        }
        if (this.mTraceRunning) {
            try {
                this.traceTag(this.mTraceTagSb.mSbBank, this.mTraceTagSb.mSbAddress, this.mTraceTagSb.mSbMaskBitLength, this.mTraceTagSb.mSbMask, this.mTraceTagFlags);
            }
            catch (Exception e2) {
                this.mTraceRunning = false;
                this.ELog("Failed to start trace tag on boot.");
            }
        }
        if (this.mNxpAlarmStreamRunning) {
            try {
                this.nxpStartAlarmStream();
            }
            catch (Exception e2) {
                this.mNxpAlarmStreamRunning = false;
                this.ELog("Failed to start NXP Alarm streaming on boot.");
            }
        }
    }
    
    private void HandleUnsolicited(final NurCmd cmd, final boolean canBeReplaced) {
        if ((this.mListener == null && this.mUnknownEventListeners == null) || this.mDisposed) {
            return;
        }
        try {
            this.asyncNotification(new NurUnsolicitedRunnable(cmd), cmd, canBeReplaced);
        }
        catch (Exception ex) {}
    }
    
    int TranslateTagError(final int tagError) {
        switch (tagError) {
            case 3: {
                return 4110;
            }
            case 4: {
                return 4111;
            }
            case 11: {
                return 4112;
            }
            case 15: {
                return 4113;
            }
            default: {
                return 66;
            }
        }
    }
    
    private void runPacketHandler() {
        while (!this.mPacketHandler.isEmpty()) {
            try {
                this.mPacketHandler.handleData();
                if (this.mPacketHandler.getState() == 4) {
                    this.Log(2, "Invalid packet");
                }
                else {
                    if (this.mPacketHandler.getState() != 3) {
                        break;
                    }
                    boolean unsol = false;
                    boolean canBeReplaced = false;
                    int command = 0;
                    int status = 0;
                    NurCmd cmd;
                    synchronized (this.readLock) {
                        final byte[] payloadData = this.mPacketHandler.getPayload();
                        final NurPacketHeader header = this.mPacketHandler.getHeader();
                        if ((header.flags & 0x4) != 0x0) {
                            this.mTransport.writeData(NurApi.ackBuf, NurApi.ackBuf.length);
                            if (header.compare(this.mLastHdr) && System.currentTimeMillis() - this.mLastHdrTime < 5000L) {
                                this.Log(1, "Duplicate packet received");
                                continue;
                            }
                            this.mLastHdr.copy(header);
                            this.mLastHdrTime = System.currentTimeMillis();
                        }
                        command = NurPacket.BytesToByte(payloadData, 0);
                        status = NurPacket.BytesToByte(payloadData, 1);
                        if (this.expectedCmd != null && this.expectedCmd.getCommand() == command) {
                            cmd = this.expectedCmd;
                        }
                        else {
                            cmd = NurCmd.createCommand(this, command);
                        }
                        if (cmd == null) {
                            this.Log(1, "Unknown packet " + command);
                        }
                        else {
                            boolean tagError = false;
                            if (status == 66) {
                                tagError = true;
                                status = this.TranslateTagError(NurPacket.BytesToByte(payloadData, 2));
                            }
                            cmd.setStatus(header.flags, status);
                            if (!tagError && header.payloadLen > 2) {
                                cmd.deserializePayload(payloadData, 2, header.payloadLen - 2);
                            }
                            if ((cmd.getFlags() & 0x1) != 0x0) {
                                if (this.expectedCmd != null && cmd.getCommand() == 128) {
                                    if (this.mConnectCalled) {
                                        this.VLog("Received NurCmdNotifyBoot during connecting, ignoring..");
                                    }
                                    else {
                                        this.VLog("Received NurCmdNotifyBoot during connecting, ignoring..");
                                        this.receivedBootEvent = (NurCmdNotifyBoot)cmd;
                                        this.receivedBootEvent.needBootSetup = false;
                                    }
                                }
                                else {
                                    unsol = true;
                                }
                            }
                            else if ((this.expectedCmd != null && this.expectedCmd.getCommand() == cmd.getCommand()) || cmd.getCommand() == 0) {
                                this.VLog("Got expected packet " + cmd.getCommand() + " status " + cmd.getStatus());
                                if (this.expectedCmd.getCommand() == 34 && cmd.getStatus() == 0) {
                                    final NurCmdLoadSetup sp = (NurCmdLoadSetup)cmd;
                                    if (this.mSentSetupFlags != 0 && sp.getFlags() != this.mSentSetupFlags) {
                                        this.Log(2, "Different setup flags received. Flags " + sp.getFlags() + "; sent flags " + this.mSentSetupFlags);
                                        continue;
                                    }
                                }
                                this.retCmd = cmd;
                                this.readLockCond = true;
                                this.readLock.notifyAll();
                            }
                            else {
                                this.Log(2, "Received unexpected packet " + cmd.getCommand());
                            }
                        }
                    }
                    if (!unsol) {
                        continue;
                    }
                    canBeReplaced = (command == 59 || command == 130);
                    this.HandleUnsolicited(cmd, canBeReplaced);
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    
    private void readThread() {
        final byte[] buffer = new byte[4096];
        int bytesRead = 0;
        try {
            this.mReadThread.setPriority(this.mReadThread.getPriority() + 1);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        synchronized (this.syncReadThreadObj) {
            this.syncReadThreadStarted = true;
            this.syncReadThreadObj.notifyAll();
        }
        this.VLog("ReadThread ENTER");
        while (this.mTransport != null && this.mTransport.isConnected()) {
            try {
                bytesRead = this.mTransport.readData(buffer);
                if (bytesRead > 0) {
                    this.doDataLog(buffer, bytesRead, true);
                    this.mPacketHandler.writeData(buffer, bytesRead);
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
            synchronized (this.mPacketHandlerLock) {
                this.mPacketHandlerLock.notifyAll();
            }
        }
        synchronized (this.readLock) {
            this.VLog("ReadThread NOTIFY");
            this.readLockCond = true;
            this.readLock.notifyAll();
        }
        this.VLog("ReadThread EXIT");
        this.mInventoryExtendedStreamRunning = false;
        this.mInventoryStreamRunning = false;
        this.mTraceRunning = false;
        this.mNxpAlarmStreamRunning = false;
        this.mEpcEnumStreamRunning = false;
        this.mTagTrackingActive = false;
        this.mAntennaMap = null;
        this.mAntennaMapPresent = false;
        this.mValidDevCaps = false;
        this.mDeviceCaps = null;
        this.mReaderInfo = null;
        this.mConnected = false;
        if (this.mListener != null && this.mConnEventSent) {
            this.asyncNotification(new Runnable() {
                @Override
                public void run() {
                    NurApi.this.mListener.disconnectedEvent();
                }
            });
        }
        this.mConnEventSent = false;
        this.mReadThread = null;
    }
    
    private void writeThread() {
        try {
            this.mWriteThread.setPriority(this.mWriteThread.getPriority() + 1);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        this.mWriteThreadQueue.clear();
        synchronized (this.syncWriteThreadObj) {
            this.syncWriteThreadStarted = true;
            this.syncWriteThreadObj.notifyAll();
        }
        this.VLog("WriteThread ENTER");
        NurCmd cmd = null;
        while (true) {
            try {
                cmd = this.mWriteThreadQueue.take();
            }
            catch (InterruptedException ex3) {}
            if (cmd.getCommand() == 0 || this.mDisposed || this.mTransport == null || !this.mTransport.isConnected()) {
                break;
            }
            synchronized (this.writeLock) {
                try {
                    if (NurPacket.write(this.mTransport, cmd) == -1) {
                        this.Log(2, "exchangeCommand() " + cmd.getCommand() + "; write failure");
                        this.writeFail = true;
                    }
                }
                catch (Exception ex2) {
                    ex2.printStackTrace();
                    this.writeFail = true;
                }
                this.writeLockCond = true;
                this.writeLock.notifyAll();
            }
        }
        synchronized (this.writeLock) {
            this.VLog("WriteThread NOTIFY WRLOCK");
            this.writeLockCond = true;
            this.writeLock.notifyAll();
        }
        this.VLog("WriteThread EXIT");
    }
    
    public NurTagStorage getStorage() {
        return this.mStorage;
    }
    
    public void doDataLog(final byte[] d, final int len, final boolean dataIn) {
        if ((this.mLogLevel & 0x8) != 0x0) {
            final StringBuilder str = new StringBuilder();
            if (dataIn) {
                str.append("IN: ");
            }
            else {
                str.append("OUT: ");
            }
            for (int i = 0; i < len; ++i) {
                str.append(String.format("%02X", d[i]));
            }
            this.DLog(str.toString());
        }
    }
    
    public NurCmd exchangeCommand(final NurCmd cmd, int timeout) throws Exception {
        int cmdNum = 0;
        int sendCount = 2;
        NurCmd succeededCmd = null;
        if (this.mTransport == null) {
            throw new NurApiException(4097);
        }
        if (!this.mTransport.isConnected()) {
            throw new NurApiException(4098);
        }
        cmdNum = cmd.getCommand();
        if (timeout == 0) {
            timeout = cmd.getTimeout();
        }
        if (timeout == 0) {
            timeout = 8000;
        }
        this.Log(1, "exchangeCommand() " + cmdNum + "; timeout = " + timeout);
        if (timeout == 1) {
            this.mWriteThreadQueue.put(cmd);
            throw new TimeoutException();
        }
        synchronized (this) {
            if (this.mTransport == null) {
                throw new NurApiException(4097);
            }
            if (!this.mTransport.isConnected()) {
                throw new NurApiException(4098);
            }
            this.receivedBootEvent = null;
            while (sendCount-- > 0) {
                cmd.setOwner(this);
                synchronized (this.readLock) {
                    final long t1 = System.currentTimeMillis();
                    this.retCmd = null;
                    this.expectedCmd = cmd;
                    this.retInvalid = false;
                    this.writeFail = false;
                    this.readLockCond = false;
                    this.writeLockCond = false;
                    try {
                        boolean cmdPutInQueue = false;
                        while (!this.writeLockCond) {
                            synchronized (this.writeLock) {
                                if (!cmdPutInQueue) {
                                    cmdPutInQueue = true;
                                    this.mWriteThreadQueue.put(cmd);
                                }
                                final int wrtimeout = (int)(10000L - (System.currentTimeMillis() - t1));
                                if (wrtimeout <= 0) {
                                    throw new TimeoutException();
                                }
                                this.writeLock.wait(wrtimeout);
                                if (!this.mTransport.isConnected()) {
                                    break;
                                }
                                continue;
                            }
                        }
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        this.writeFail = true;
                    }
                    if (!this.mTransport.isConnected()) {
                        this.VLog("exchangeCommand() transport disconnected during write!");
                        throw new NurApiException(4098);
                    }
                    if (this.writeFail) {
                        try {
                            this.Log(2, "exchangeCommand() " + cmdNum + "; Write failure, call transport.disconnect()");
                            this.mTransport.disconnect();
                        }
                        catch (Exception e2) {
                            e2.printStackTrace();
                        }
                        throw new IOException("Write failed");
                    }
                    try {
                        while (!this.readLockCond) {
                            final int rdtimeout = (int)(timeout - (System.currentTimeMillis() - t1));
                            if (rdtimeout <= 0) {
                                this.ELog("exchangeCommand() Read timeout cmd " + cmd.getCommand());
                                break;
                            }
                            this.readLock.wait(timeout);
                            if (!this.mTransport.isConnected()) {
                                break;
                            }
                        }
                    }
                    catch (InterruptedException e3) {
                        e3.printStackTrace();
                    }
                    if (!this.mTransport.isConnected()) {
                        this.VLog("exchangeCommand() transport disconnected during read response!");
                        throw new NurApiException(4098);
                    }
                    final long t2 = System.currentTimeMillis();
                    this.expectedCmd = null;
                    if (this.retInvalid) {
                        this.Log(2, "exchangeCommand() Invalid packet returned for " + cmd.getCommand());
                        throw new NurApiException(4103);
                    }
                    if (this.retCmd == null) {
                        if (timeout != 1) {
                            this.Log(2, "exchangeCommand() Timeout cmd = " + cmd.getCommand() + " in " + (t2 - t1) + " ms.");
                            if (sendCount > 0) {
                                this.Log(2, "Resending packet..");
                                continue;
                            }
                        }
                        throw new TimeoutException();
                    }
                    final int retStatus = this.retCmd.getStatus();
                    if (retStatus != 0) {
                        if (retStatus == 17 && this.retCmd.getCommand() == 0 && sendCount > 0) {
                            this.Log(2, "exchangeCommand() " + cmd.getCommand() + "; device requested resending packet.");
                            continue;
                        }
                        throw new NurApiException(this.retCmd.getStatus());
                    }
                    else {
                        succeededCmd = this.retCmd;
                        this.Log(1, "exchangeCommand() " + cmd.getCommand() + "; Status " + succeededCmd.getStatus() + " Succeeded in " + (t2 - t1) + " ms");
                    }
                }
                break;
            }
            if (this.receivedBootEvent != null) {
                final NurCmdNotifyBoot bootEvent = this.receivedBootEvent;
                this.VLog("exchangeCommand() cmd " + cmd.getCommand() + "; Received bootevent during command reception");
                final String bootStr = bootEvent.getResponse();
                final boolean app = bootStr.compareToIgnoreCase("app") == 0;
                this.bootSetup(app);
                this.VLog("exchangeCommand() bootSetup done; bootStr " + bootStr);
                this.HandleUnsolicited(bootEvent, false);
            }
        }
        if (succeededCmd == null) {
            this.ELog("Unexpected exchangeCommand failure!");
            throw new NurApiException(16);
        }
        return succeededCmd;
    }
    
    public NurCmd exchangeCommand(final NurCmd cmd) throws Exception {
        return this.exchangeCommand(cmd, 0);
    }
    
    public String getFileVersion() {
        return "1.9.1.3";
    }
    
    public String ping() throws Exception {
        return ((NurCmdPing)this.exchangeCommand(new NurCmdPing(this.mHostFlags))).getResponse();
    }
    
    public Map<String, String> getFwInfo() throws Exception {
        final HashMap<String, String> ret = new HashMap<String, String>();
        String fwinfo = ((NurCmdGetFwInfo)this.exchangeCommand(new NurCmdGetFwInfo())).getResponse();
        if (fwinfo.contains("<FWINFO>")) {
            fwinfo = fwinfo.replace("<FWINFO>", "");
            fwinfo = fwinfo.replace("</FWINFO>", "");
            final String[] pairs = fwinfo.split(";");
            for (int n = 0; n < pairs.length; ++n) {
                final String[] pair = pairs[n].split("=");
                if (pair.length == 2) {
                    ret.put(pair[0], pair[1]);
                }
            }
        }
        return ret;
    }
    
    public NurRespVersions getVersions() throws Exception {
        return ((NurCmdVersions)this.exchangeCommand(new NurCmdVersions())).getResponse();
    }
    
    public void reset() throws Exception {
        this.exchangeCommand(new NurCmdReset());
    }
    
    public String getMode() throws Exception {
        this.mValidDevCaps = false;
        return ((NurCmdGetMode)this.exchangeCommand(new NurCmdGetMode())).getResponse();
    }
    
    public void clearIdBuffer(final boolean clearInternal) throws Exception {
        if (clearInternal) {
            this.mStorage.clear();
        }
        this.exchangeCommand(new NurCmdClearIdBuffer());
    }
    
    public void clearIdBuffer() throws Exception {
        this.clearIdBuffer(true);
    }
    
    public void fetchTags(final boolean includeMeta) throws Exception {
        this.exchangeCommand(new NurCmdFetchTags(includeMeta));
    }
    
    public NurTag fetchTagAt(final boolean includeMeta, final int num) throws Exception {
        return ((NurCmdFetchTagAt)this.exchangeCommand(new NurCmdFetchTagAt(includeMeta, num, this.mXPCRemovalEnabled))).getResponse();
    }
    
    public NurTag fetchTagAt(final boolean includeMeta, final int num, final boolean removeXPC) throws Exception {
        return ((NurCmdFetchTagAt)this.exchangeCommand(new NurCmdFetchTagAt(includeMeta, num, removeXPC))).getResponse();
    }
    
    public void fetchTags() throws Exception {
        this.fetchTags(true);
    }
    
    public NurRespSystemInfo getSystemInfo() throws Exception {
        return ((NurCmdGetSystem)this.exchangeCommand(new NurCmdGetSystem())).getResponse();
    }
    
    public NurRespReaderInfo getReaderInfo() throws Exception {
        if (this.mReaderInfo == null) {
            this.mReaderInfo = ((NurCmdReaderInfo)this.exchangeCommand(new NurCmdReaderInfo())).getResponse();
        }
        return this.mReaderInfo;
    }
    
    public NurRespDevCaps getDeviceCaps() throws Exception {
        if (this.mDeviceCaps == null) {
            this.mValidDevCaps = false;
        }
        if (!this.mValidDevCaps) {
            this.mDeviceCaps = ((NurCmdDevCaps)this.exchangeCommand(new NurCmdDevCaps())).getResponse();
        }
        this.mIsL2Module = (this.mDeviceCaps.chipVersion == 2);
        this.mValidDevCaps = (this.mDeviceCaps != null);
        return this.mDeviceCaps;
    }
    
    public void beep(final int frequency, final int time, final int duty) throws Exception {
        this.exchangeCommand(new NurCmdBeep(frequency, time, duty));
    }
    
    public void beep() throws Exception {
        this.exchangeCommand(new NurCmdBeep());
    }
    
    public int beepBroadcast() throws Exception {
        return this.sendBroadcast(5, 0, 0, null, 0, null, 0);
    }
    
    public int beepBroadcast(final NurBroadcastFilter filter) throws Exception {
        return this.sendBroadcast(5, filter.filterType, filter.filterOp, filter.filterData, filter.filterSize, null, 0);
    }
    
    public int beepBroadcast(final NurBroadcastFilter filter, final int time, final int duty) throws Exception {
        final byte[] data = new byte[2];
        NurPacket.PacketByte(data, 0, time);
        NurPacket.PacketByte(data, 1, duty);
        return this.sendBroadcast(5, filter.filterType, filter.filterOp, filter.filterData, filter.filterSize, data, 2);
    }
    
    public int beepBroadcast(final int time, final int duty) throws Exception {
        final byte[] data = new byte[2];
        NurPacket.PacketByte(data, 0, time);
        NurPacket.PacketByte(data, 1, duty);
        return this.sendBroadcast(5, 0, 0, null, 0, data, 2);
    }
    
    public void stopAllContinuousCommands() throws Exception {
        final NurCmdStopContinuous cmd = new NurCmdStopContinuous();
        cmd.timeout = 1;
        try {
            this.exchangeCommand(cmd);
        }
        catch (Exception ex) {}
        this.mInventoryExtendedStreamRunning = false;
        this.mInventoryStreamRunning = false;
        this.mTraceRunning = false;
        this.mNxpAlarmStreamRunning = false;
        this.mEpcEnumStreamRunning = false;
        this.mTagTrackingActive = false;
    }
    
    public NurGPIOConfig[] getGPIOConfigure() throws Exception {
        return ((NurCmdGPIOConfig)this.exchangeCommand(new NurCmdGPIOConfig())).getResponse();
    }
    
    public void setGPIOConfigure(final NurGPIOConfig config) throws Exception {
        this.exchangeCommand(new NurCmdGPIOConfig(config));
    }
    
    public void setGPIOConfigure(final NurGPIOConfig[] config) throws Exception {
        this.exchangeCommand(new NurCmdGPIOConfig(config));
    }
    
    public NurRespGPIOStatus getGPIOStatus(final int gpio) throws Exception {
        return ((NurCmdGetGPIO)this.exchangeCommand(new NurCmdGetGPIO(gpio))).getResponse();
    }
    
    public void setGPIOStatus(final int gpio, final boolean state) throws Exception {
        this.exchangeCommand(new NurCmdSetGPIO(gpio, state));
    }
    
    public void setGPIOStatusEx(final int gpio, final boolean state) throws Exception {
        this.exchangeCommand(new NurCmdSetGPIO(gpio, state, false));
    }
    
    public NurSensors getSensors() throws Exception {
        return ((NurCmdSensors)this.exchangeCommand(new NurCmdSensors())).getResponse();
    }
    
    public NurSensors setSensors(final NurSensors sensors) throws Exception {
        return ((NurCmdSensors)this.exchangeCommand(new NurCmdSensors(sensors))).getResponse();
    }
    
    public void factoryReset(final int resetCode) throws Exception {
        this.exchangeCommand(new NurCmdFactoryReset(resetCode));
    }
    
    public void moduleRestart() throws Exception {
        this.exchangeCommand(new NurCmdRestart());
        Thread.sleep(200L);
    }
    
    public String getTitle() throws Exception {
        return ((NurCmdTitleGet)this.exchangeCommand(new NurCmdTitleGet())).getResponse().trim();
    }
    
    public String setTitle(final String title) throws Exception {
        return ((NurCmdTitleSet)this.exchangeCommand(new NurCmdTitleSet(title))).getResponse();
    }
    
    public NurEthConfig getEthConfig() throws Exception {
        return ((NurCmdGetEthConfig)this.exchangeCommand(new NurCmdGetEthConfig())).getResponse();
    }
    
    public ArrayList<NurEthConfig> queryEthDevices() throws Exception {
        return this.queryEthDevices(null);
    }
    
    public ArrayList<NurEthConfig> queryEthDevices(final NurBroadcastFilter filter) throws Exception {
        synchronized (this.mSyncEthQueryMutex) {
            if (filter == null) {
                this.mSyncEthQueryBroadcast = new SendBroadcastMessage(this, new NurBroadcastMessage(255, 2, 161, 0, 0, null, 0, 1, 0, null));
            }
            else {
                this.mSyncEthQueryBroadcast = new SendBroadcastMessage(this, new NurBroadcastMessage(255, 2, 161, filter.filterType, filter.filterOp, filter.filterData, filter.filterSize, 1, 0, null));
            }
            this.mSyncEthQueryBroadcast.init(false);
            (this.mEthQueryThread = new Thread(this.mSyncEthQueryBroadcast)).start();
            this.mEthQueryThread.join();
        }
        return this.mSyncEthQueryBroadcast.getEthResponses();
    }
    
    public void setEthConfigBroadcast(final NurEthConfig ethConfig) throws Exception {
        final String[] parts = ethConfig.mac.split(":");
        if (parts.length != 6) {
            throw new NurApiException(5);
        }
        final byte[] mac = new byte[6];
        for (int i = 0; i < 6; ++i) {
            mac[i] = (byte)Integer.parseInt(parts[i], 16);
        }
        final byte[] data = new byte[200];
        int offset = NurPacket.PacketByte(data, 0, ethConfig.title.length());
        final byte[] arr = new byte[32];
        for (int i = 0; i < ethConfig.title.length(); ++i) {
            arr[i] = (byte)ethConfig.title.charAt(i);
        }
        offset += NurPacket.PacketBytes(data, offset, arr, 32);
        String[] tmp = ethConfig.mask.split("\\.");
        if (tmp.length == 4) {
            for (int i = 0; i < 4; ++i) {
                offset += NurPacket.PacketByte(data, offset, Integer.parseInt(tmp[i]));
            }
        }
        else {
            for (int i = 0; i < 4; ++i) {
                offset += NurPacket.PacketByte(data, offset, 0);
            }
        }
        tmp = ethConfig.gw.split("\\.");
        if (tmp.length == 4) {
            for (int i = 0; i < 4; ++i) {
                offset += NurPacket.PacketByte(data, offset, Integer.parseInt(tmp[i]));
            }
        }
        else {
            for (int i = 0; i < 4; ++i) {
                offset += NurPacket.PacketByte(data, offset, 0);
            }
        }
        offset += NurPacket.PacketByte(data, offset, ethConfig.addrType);
        tmp = ethConfig.staticip.split("\\.");
        if (tmp.length == 4) {
            for (int i = 0; i < 4; ++i) {
                offset += NurPacket.PacketByte(data, offset, Integer.parseInt(tmp[i]));
            }
        }
        else {
            for (int i = 0; i < 4; ++i) {
                offset += NurPacket.PacketByte(data, offset, 0);
            }
        }
        offset += NurPacket.PacketByte(data, offset, ethConfig.serverPort >> 8);
        offset += NurPacket.PacketByte(data, offset, ethConfig.serverPort);
        offset += NurPacket.PacketByte(data, offset, ethConfig.hostMode);
        tmp = ethConfig.hostip.split("\\.");
        if (tmp.length == 4) {
            for (int i = 0; i < 4; ++i) {
                offset += NurPacket.PacketByte(data, offset, Integer.parseInt(tmp[i]));
            }
        }
        else {
            for (int i = 0; i < 4; ++i) {
                offset += NurPacket.PacketByte(data, offset, 0);
            }
        }
        offset += NurPacket.PacketByte(data, offset, ethConfig.hostPort >> 8);
        offset += NurPacket.PacketByte(data, offset, ethConfig.hostPort);
        offset += NurPacket.PacketBytes(data, offset, ethConfig.reserved, 8);
        if (this.sendBroadcast(4, 1, 0, mac, 6, data, offset) != 1) {
            throw new NurApiException("No response on setEthConfigBroadcast()");
        }
    }
    
    public void setEthConfig(final NurEthConfig ethConfig) throws Exception {
        this.exchangeCommand(new NurCmdSetEthConfig(ethConfig));
    }
    
    public String getBroadcastAddress() {
        return this.mBroadcastAddress;
    }
    
    public void setBroadcastAddress(final String broadcastAddress) {
        this.mBroadcastAddress = broadcastAddress;
    }
    
    private int sendBroadcast(final int cmd, final int filterType, final int filterOp, final byte[] filterData, final int filterSize, final byte[] data, final int dataLength) throws Exception {
        if (this.mGenericBCastThread != null && this.mGenericBCastThread.isAlive()) {
            return 0;
        }
        if (this.isDisposed()) {
            return 0;
        }
        final SendBroadcastMessage bcm = new SendBroadcastMessage(this, new NurBroadcastMessage(255, 2, 161, filterType, filterOp, filterData, filterSize, cmd, dataLength, data));
        (this.mGenericBCastThread = new Thread(bcm)).start();
        if (cmd == 1) {
            return 0;
        }
        this.mGenericBCastThread.join();
        return bcm.getAnswerCount();
    }
    
    public int getBaudrate() throws Exception {
        return ((NurCmdSetBaudrate)this.exchangeCommand(new NurCmdSetBaudrate())).getResponse();
    }
    
    public int setBaudrate(final int baudrate) throws Exception {
        return ((NurCmdSetBaudrate)this.exchangeCommand(new NurCmdSetBaudrate(baudrate))).getResponse();
    }
    
    public void storeSetup(final int flags) throws Exception {
        this.exchangeCommand(new NurCmdStoreSetup(flags));
        this.mChangedSetupFlags = 0;
    }
    
    public NurSetup getModuleSetup() throws Exception {
        return this.getModuleSetup(0x3FFFFFFF & this.mSetupRestriction);
    }
    
    public void saveSetupFile(final String fileName) throws Exception {
        final File f = new File(fileName);
        if (!f.exists()) {
            f.createNewFile();
        }
        final NurIniFile ini = new NurIniFile();
        final NurRespReaderInfo readerInfo = this.getReaderInfo();
        final NurSetup setup = this.getModuleSetup();
        if ((setup.flags & 0x1) != 0x0) {
            ini.put("NUR", "LinkFrequency", setup.linkFreq);
        }
        if ((setup.flags & 0x2) != 0x0) {
            ini.put("NUR", "ReceiverDecoding", setup.rxDecoding);
        }
        if ((setup.flags & 0x8) != 0x0) {
            ini.put("NUR", "TxModulation", setup.txModulation);
        }
        if ((setup.flags & 0x4) != 0x0) {
            ini.put("NUR", "TxLevel", setup.txLevel);
        }
        if ((setup.flags & 0x10) != 0x0) {
            ini.put("NUR", "Region", setup.regionId);
        }
        if ((setup.flags & 0x10000000) != 0x0) {
            ini.put("NUR", "RxSensitivity", setup.rxSensitivity);
        }
        if ((setup.flags & 0x20000000) != 0x0) {
            ini.put("NUR", "RfProfile", setup.rfProfile);
        }
        if ((setup.flags & 0x20) != 0x0) {
            ini.put("NUR", "InventoryQ", setup.inventoryQ);
        }
        if ((setup.flags & 0x40) != 0x0) {
            ini.put("NUR", "InventorySession", setup.inventorySession);
        }
        if ((setup.flags & 0x80) != 0x0) {
            ini.put("NUR", "InventoryRounds", setup.inventoryRounds);
        }
        if ((setup.flags & 0x2000) != 0x0) {
            ini.put("NUR", "InventoryTarget", setup.inventoryTarget);
        }
        if ((setup.flags & 0x4000) != 0x0) {
            ini.put("NUR", "InventoryEpcLength", setup.inventoryEpcLength);
        }
        if ((setup.flags & 0x400000) != 0x0) {
            ini.put("NUR", "DutyCycle", setup.periodSetup);
        }
        if ((setup.flags & 0x100) != 0x0) {
            ini.put("NUR", "AntennaMask", setup.antennaMask);
        }
        if ((setup.flags & 0x2000000) != 0x0) {
            ini.put("NUR", "AntennaMaskEx", setup.antennaMaskEx);
        }
        if ((setup.flags & 0x800) != 0x0) {
            ini.put("NUR", "SelectedAntenna", setup.selectedAntenna);
        }
        if ((setup.flags & 0x200) != 0x0) {
            ini.put("NUR", "ScanSingleTimeout", setup.scanSingleTriggerTimeout);
        }
        if ((setup.flags & 0x400) != 0x0) {
            ini.put("NUR", "InventoryTimeout", setup.inventoryTriggerTimeout);
        }
        if ((setup.flags & 0x40000) != 0x0) {
            ini.put("NUR", "ReadTimeout", setup.readTimeout);
        }
        if ((setup.flags & 0x80000) != 0x0) {
            ini.put("NUR", "WriteTimeout", setup.writeTimeout);
        }
        if ((setup.flags & 0x100000) != 0x0) {
            ini.put("NUR", "LockTimeout", setup.lockTimeout);
        }
        if ((setup.flags & 0x200000) != 0x0) {
            ini.put("NUR", "KillTimeout", setup.killTimeout);
        }
        if ((setup.flags & 0x4000000) != 0x0) {
            ini.put("NUR", "AutoTuneMode", setup.autotune.mode);
            ini.put("NUR", "AutoTuneThreshold", setup.autotune.thresholddBm);
        }
        if ((setup.flags & 0x8000) != 0x0) {
            ini.put("NUR", "rssiFilterReadMin", setup.readRssiFilter.min);
            ini.put("NUR", "rssiFilterReadMax", setup.readRssiFilter.max);
        }
        if ((setup.flags & 0x10000) != 0x0) {
            ini.put("NUR", "rssiFilterWriteMin", setup.writeRssiFilter.min);
            ini.put("NUR", "rssiFilterWriteMax", setup.writeRssiFilter.max);
        }
        if ((setup.flags & 0x20000) != 0x0) {
            ini.put("NUR", "rssiFilterInvMin", setup.inventoryRssiFilter.min);
            ini.put("NUR", "rssiFilterInvMax", setup.inventoryRssiFilter.max);
        }
        if ((setup.flags & 0x800000) != 0x0) {
            ini.put("NUR", "AntennaPower", setup.antPower[0] + "," + setup.antPower[1] + "," + setup.antPower[2] + "," + setup.antPower[3]);
        }
        if ((setup.flags & 0x8000000) != 0x0) {
            String val = "";
            for (int n = 0; n < 32; ++n) {
                if (n > 0) {
                    val += ",";
                }
                val += setup.antPowerEx[n];
            }
            ini.put("NUR", "AntennaPowerEx", val);
        }
        if ((setup.flags & 0x1000000) != 0x0) {
            ini.put("NUR", "PowerOffset", setup.powerOffset + "," + setup.powerOffset + "," + setup.powerOffset + "," + setup.powerOffset);
        }
        if ((setup.flags & 0x1000) != 0x0) {
            ini.put("NUR", "OpFlags", setup.opFlags);
        }
        if (readerInfo.numSensors > 0) {
            try {
                final NurSensors sensors = this.getSensors();
                ini.put("NurSensor", "Tap", (sensors.tapSensorEnabled ? 1 : 0) + "," + sensors.tapAction);
                ini.put("NurSensor", "Light", (sensors.lightSensorEnabled ? 1 : 0) + "," + sensors.lightAction);
            }
            catch (Exception ex) {}
        }
        if (readerInfo.numGpio > 0) {
            try {
                final NurGPIOConfig[] gpioConfig = this.getGPIOConfigure();
                for (int i = 0; i < readerInfo.numGpio; ++i) {
                    ini.put("NurGpio", "Gpio" + i, String.format("%d,%d,%d,%d,%d", gpioConfig[i].available ? 1 : 0, gpioConfig[i].enabled ? 1 : 0, gpioConfig[i].type, gpioConfig[i].edge, gpioConfig[i].action));
                }
            }
            catch (Exception ex2) {}
        }
        try {
            final int baudrate = this.getBaudrate();
            ini.put("NUR", "Baudrate", baudrate);
        }
        catch (Exception ex3) {}
        ini.save(fileName);
    }
    
    public void loadSetupFile(final String fileName) throws Exception {
        final NurIniFile ini = new NurIniFile(fileName);
        final int baudrate = ini.getInt("NUR", "Baudrate", 0);
        this.setBaudrate(baudrate);
        final NurSensors sensors = new NurSensors();
        String[] arr = null;
        final String sensorValue = ini.get("NurSensor", "Tap");
        if (sensorValue != null) {
            arr = sensorValue.split(",");
            if (arr.length == 2) {
                sensors.tapSensorEnabled = arr[0].equals("1");
                sensors.tapAction = Integer.parseInt(arr[1]);
            }
        }
        final String sensorValue2 = ini.get("NurSensor", "Light");
        if (sensorValue2 != null) {
            arr = sensorValue2.split(",");
            if (arr.length == 2) {
                sensors.lightSensorEnabled = arr[0].equals("1");
                sensors.lightAction = Integer.parseInt(arr[1]);
            }
        }
        if (sensorValue != null || sensorValue2 != null) {
            this.setSensors(sensors);
        }
        final NurRespReaderInfo readerInfo = this.getReaderInfo();
        final NurGPIOConfig[] gpioConfig = new NurGPIOConfig[readerInfo.numGpio];
        String gpio = "";
        for (int i = 0; i < readerInfo.numGpio; ++i) {
            gpioConfig[i] = new NurGPIOConfig();
            gpio = ini.get("NurGpio", "Gpio" + i);
            if (gpio != null) {
                arr = gpio.split(",");
                if (arr.length == 5) {
                    gpioConfig[i].available = arr[0].equals("1");
                    gpioConfig[i].enabled = arr[1].equals("1");
                    gpioConfig[i].type = Integer.parseInt(arr[2]);
                    gpioConfig[i].edge = Integer.parseInt(arr[3]);
                    gpioConfig[i].action = Integer.parseInt(arr[4]);
                }
            }
        }
        if (readerInfo.numGpio != 0) {
            int totalConfigs = 0;
            for (int j = 0; j < gpioConfig.length; ++j) {
                if (gpioConfig[j].available) {
                    ++totalConfigs;
                }
            }
            if (totalConfigs != 0) {
                this.setGPIOConfigure(gpioConfig);
            }
        }
        final NurSetup setup = new NurSetup();
        int setupFlags = 0;
        String tmp = ini.get("NUR", "LinkFrequency");
        if (tmp != null) {
            setup.linkFreq = Integer.parseInt(tmp);
            setupFlags |= 0x1;
        }
        tmp = ini.get("NUR", "ReceiverDecoding");
        if (tmp != null) {
            setup.rxDecoding = Integer.parseInt(tmp);
            setupFlags |= 0x2;
        }
        tmp = ini.get("NUR", "TxModulation");
        if (tmp != null) {
            setup.txModulation = Integer.parseInt(tmp);
            setupFlags |= 0x8;
        }
        tmp = ini.get("NUR", "TxLevel");
        if (tmp != null) {
            setup.txLevel = Integer.parseInt(tmp);
            setupFlags |= 0x4;
        }
        tmp = ini.get("NUR", "Region");
        if (tmp != null) {
            setup.regionId = Integer.parseInt(tmp);
            setupFlags |= 0x10;
        }
        tmp = ini.get("NUR", "RxSensitivity");
        if (tmp != null) {
            setup.rxSensitivity = Integer.parseInt(tmp);
            setupFlags |= 0x10000000;
        }
        tmp = ini.get("NUR", "RfProfile");
        if (tmp != null) {
            setup.rfProfile = Integer.parseInt(tmp);
            setupFlags |= 0x20000000;
        }
        tmp = ini.get("NUR", "InventoryQ");
        if (tmp != null) {
            setup.inventoryQ = Integer.parseInt(tmp);
            setupFlags |= 0x20;
        }
        tmp = ini.get("NUR", "InventorySession");
        if (tmp != null) {
            setup.inventorySession = Integer.parseInt(tmp);
            setupFlags |= 0x40;
        }
        tmp = ini.get("NUR", "InventoryRounds");
        if (tmp != null) {
            setup.inventoryRounds = Integer.parseInt(tmp);
            setupFlags |= 0x80;
        }
        tmp = ini.get("NUR", "AntennaMask");
        if (tmp != null) {
            setup.antennaMask = Integer.parseInt(tmp);
            setupFlags |= 0x100;
        }
        tmp = ini.get("NUR", "AntennaMaskEx");
        if (tmp != null) {
            setup.antennaMaskEx = Integer.parseInt(tmp);
            setupFlags |= 0x2000000;
        }
        tmp = ini.get("NUR", "SelectedAntenna");
        if (tmp != null) {
            setup.selectedAntenna = Integer.parseInt(tmp);
            setupFlags |= 0x800;
        }
        tmp = ini.get("NUR", "InventoryTarget");
        if (tmp != null) {
            setup.inventoryTarget = Integer.parseInt(tmp);
            setupFlags |= 0x2000;
        }
        tmp = ini.get("NUR", "InventoryEpcLength");
        if (tmp != null) {
            setup.inventoryEpcLength = Integer.parseInt(tmp);
            setupFlags |= 0x4000;
        }
        tmp = ini.get("NUR", "ScanSingleTimeout");
        if (tmp != null) {
            setup.scanSingleTriggerTimeout = Integer.parseInt(tmp);
            setupFlags |= 0x200;
        }
        tmp = ini.get("NUR", "InventoryTimeout");
        if (tmp != null) {
            setup.inventoryTriggerTimeout = Integer.parseInt(tmp);
            setupFlags |= 0x400;
        }
        tmp = ini.get("NUR", "ReadTimeout");
        if (tmp != null) {
            setup.readTimeout = Integer.parseInt(tmp);
            setupFlags |= 0x40000;
        }
        tmp = ini.get("NUR", "WriteTimeout");
        if (tmp != null) {
            setup.writeTimeout = Integer.parseInt(tmp);
            setupFlags |= 0x80000;
        }
        tmp = ini.get("NUR", "LockTimeout");
        if (tmp != null) {
            setup.lockTimeout = Integer.parseInt(tmp);
            setupFlags |= 0x100000;
        }
        tmp = ini.get("NUR", "KillTimeout");
        if (tmp != null) {
            setup.killTimeout = Integer.parseInt(tmp);
            setupFlags |= 0x200000;
        }
        tmp = ini.get("NUR", "DutyCycle");
        if (tmp != null) {
            setup.periodSetup = Integer.parseInt(tmp);
            setupFlags |= 0x400000;
        }
        tmp = ini.get("NUR", "OpFlags");
        if (tmp != null) {
            setup.opFlags = Integer.parseInt(tmp);
            setupFlags |= 0x1000;
        }
        tmp = ini.get("NUR", "rssiFilterReadMin");
        String tmp2 = ini.get("NUR", "rssiFilterReadMax");
        if (tmp != null && tmp2 != null) {
            setup.readRssiFilter.min = Integer.parseInt(tmp);
            setup.readRssiFilter.max = Integer.parseInt(tmp2);
            setupFlags |= 0x8000;
        }
        tmp = ini.get("NUR", "rssiFilterWriteMin");
        tmp2 = ini.get("NUR", "rssiFilterWriteMax");
        if (tmp != null && tmp2 != null) {
            setup.writeRssiFilter.min = Integer.parseInt(tmp);
            setup.writeRssiFilter.max = Integer.parseInt(tmp2);
            setupFlags |= 0x10000;
        }
        tmp = ini.get("NUR", "rssiFilterInvMin");
        tmp2 = ini.get("NUR", "rssiFilterInvMax");
        if (tmp != null && tmp2 != null) {
            setup.inventoryRssiFilter.min = Integer.parseInt(tmp);
            setup.inventoryRssiFilter.max = Integer.parseInt(tmp2);
            setupFlags |= 0x20000;
        }
        tmp = ini.get("NUR", "AutoTuneMode");
        tmp2 = ini.get("NUR", "AutoTuneThreshold");
        if (tmp != null && tmp2 != null) {
            setup.autotune.mode = Integer.parseInt(tmp);
            setup.autotune.thresholddBm = Integer.parseInt(tmp2);
            setupFlags |= 0x4000000;
        }
        tmp = ini.get("NUR", "AntennaPower");
        if (tmp != null) {
            arr = tmp.split(",");
            if (arr.length == 4) {
                setup.antPower[0] = Integer.parseInt(arr[0]);
                setup.antPower[1] = Integer.parseInt(arr[1]);
                setup.antPower[2] = Integer.parseInt(arr[2]);
                setup.antPower[3] = Integer.parseInt(arr[3]);
                setupFlags |= 0x800000;
            }
        }
        tmp = ini.get("NUR", "AntennaPowerEx");
        if (tmp != null) {
            arr = tmp.split(",");
            if (arr.length == 32) {
                for (int n = 0; n < 32; ++n) {
                    setup.antPowerEx[n] = Integer.parseInt(arr[n]);
                }
                setupFlags |= 0x8000000;
            }
        }
        tmp = ini.get("NUR", "PowerOffset");
        if (tmp != null) {
            arr = tmp.split(",");
            if (arr.length == 4) {
                setup.powerOffset = Integer.parseInt(arr[0]);
            }
            else {
                setup.powerOffset = Integer.parseInt(tmp);
            }
            setupFlags |= 0x1000000;
        }
        this.setModuleSetup(setup, setupFlags);
    }
    
    public NurSetup getModuleSetup(final int flags) throws Exception {
        if (flags == 0 || (this.mCurrentSetup.flags & flags) != flags) {
            synchronized (this) {
                this.mSentSetupFlags = flags;
                final NurSetup s = ((NurCmdLoadSetup)this.exchangeCommand(new NurCmdLoadSetup(flags))).getResponse();
                this.updateInternalSetup(s, s.flags);
            }
        }
        return this.mCurrentSetup;
    }
    
    private void updateInternalSetup(final NurSetup s, final int flags) {
        if (this.mCurrentSetup == null) {
            this.mCurrentSetup = new NurSetup();
        }
        if ((flags & 0x1) != 0x0) {
            this.mCurrentSetup.linkFreq = s.linkFreq;
        }
        if ((flags & 0x2) != 0x0) {
            this.mCurrentSetup.rxDecoding = s.rxDecoding;
        }
        if ((flags & 0x4) != 0x0) {
            this.mCurrentSetup.txLevel = s.txLevel;
        }
        if ((flags & 0x8) != 0x0) {
            this.mCurrentSetup.txModulation = s.txModulation;
        }
        if ((flags & 0x10) != 0x0) {
            this.mCurrentSetup.regionId = s.regionId;
        }
        if ((flags & 0x20) != 0x0) {
            this.mCurrentSetup.inventoryQ = s.inventoryQ;
        }
        if ((flags & 0x40) != 0x0) {
            this.mCurrentSetup.inventorySession = s.inventorySession;
        }
        if ((flags & 0x80) != 0x0) {
            this.mCurrentSetup.inventoryRounds = s.inventoryRounds;
        }
        if ((flags & 0x100) != 0x0) {
            this.mCurrentSetup.antennaMask = s.antennaMask;
        }
        if ((flags & 0x200) != 0x0) {
            this.mCurrentSetup.scanSingleTriggerTimeout = s.scanSingleTriggerTimeout;
        }
        if ((flags & 0x400) != 0x0) {
            this.mCurrentSetup.inventoryTriggerTimeout = s.inventoryTriggerTimeout;
        }
        if ((flags & 0x800) != 0x0) {
            this.mCurrentSetup.selectedAntenna = s.selectedAntenna;
        }
        if ((flags & 0x1000) != 0x0) {
            this.mCurrentSetup.opFlags = s.opFlags;
        }
        if ((flags & 0x2000) != 0x0) {
            this.mCurrentSetup.inventoryTarget = s.inventoryTarget;
        }
        if ((flags & 0x4000) != 0x0) {
            this.mCurrentSetup.inventoryEpcLength = s.inventoryEpcLength;
        }
        if ((flags & 0x8000) != 0x0) {
            this.mCurrentSetup.readRssiFilter.min = s.readRssiFilter.min;
            this.mCurrentSetup.readRssiFilter.max = s.readRssiFilter.max;
        }
        if ((flags & 0x10000) != 0x0) {
            this.mCurrentSetup.writeRssiFilter.min = s.writeRssiFilter.min;
            this.mCurrentSetup.writeRssiFilter.max = s.writeRssiFilter.max;
        }
        if ((flags & 0x20000) != 0x0) {
            this.mCurrentSetup.inventoryRssiFilter.min = s.inventoryRssiFilter.min;
            this.mCurrentSetup.inventoryRssiFilter.max = s.inventoryRssiFilter.max;
        }
        if ((flags & 0x40000) != 0x0) {
            this.mCurrentSetup.readTimeout = s.readTimeout;
        }
        if ((flags & 0x80000) != 0x0) {
            this.mCurrentSetup.writeTimeout = s.writeTimeout;
        }
        if ((flags & 0x100000) != 0x0) {
            this.mCurrentSetup.lockTimeout = s.lockTimeout;
        }
        if ((flags & 0x200000) != 0x0) {
            this.mCurrentSetup.killTimeout = s.killTimeout;
        }
        if ((flags & 0x400000) != 0x0) {
            this.mCurrentSetup.periodSetup = s.periodSetup;
        }
        if ((flags & 0x800000) != 0x0) {
            for (int i = 0; i < this.mCurrentSetup.antPower.length; ++i) {
                this.mCurrentSetup.antPower[i] = s.antPower[i];
            }
        }
        if ((flags & 0x1000000) != 0x0) {
            this.mCurrentSetup.powerOffset = s.powerOffset;
        }
        if ((flags & 0x2000000) != 0x0) {
            this.mCurrentSetup.antennaMaskEx = s.antennaMaskEx;
        }
        if ((flags & 0x4000000) != 0x0) {
            this.mCurrentSetup.autotune.mode = s.autotune.mode;
            this.mCurrentSetup.autotune.thresholddBm = s.autotune.thresholddBm;
        }
        if ((flags & 0x8000000) != 0x0) {
            for (int i = 0; i < this.mCurrentSetup.antPowerEx.length; ++i) {
                this.mCurrentSetup.antPowerEx[i] = s.antPowerEx[i];
            }
        }
        if ((flags & 0x10000000) != 0x0) {
            this.mCurrentSetup.rxSensitivity = s.rxSensitivity;
        }
        if ((flags & 0x20000000) != 0x0) {
            this.mCurrentSetup.rfProfile = s.rfProfile;
        }
        final NurSetup mCurrentSetup = this.mCurrentSetup;
        mCurrentSetup.flags |= flags;
        this.Hook();
    }
    
    public NurSetup setModuleSetup(final NurSetup setup, final int flags) throws Exception {
        final NurSetup s = ((NurCmdLoadSetup)this.exchangeCommand(new NurCmdLoadSetup(setup, flags))).getResponse();
        synchronized (this) {
            this.mChangedSetupFlags |= flags;
            this.updateInternalSetup(s, s.flags);
        }
        return this.mCurrentSetup;
    }
    
    private void buildDefaultAntennaMap() {
        this.mAntennaMap = new AntennaMapping[32];
        for (int i = 0; i < this.mAntennaMap.length; ++i) {
            this.mAntennaMap[i] = new AntennaMapping();
            this.mAntennaMap[i].antennaId = i;
            this.mAntennaMap[i].name = "Default " + i;
        }
        this.mAntennaMapPresent = true;
    }
    
    public AntennaMapping[] getAntennaMapping() throws Exception {
        if (!this.mAntennaMapPresent) {
            if (!this.isConnected()) {
                throw new NurApiException("Get antenna mapping: not connected.");
            }
            try {
                this.mAntennaMap = ((NurCmdAntennaEx)this.exchangeCommand(new NurCmdAntennaEx())).getResponse();
                if (this.mAntennaMap != null && this.mAntennaMap.length > 0) {
                    this.mAntennaMapPresent = true;
                }
            }
            catch (Exception ex) {
                this.buildDefaultAntennaMap();
            }
        }
        return this.mAntennaMap;
    }
    
    public String antennaIdToPhysicalAntenna(final int antennaId) throws Exception {
        if (!this.mAntennaMapPresent) {
            if (!this.isConnected()) {
                throw new NurApiException("Antenna mapping: not connected.");
            }
            try {
                this.getAntennaMapping();
                for (int i = 0; i < this.mAntennaMap.length; ++i) {
                    if (this.mAntennaMap[i].antennaId == antennaId) {
                        return this.mAntennaMap[i].name;
                    }
                }
            }
            catch (Exception ex) {}
        }
        else {
            for (int i = 0; i < this.mAntennaMap.length; ++i) {
                if (this.mAntennaMap[i].antennaId == antennaId) {
                    return this.mAntennaMap[i].name;
                }
            }
        }
        return "Antenna " + antennaId + " (?)";
    }
    
    public int physicalAntennaToNurAntennaId(final String physicalAntenna) {
        if (!this.mAntennaMapPresent) {
            try {
                this.getAntennaMapping();
            }
            catch (Exception ex) {
                return -1;
            }
        }
        for (int i = 0; i < this.mAntennaMap.length; ++i) {
            if (this.mAntennaMap[i].name.compareToIgnoreCase(physicalAntenna) == 0) {
                return this.mAntennaMap[i].antennaId;
            }
        }
        return -1;
    }
    
    private int physicalNameToAntennaMask(final AntennaMapping[] mapArr, final String physicalName) {
        int mask = 0;
        boolean physicalHasDot = false;
        if (physicalName.compareToIgnoreCase("all") != 0) {
            physicalHasDot = physicalName.contains(".");
            for (int i = 0; i < mapArr.length; ++i) {
                String tmpStr = mapArr[i].name;
                final boolean nameHasDot = tmpStr.contains(".");
                if (nameHasDot && !physicalHasDot) {
                    tmpStr = mapArr[i].name.split(".")[0].trim();
                }
                if (physicalName.compareToIgnoreCase(tmpStr) == 0) {
                    mask |= 1 << mapArr[i].antennaId;
                }
            }
            return mask;
        }
        if (mapArr.length == 32) {
            return -1;
        }
        return (1 << this.mAntennaMap.length) - 1;
    }
    
    private int getAllMappingsMask(final String[] strArr, final AntennaMapping[] mapArr) {
        int tmpMask = 0;
        int checkMask = 0;
        if (strArr.length != 1 || strArr[0].compareToIgnoreCase("all") != 0) {
            for (int i = 0; i < strArr.length; ++i) {
                tmpMask = this.physicalNameToAntennaMask(this.mAntennaMap, strArr[i]);
                if (tmpMask == 0) {
                    return 0;
                }
                checkMask |= tmpMask;
            }
            return checkMask;
        }
        if (mapArr.length == 32) {
            return -1;
        }
        return (1 << this.mAntennaMap.length) - 1;
    }
    
    public boolean isPhysicalAntennaEnabled(final String physicalAntenna) throws Exception {
        final String[] strArr = physicalAntenna.split(",");
        this.getModuleSetup();
        if ((this.mCurrentSetup.flags & 0x2000000) == 0x0) {
            throw new NurApiException("Antenna operation not supported.");
        }
        for (int i = 0; i < strArr.length; ++i) {
            strArr[i] = strArr[i].trim();
        }
        final int checkMask = this.getAllMappingsMask(strArr, this.mAntennaMap);
        return (this.mCurrentSetup.antennaMaskEx & checkMask) == checkMask;
    }
    
    public boolean isAntennaEnabled(final int antennaId) throws Exception {
        int mask = 0;
        int maskEx = 0;
        int failCount = 0;
        if (antennaId < 0 || antennaId > 31) {
            throw new NurApiException("isAntennaEnabled: invalid ID");
        }
        if (!this.isConnected()) {
            throw new NurApiException("isAntennaEnabled: not connected");
        }
        final int chkMask = 1 << antennaId;
        try {
            mask = (this.getSetupAntennaMask() & 0xF);
        }
        catch (Exception e) {
            ++failCount;
        }
        try {
            maskEx = (this.getSetupAntennaMaskEx() & 0xF);
        }
        catch (Exception e) {
            ++failCount;
        }
        if (failCount == 2) {
            throw new NurApiException("isAntennaEnabled: cannot get antenna mask");
        }
        return (maskEx & chkMask) != 0x0 || (mask & chkMask) != 0x0;
    }
    
    public NurRespRegionInfo getRegionInfo() throws Exception {
        return ((NurCmdRegionInfo)this.exchangeCommand(new NurCmdRegionInfo())).getResponse();
    }
    
    public NurRespRegionInfo getRegionInfo(final int regionId) throws Exception {
        return ((NurCmdRegionInfo)this.exchangeCommand(new NurCmdRegionInfo(regionId))).getResponse();
    }
    
    public NurCustomHopTable getCustomHopTable() throws Exception {
        return ((NurCmdCustomHop)this.exchangeCommand(new NurCmdCustomHop())).getResponse();
    }
    
    public NurCustomHopTableEx getCustomHopTableEx() throws Exception {
        return ((NurCmdCustomHopEx)this.exchangeCommand(new NurCmdCustomHopEx())).getResponse();
    }
    
    public void setCustomHopTable(final int[] freqs, final int nChan, final int chTime, final int pauseTime, final int lf, final int Tari) throws Exception {
        this.exchangeCommand(new NurCmdCustomHop(freqs, nChan, chTime, pauseTime, lf, Tari));
    }
    
    public void setCustomHopTableEx(final int[] freqs, final int nChan, final int chTime, final int pauseTime, final int lf, final int Tari, final int lbtThresh, final int maxTxLevel) throws Exception {
        this.exchangeCommand(new NurCmdCustomHopEx(freqs, nChan, chTime, pauseTime, lf, Tari, lbtThresh, maxTxLevel));
    }
    
    public void setCustomHopTable(final NurCustomHopTable cht) throws Exception {
        this.exchangeCommand(new NurCmdCustomHop(cht.freqs, cht.nChan, cht.chTime, cht.pauseTime, cht.lf, cht.Tari));
    }
    
    public void setCustomHopTableEx(final NurCustomHopTableEx hte) throws Exception {
        this.exchangeCommand(new NurCmdCustomHopEx(hte.freqs, hte.nChan, hte.chTime, hte.pauseTime, hte.lf, hte.Tari, hte.lbtThresh, hte.TxLevel));
    }
    
    public void setConstantChannelIndex(final int channelIdx) throws Exception {
        final byte[] arr = { 0 };
        if (channelIdx == -1) {
            arr[0] = -1;
        }
        else {
            arr[0] = (byte)(channelIdx & 0xFF);
        }
        this.customCmd(106, arr);
    }
    
    public NurRespReadData scanSingleTag() throws Exception {
        return ((NurCmdScanSingle)this.exchangeCommand(new NurCmdScanSingle(500))).getResponse();
    }
    
    public NurRespReadData scanSingleTag(final int timeout) throws Exception {
        return ((NurCmdScanSingle)this.exchangeCommand(new NurCmdScanSingle(timeout))).getResponse();
    }
    
    public NurRespInventory inventory() throws Exception {
        return ((NurCmdInventory)this.exchangeCommand(new NurCmdInventory())).getResponse();
    }
    
    public NurRespInventory inventory(final int rounds, final int Q, final int session) throws Exception {
        return ((NurCmdInventory)this.exchangeCommand(new NurCmdInventory(Q, session, rounds))).getResponse();
    }
    
    public NurRespInventory inventorySelect(final int rounds, final int Q, final int session, final boolean invertSelect, final int sBank, final int sAddress, final int sMaskBitLength, final byte[] sMask) throws Exception {
        return ((NurCmdInventorySelect)this.exchangeCommand(new NurCmdInventorySelect(rounds, Q, session, invertSelect, sBank, sAddress, sMaskBitLength, sMask))).getResponse();
    }
    
    public NurRespInventory inventorySelectByEpc(final int rounds, final int Q, final int session, final boolean invertSelect, final byte[] epcBuffer, final int epcBufferLen) throws Exception {
        return this.inventorySelect(rounds, Q, session, invertSelect, 1, 32, epcBufferLen * 8, epcBuffer);
    }
    
    public byte[] readTag(final int rdBank, final int rdAddress, final int rdByteCount) throws Exception {
        return ((NurCmdReadTag)this.exchangeCommand(new NurCmdReadTag(0, false, 0, 0, 0, null, rdBank, rdAddress, rdByteCount))).getResponse();
    }
    
    public byte[] readTagByEpc(final byte[] epcBuf, final int epcBufferLength, final int rdBank, final int rdAddress, final int rdByteCount) throws Exception {
        return ((NurCmdReadTag)this.exchangeCommand(new NurCmdReadTag(0, false, 1, 32, epcBufferLength * 8, epcBuf, rdBank, rdAddress, rdByteCount))).getResponse();
    }
    
    public byte[] readTagByEpc(final int passwd, final byte[] epcBuf, final int epcBufferLength, final int rdBank, final int rdAddress, final int rdByteCount) throws Exception {
        return ((NurCmdReadTag)this.exchangeCommand(new NurCmdReadTag(passwd, true, 1, 32, epcBufferLength * 8, epcBuf, rdBank, rdAddress, rdByteCount))).getResponse();
    }
    
    public byte[] readTag(final int sBank, final int sAddress, final int sMaskBitLength, final byte[] sMask, final int rdBank, final int rdAddress, final int rdByteCount) throws Exception {
        return ((NurCmdReadTag)this.exchangeCommand(new NurCmdReadTag(0, false, sBank, sAddress, sMaskBitLength, sMask, rdBank, rdAddress, rdByteCount))).getResponse();
    }
    
    public byte[] readTag(final int passwd, final int rdBank, final int rdAddress, final int rdByteCount) throws Exception {
        return ((NurCmdReadTag)this.exchangeCommand(new NurCmdReadTag(passwd, true, 0, 0, 0, null, rdBank, rdAddress, rdByteCount))).getResponse();
    }
    
    public byte[] readTag(final int passwd, final int sBank, final int sAddress, final int sMaskBitLength, final byte[] sMask, final int rdBank, final int rdAddress, final int rdByteCount) throws Exception {
        return ((NurCmdReadTag)this.exchangeCommand(new NurCmdReadTag(passwd, true, sBank, sAddress, sMaskBitLength, sMask, rdBank, rdAddress, rdByteCount))).getResponse();
    }
    
    public byte[] readCustomTag(final int passwd, final int rdCmd, final int cmdBits, final int rdBank, final int bankBits, final int sBank, final int sAddress, final int sMaskBitLength, final byte[] sMask, final int rdAddress, final int rdByteCount) throws Exception {
        return ((NurCmdCustomReadTag)this.exchangeCommand(new NurCmdCustomReadTag(rdCmd, cmdBits, rdBank, bankBits, passwd, true, sBank, sAddress, sMaskBitLength, sMask, rdAddress, rdByteCount))).getResponse();
    }
    
    public byte[] readCustomTag(final int rdCmd, final int cmdBits, final int rdBank, final int bankBits, final int sBank, final int sAddress, final int sMaskBitLength, final byte[] sMask, final int rdAddress, final int rdByteCount) throws Exception {
        return ((NurCmdCustomReadTag)this.exchangeCommand(new NurCmdCustomReadTag(rdCmd, cmdBits, rdBank, bankBits, 0, false, sBank, sAddress, sMaskBitLength, sMask, rdAddress, rdByteCount))).getResponse();
    }
    
    public byte[] readCustomTag(final int passwd, final int rdCmd, final int cmdBits, final int rdBank, final int bankBits, final int rdAddress, final int rdByteCount) throws Exception {
        return this.readCustomTag(passwd, rdCmd, cmdBits, rdBank, bankBits, 0, 0, 0, null, rdAddress, rdByteCount);
    }
    
    public byte[] readCustomTag(final int rdCmd, final int cmdBits, final int rdBank, final int bankBits, final int rdAddress, final int rdByteCount) throws Exception {
        return this.readCustomTag(rdCmd, cmdBits, rdBank, bankBits, 0, 0, 0, null, rdAddress, rdByteCount);
    }
    
    public byte[] readCustomTagByEpc(final int passwd, final int rdCmd, final int cmdBits, final int rdBank, final int bankBits, final byte[] epcBuf, final int epcBufferLength, final int rdAddress, final int rdByteCount) throws Exception {
        return this.readCustomTag(passwd, rdCmd, cmdBits, rdBank, bankBits, 1, 32, epcBufferLength * 8, epcBuf, rdAddress, rdByteCount);
    }
    
    public byte[] readCustomTagByEpc(final int rdCmd, final int cmdBits, final int rdBank, final int bankBits, final byte[] epcBuf, final int epcBufferLength, final int rdAddress, final int rdByteCount) throws Exception {
        return this.readCustomTag(rdCmd, cmdBits, rdBank, bankBits, 1, 32, epcBufferLength * 8, epcBuf, rdAddress, rdByteCount);
    }
    
    public byte[] customCmd(final int cmd, final byte[] dataIn) throws Exception {
        return ((NurCmdCustom)this.exchangeCommand(new NurCmdCustom(cmd, dataIn))).getResponse();
    }
    
    public byte[] customCmd(final int cmd, final byte[] dataIn, final int timeout) throws Exception {
        final NurCmdCustom ccmd = new NurCmdCustom(cmd, dataIn);
        ccmd.setTimeout(timeout);
        return ((NurCmdCustom)this.exchangeCommand(ccmd)).getResponse();
    }
    
    public int getAccessPassword(final int passwd, final int sBank, final int sAddress, final int sMaskBitLength, final byte[] sMask) throws Exception {
        byte[] arr = new byte[4];
        arr = this.readTag(passwd, sBank, sAddress, sMaskBitLength, sMask, 0, 2, 4);
        return this.SwapEndian(NurPacket.BytesToDword(arr, 0));
    }
    
    public int getAccessPassword(final int sBank, final int sAddress, final int sMaskBitLength, final byte[] sMask) throws Exception {
        byte[] arr = new byte[4];
        arr = this.readTag(sBank, sAddress, sMaskBitLength, sMask, 0, 2, 4);
        return this.SwapEndian(NurPacket.BytesToDword(arr, 0));
    }
    
    public int getAccessPasswordByEpc(final int passwd, final byte[] epcBuffer, final int epcBufferLength) throws Exception {
        return this.getAccessPassword(passwd, 1, 32, epcBufferLength * 8, epcBuffer);
    }
    
    public int getAccessPasswordByEpc(final byte[] epcBuffer, final int epcBufferLength) throws Exception {
        return this.getAccessPassword(1, 32, epcBufferLength * 8, epcBuffer);
    }
    
    public void setAccessPassword(final int sBank, final int sAddress, final int sMaskBitLength, final byte[] sMask, final int newPassword) throws Exception {
        final byte[] pw = new byte[4];
        NurPacket.PacketDword(pw, 0, this.SwapEndian(newPassword));
        this.writeTag(sBank, sAddress, sMaskBitLength, sMask, 0, 2, 4, pw);
    }
    
    public void setAccessPassword(final int password, final int sBank, final int sAddress, final int sMaskBitLength, final byte[] sMask, final int newPassword) throws Exception {
        final byte[] pw = new byte[4];
        NurPacket.PacketDword(pw, 0, this.SwapEndian(newPassword));
        this.writeTag(password, sBank, sAddress, sMaskBitLength, sMask, 0, 2, 4, pw);
    }
    
    public void setAccessPasswordByEpc(final int epcBufLen, final byte[] epcBuf, final int newPassword) throws Exception {
        this.setAccessPassword(1, 32, epcBufLen, epcBuf, newPassword);
    }
    
    public void setAccessPasswordByEpc(final int password, final int epcBufLen, final byte[] epcBuf, final int newPassword) throws Exception {
        this.setAccessPassword(password, 1, 32, epcBufLen, epcBuf, newPassword);
    }
    
    public int getKillPassword(final int sBank, final int sAddress, final int sMaskBitLength, final byte[] sMask) throws Exception {
        byte[] arr = new byte[4];
        arr = this.readTag(sBank, sAddress, sMaskBitLength, sMask, 0, 0, 4);
        return this.SwapEndian(NurPacket.BytesToDword(arr, 0));
    }
    
    public int getKillPassword(final int passwd, final int sBank, final int sAddress, final int sMaskBitLength, final byte[] sMask) throws Exception {
        byte[] arr = new byte[4];
        arr = this.readTag(passwd, sBank, sAddress, sMaskBitLength, sMask, 0, 0, 4);
        return this.SwapEndian(NurPacket.BytesToDword(arr, 0));
    }
    
    public int getKillPasswordByEpc(final byte[] epcBuffer, final int epcBufferLen) throws Exception {
        return this.getKillPassword(1, 32, epcBufferLen * 8, epcBuffer);
    }
    
    public int getKillPasswordByEpc(final int passwd, final byte[] epcBuffer, final int epcBufferLen) throws Exception {
        return this.getKillPassword(passwd, 1, 32, epcBufferLen * 8, epcBuffer);
    }
    
    public void setKillPassword(final int password, final int sBank, final int sAddress, final int sMaskBitLength, final byte[] sMask, final int newPassword) throws Exception {
        final byte[] pw = new byte[4];
        NurPacket.PacketDword(pw, 0, this.SwapEndian(newPassword));
        this.writeTag(password, sBank, sAddress, sMaskBitLength, sMask, 0, 0, 4, pw);
    }
    
    public void setKillPassword(final int sBank, final int sAddress, final int sMaskBitLength, final byte[] sMask, final int newPassword) throws Exception {
        final byte[] pw = new byte[4];
        NurPacket.PacketDword(pw, 0, this.SwapEndian(newPassword));
        this.writeTag(sBank, sAddress, sMaskBitLength, sMask, 0, 0, 4, pw);
    }
    
    public void setKillPasswordByEpc(final int password, final byte[] epcBuffer, final int epcBufferLen, final int newPassword) throws Exception {
        this.setKillPassword(password, 1, 32, epcBufferLen * 8, epcBuffer, newPassword);
    }
    
    public void setKillPasswordByEpc(final byte[] epcBuffer, final int epcBufferLen, final int newPassword) throws Exception {
        this.setKillPassword(1, 32, epcBufferLen, epcBuffer, newPassword);
    }
    
    public void writeTag(final int wrBank, final int wrAddress, final int wrByteCount, final byte[] wrData) throws Exception {
        this.exchangeCommand(new NurCmdWriteTag(0, false, 0, 0, 0, null, wrBank, wrAddress, wrByteCount, wrData));
    }
    
    public void writeTag(final int sBank, final int sAddress, final int sMaskBitLength, final byte[] sMask, final int wrBank, final int wrAddress, final int wrByteCount, final byte[] wrData) throws Exception {
        this.exchangeCommand(new NurCmdWriteTag(0, false, sBank, sAddress, sMaskBitLength, sMask, wrBank, wrAddress, wrByteCount, wrData));
    }
    
    public void writeTag(final int passwd, final int wrBank, final int wrAddress, final int wrByteCount, final byte[] wrData) throws Exception {
        this.exchangeCommand(new NurCmdWriteTag(passwd, true, 0, 0, 0, null, wrBank, wrAddress, wrByteCount, wrData));
    }
    
    public void writeTag(final int passwd, final int sBank, final int sAddress, final int sMaskBitLength, final byte[] sMask, final int wrBank, final int wrAddress, final int wrByteCount, final byte[] wrData) throws Exception {
        this.exchangeCommand(new NurCmdWriteTag(passwd, true, sBank, sAddress, sMaskBitLength, sMask, wrBank, wrAddress, wrByteCount, wrData));
    }
    
    public void writeTagByEpc(final int passwd, final byte[] epcBuffer, final int epcBufferLength, final int wrBank, final int wrAddress, final int wrByteCount, final byte[] wrData) throws Exception {
        this.writeTag(passwd, 1, 32, epcBufferLength * 8, epcBuffer, wrBank, wrAddress, wrByteCount, wrData);
    }
    
    public void writeTagByEpc(final int passwd, final byte[] epcBuffer, final int wrBank, final int wrAddress, final int wrByteCount, final byte[] wrData) throws Exception {
        this.writeTagByEpc(passwd, epcBuffer, epcBuffer.length, wrBank, wrAddress, wrByteCount, wrData);
    }
    
    public void writeTagByEpc(final byte[] epcBuffer, final int epcBufferLength, final int wrBank, final int wrAddress, final int wrByteCount, final byte[] wrData) throws Exception {
        this.writeTag(1, 32, epcBufferLength * 8, epcBuffer, wrBank, wrAddress, wrByteCount, wrData);
    }
    
    public void writeTagByEpc(final byte[] epcBuffer, final int wrBank, final int wrAddress, final int wrByteCount, final byte[] wrData) throws Exception {
        this.writeTagByEpc(0, epcBuffer, epcBuffer.length, wrBank, wrAddress, wrByteCount, wrData);
    }
    
    public void writeEpcByEpc(final int passwd, final byte[] epcBuffer, final int epcBufferLength, final int newEpcBufferLength, final byte[] newEpcBuffer) throws Exception {
        final byte[] wrBuf = new byte[64];
        final int paddedEpcBufferLen = (newEpcBufferLength * 8 + 15) / 16 * 2;
        Arrays.fill(wrBuf, (byte)0);
        final int pc = paddedEpcBufferLen / 2 << 11;
        wrBuf[0] = (byte)(pc >> 8);
        wrBuf[1] = (byte)pc;
        System.arraycopy(newEpcBuffer, 0, wrBuf, 2, newEpcBufferLength);
        this.writeTag(passwd, 1, 32, epcBufferLength * 8, epcBuffer, 1, 1, paddedEpcBufferLen + 2, wrBuf);
    }
    
    public void writeEpcByEpc(final byte[] epcBuffer, final int epcBufferLength, final int newEpcBufferLength, final byte[] newEpcBuffer) throws Exception {
        final byte[] wrBuf = new byte[64];
        final int paddedEpcBufferLen = (newEpcBufferLength * 8 + 15) / 16 * 2;
        Arrays.fill(wrBuf, (byte)0);
        final int pc = paddedEpcBufferLen / 2 << 11;
        wrBuf[0] = (byte)(pc >> 8);
        wrBuf[1] = (byte)pc;
        System.arraycopy(newEpcBuffer, 0, wrBuf, 2, newEpcBufferLength);
        this.writeTag(1, 32, epcBufferLength * 8, epcBuffer, 1, 1, paddedEpcBufferLen + 2, wrBuf);
    }
    
    public void blockWriteTag(final int wrBank, final int wrAddress, final int wrByteCount, final byte[] wrData) throws Exception {
        this.exchangeCommand(new NurCmdBlockWrite(0, false, 0, 0, 0, null, wrBank, wrAddress, wrByteCount, wrData));
    }
    
    public void blockWriteTag(final int sBank, final int sAddress, final int sMaskBitLength, final byte[] sMask, final int wrBank, final int wrAddress, final int wrByteCount, final byte[] wrData) throws Exception {
        this.exchangeCommand(new NurCmdBlockWrite(0, false, sBank, sAddress, sMaskBitLength, sMask, wrBank, wrAddress, wrByteCount, wrData));
    }
    
    public void blockWriteTag(final int passwd, final int wrBank, final int wrAddress, final int wrByteCount, final byte[] wrData) throws Exception {
        this.exchangeCommand(new NurCmdBlockWrite(passwd, true, 0, 0, 0, null, wrBank, wrAddress, wrByteCount, wrData));
    }
    
    public void blockWriteTag(final int passwd, final int sBank, final int sAddress, final int sMaskBitLength, final byte[] sMask, final int wrBank, final int wrAddress, final int wrByteCount, final byte[] wrData) throws Exception {
        this.exchangeCommand(new NurCmdBlockWrite(passwd, true, sBank, sAddress, sMaskBitLength, sMask, wrBank, wrAddress, wrByteCount, wrData));
    }
    
    public void writeCustomTag(final int password, final int wrCmd, final int cmdBits, final int wrBank, final int bankBits, final int sBank, final int sAddress, final int sMaskBitLength, final byte[] sMask, final int wrAddress, final int wrByteCount, final byte[] wrBuffer) throws NurApiException, Exception {
        this.exchangeCommand(new NurCmdCustomWriteTag(wrCmd, cmdBits, wrBank, bankBits, password, true, sBank, sAddress, sMaskBitLength, sMask, wrAddress, wrByteCount, wrBuffer));
    }
    
    public void writeCustomTag(final int wrCmd, final int cmdBits, final int wrBank, final int bankBits, final int sBank, final int sAddress, final int sMaskBitLength, final byte[] sMask, final int wrAddress, final int wrByteCount, final byte[] wrBuffer) throws NurApiException, Exception {
        this.exchangeCommand(new NurCmdCustomWriteTag(wrCmd, cmdBits, wrBank, bankBits, 0, false, sBank, sAddress, sMaskBitLength, sMask, wrAddress, wrByteCount, wrBuffer));
    }
    
    public void writeCustomTag(final int password, final int wrCmd, final int cmdBits, final int wrBank, final int bankBits, final int wrAddress, final int wrByteCount, final byte[] wrBuffer) throws NurApiException, Exception {
        this.writeCustomTag(password, wrCmd, cmdBits, wrBank, bankBits, 0, 0, 0, null, wrAddress, wrByteCount, wrBuffer);
    }
    
    public void writeCustomTag(final int wrCmd, final int cmdBits, final int wrBank, final int bankBits, final int wrAddress, final int wrByteCount, final byte[] wrBuffer) throws NurApiException, Exception {
        this.writeCustomTag(wrCmd, cmdBits, wrBank, bankBits, 0, 0, 0, null, wrAddress, wrByteCount, wrBuffer);
    }
    
    public void writeCustomTagByEpc(final int password, final int wrCmd, final int cmdBits, final int wrBank, final int bankBits, final byte[] epcBuffer, final int epcBufferLength, final int wrAddress, final int wrByteCount, final byte[] wrBuffer) throws NurApiException, Exception {
        this.writeCustomTag(password, wrCmd, cmdBits, wrBank, bankBits, 1, 32, epcBufferLength * 8, epcBuffer, wrAddress, wrByteCount, wrBuffer);
    }
    
    public void writeCustomTagByEpc(final int wrCmd, final int cmdBits, final int wrBank, final int bankBits, final byte[] epcBuffer, final int epcBufferLength, final int wrAddress, final int wrByteCount, final byte[] wrBuffer) throws NurApiException, Exception {
        this.writeCustomTag(wrCmd, cmdBits, wrBank, bankBits, 1, 32, epcBufferLength * 8, epcBuffer, wrAddress, wrByteCount, wrBuffer);
    }
    
    public void blockWriteCustomTag(final int password, final int wrCmd, final int cmdBits, final int wrBank, final int bankBits, final int sBank, final int sAddress, final int sMaskBitLength, final byte[] sMask, final int wrAddress, final int wrByteCount, final byte[] wrBuffer) throws NurApiException, Exception {
        this.exchangeCommand(new NurCmdCustomBlockWriteTag(wrCmd, cmdBits, wrBank, bankBits, password, true, sBank, sAddress, sMaskBitLength, sMask, wrAddress, wrByteCount, wrBuffer));
    }
    
    public void blockWriteCustomTag(final int wrCmd, final int cmdBits, final int wrBank, final int bankBits, final int sBank, final int sAddress, final int sMaskBitLength, final byte[] sMask, final int wrAddress, final int wrByteCount, final byte[] wrBuffer) throws NurApiException, Exception {
        this.exchangeCommand(new NurCmdCustomBlockWriteTag(wrCmd, cmdBits, wrBank, bankBits, 0, false, sBank, sAddress, sMaskBitLength, sMask, wrAddress, wrByteCount, wrBuffer));
    }
    
    public void blockWriteCustomTag(final int password, final int wrCmd, final int cmdBits, final int wrBank, final int bankBits, final int wrAddress, final int wrByteCount, final byte[] wrBuffer) throws NurApiException, Exception {
        this.blockWriteCustomTag(password, wrCmd, cmdBits, wrBank, bankBits, 0, 0, 0, null, wrAddress, wrByteCount, wrBuffer);
    }
    
    public void blockWriteCustomTag(final int wrCmd, final int cmdBits, final int wrBank, final int bankBits, final int wrAddress, final int wrByteCount, final byte[] wrBuffer) throws NurApiException, Exception {
        this.blockWriteCustomTag(wrCmd, cmdBits, wrBank, bankBits, 0, 0, 0, null, wrAddress, wrByteCount, wrBuffer);
    }
    
    public void blockWriteCustomTagByEpc(final int password, final int wrCmd, final int cmdBits, final int wrBank, final int bankBits, final byte[] epcBuffer, final int epcBufferLength, final int wrAddress, final int wrByteCount, final byte[] wrBuffer) throws NurApiException, Exception {
        this.blockWriteCustomTag(password, wrCmd, cmdBits, wrBank, bankBits, 1, 32, epcBufferLength * 8, epcBuffer, wrAddress, wrByteCount, wrBuffer);
    }
    
    public void blockWriteCustomTagByEpc(final int wrCmd, final int cmdBits, final int wrBank, final int bankBits, final byte[] epcBuffer, final int epcBufferLength, final int wrAddress, final int wrByteCount, final byte[] wrBuffer) throws NurApiException, Exception {
        this.blockWriteCustomTag(wrCmd, cmdBits, wrBank, bankBits, 1, 32, epcBufferLength * 8, epcBuffer, wrAddress, wrByteCount, wrBuffer);
    }
    
    public void blockEraseTag(final int passwd, final int erBank, final int erAddress, final int erWords) throws NurApiException, Exception {
        this.exchangeCommand(new NurCmdBlockErase(passwd, true, 0, 0, 0, null, erBank, erAddress, erWords));
    }
    
    public void blockEraseTag(final int erBank, final int erAddress, final int erWords) throws NurApiException, Exception {
        this.exchangeCommand(new NurCmdBlockErase(0, false, 0, 0, 0, null, erBank, erAddress, erWords));
    }
    
    public void blockEraseTag(final int passwd, final int sBank, final int sAddress, final int sMaskBitLength, final byte[] sMask, final int erBank, final int erAddress, final int erWords) throws NurApiException, Exception {
        this.exchangeCommand(new NurCmdBlockErase(passwd, true, sBank, sAddress, sMaskBitLength, sMask, erBank, erAddress, erWords));
    }
    
    public void blockEraseTag(final int sBank, final int sAddress, final int sMaskBitLength, final byte[] sMask, final int erBank, final int erAddress, final int erWords) throws NurApiException, Exception {
        this.exchangeCommand(new NurCmdBlockErase(0, false, sBank, sAddress, sMaskBitLength, sMask, erBank, erAddress, erWords));
    }
    
    public void blockEraseTagByEpc(final int passwd, final byte[] epcBuf, final int erBank, final int erAddress, final int erWords) throws NurApiException, Exception {
        this.exchangeCommand(new NurCmdBlockErase(passwd, true, 1, 32, epcBuf.length * 8, epcBuf, erBank, erAddress, erWords));
    }
    
    public void blockEraseTagByEpc(final byte[] epcBuf, final int erBank, final int erAddress, final int erWords) throws NurApiException, Exception {
        this.exchangeCommand(new NurCmdBlockErase(0, false, 1, 32, epcBuf.length * 8, epcBuf, erBank, erAddress, erWords));
    }
    
    public void setLock(final int passwd, final int sBank, final int sAddress, final int sMaskBitLength, final byte[] sMask, final int memoryMask, final int action) throws Exception {
        this.exchangeCommand(new NurCmdLock(true, passwd, sBank, sAddress, sMaskBitLength, sMask, memoryMask, action, false));
    }
    
    public void setLockRaw(final int passwd, final int sBank, final int sAddress, final int sMaskBitLength, final byte[] sMask, final int lockMask, final int lockAction) throws Exception {
        this.exchangeCommand(new NurCmdLock(true, passwd, sBank, sAddress, sMaskBitLength, sMask, lockMask, lockAction, true));
    }
    
    public void setLockByEpc(final int passwd, final byte[] epcBuffer, final int epcBufferLength, final int memoryMask, final int action) throws Exception {
        this.setLock(passwd, 1, 32, epcBufferLength * 8, epcBuffer, memoryMask, action);
    }
    
    public void openStateLock(final int sBank, final int sAddress, final int sMaskBitLength, final byte[] sMask, final int memoryMask, final int action) throws Exception {
        this.exchangeCommand(new NurCmdLock(false, 0, sBank, sAddress, sMaskBitLength, sMask, memoryMask, action, false));
    }
    
    public void openStateLockRaw(final int sBank, final int sAddress, final int sMaskBitLength, final byte[] sMask, final int lockMask, final int lockAction) throws Exception {
        this.exchangeCommand(new NurCmdLock(false, 0, sBank, sAddress, sMaskBitLength, sMask, lockMask, lockAction, true));
    }
    
    public void openStateLockByEpc(final byte[] epcBuffer, final int epcBufferLength, final int memoryMask, final int action) throws Exception {
        this.openStateLock(1, 32, epcBufferLength * 8, epcBuffer, memoryMask, action);
    }
    
    public NurRespPermalock setBlockPermaLock(final int passwd, final int plBank, final int firstBlock, final short[] lockWords) throws Exception {
        return ((NurCmdBlockPermaLock)this.exchangeCommand(new NurCmdBlockPermaLock(passwd, true, 0, 0, 0, null, plBank, firstBlock, lockWords))).getResponse();
    }
    
    public NurRespPermalock getBlockPermaLock(final int passwd, final int plBank, final int firstBlock, final int nBlocks) throws Exception {
        return ((NurCmdBlockPermaLock)this.exchangeCommand(new NurCmdBlockPermaLock(passwd, true, 0, 0, 0, null, plBank, firstBlock, nBlocks))).getResponse();
    }
    
    public NurRespPermalock setBlockPermaLock(final int plBank, final int firstBlock, final short[] lockWords) throws Exception {
        return ((NurCmdBlockPermaLock)this.exchangeCommand(new NurCmdBlockPermaLock(0, false, 0, 0, 0, null, plBank, firstBlock, lockWords))).getResponse();
    }
    
    public NurRespPermalock getBlockPermaLock(final int plBank, final int firstBlock, final int nBlocks) throws Exception {
        return ((NurCmdBlockPermaLock)this.exchangeCommand(new NurCmdBlockPermaLock(0, false, 0, 0, 0, null, plBank, firstBlock, nBlocks))).getResponse();
    }
    
    public NurRespPermalock setBlockPermaLock(final int passwd, final int sBank, final int sAddress, final int sMaskBitLength, final byte[] sMask, final int plBank, final int firstBlock, final short[] lockWords) throws Exception {
        return ((NurCmdBlockPermaLock)this.exchangeCommand(new NurCmdBlockPermaLock(passwd, true, sBank, sAddress, sMaskBitLength, sMask, plBank, firstBlock, lockWords))).getResponse();
    }
    
    public NurRespPermalock getBlockPermaLock(final int passwd, final int sBank, final int sAddress, final int sMaskBitLength, final byte[] sMask, final int plBank, final int firstBlock, final int nBlocks) throws Exception {
        return ((NurCmdBlockPermaLock)this.exchangeCommand(new NurCmdBlockPermaLock(passwd, true, sBank, sAddress, sMaskBitLength, sMask, plBank, firstBlock, nBlocks))).getResponse();
    }
    
    public NurRespPermalock setBlockPermaLock(final int sBank, final int sAddress, final int sMaskBitLength, final byte[] sMask, final int plBank, final int firstBlock, final short[] lockWords) throws Exception {
        return ((NurCmdBlockPermaLock)this.exchangeCommand(new NurCmdBlockPermaLock(0, false, sBank, sAddress, sMaskBitLength, sMask, plBank, firstBlock, lockWords))).getResponse();
    }
    
    public NurRespPermalock getBlockPermaLock(final int sBank, final int sAddress, final int sMaskBitLength, final byte[] sMask, final int plBank, final int firstBlock, final int nBlocks) throws Exception {
        return ((NurCmdBlockPermaLock)this.exchangeCommand(new NurCmdBlockPermaLock(0, false, sBank, sAddress, sMaskBitLength, sMask, plBank, firstBlock, nBlocks))).getResponse();
    }
    
    public NurRespPermalock setBlockPermaLockByEpc(final int passwd, final byte[] epcBuffer, final int plBank, final int firstBlock, final short[] lockWords) throws Exception {
        return ((NurCmdBlockPermaLock)this.exchangeCommand(new NurCmdBlockPermaLock(passwd, true, 1, 32, epcBuffer.length * 8, epcBuffer, plBank, firstBlock, lockWords))).getResponse();
    }
    
    public NurRespPermalock getBlockPermaLockByEpc(final int passwd, final byte[] epcBuffer, final int plBank, final int firstBlock, final int nBlocks) throws Exception {
        return ((NurCmdBlockPermaLock)this.exchangeCommand(new NurCmdBlockPermaLock(passwd, true, 1, 32, epcBuffer.length * 8, epcBuffer, plBank, firstBlock, nBlocks))).getResponse();
    }
    
    public NurRespPermalock setBlockPermaLockByEpc(final byte[] epcBuffer, final int plBank, final int firstBlock, final short[] lockWords) throws Exception {
        return ((NurCmdBlockPermaLock)this.exchangeCommand(new NurCmdBlockPermaLock(0, false, 1, 32, epcBuffer.length * 8, epcBuffer, plBank, firstBlock, lockWords))).getResponse();
    }
    
    public NurRespPermalock getBlockPermaLockByEpc(final byte[] epcBuffer, final int plBank, final int firstBlock, final int nBlocks) throws Exception {
        return ((NurCmdBlockPermaLock)this.exchangeCommand(new NurCmdBlockPermaLock(0, false, 1, 32, epcBuffer.length * 8, epcBuffer, plBank, firstBlock, nBlocks))).getResponse();
    }
    
    public void killTag(final int passwd, final int sBank, final int sAddress, final int sMaskBitLength, final byte[] sMask) throws Exception {
        this.exchangeCommand(new NurCmdKill(passwd, sBank, sAddress, sMaskBitLength, sMask));
    }
    
    public void killTagByEpc(final int passwd, final byte[] epcBuffer, final int epcBufferLength) throws Exception {
        this.killTag(passwd, 1, 32, epcBufferLength * 8, epcBuffer);
    }
    
    public NurRespReadData traceTag(final int sBank, final int sAddress, final int sMaskBitLength, final byte[] sMaskData, final int flags) throws Exception {
        final NurRespReadData readData = ((NurCmdTraceTag)this.exchangeCommand(new NurCmdTraceTag(flags, sBank, sAddress, sMaskBitLength, sMaskData))).getResponse();
        if ((flags & 0x2) != 0x0) {
            this.mTraceTagSb = new NurRWSingulationBlock(sBank, sAddress, sMaskBitLength, sMaskData);
            this.mTraceTagFlags = flags;
            this.mTraceRunning = true;
        }
        else if ((flags & 0x8) != 0x0) {
            this.mTraceTagSb = null;
            this.mTraceTagFlags = 0;
            this.mTraceRunning = false;
        }
        return readData;
    }
    
    public NurRespReadData traceTagByEpc(final byte[] epcBuffer, final int epcBufferLength, final int flags) throws Exception {
        return this.traceTag(1, 32, epcBufferLength * 8, epcBuffer, flags | 0x1);
    }
    
    public NurRespReadData traceTagByEpc(final byte[] epcBuffer) throws Exception {
        return this.traceTag(1, 32, epcBuffer.length * 8, epcBuffer, 3);
    }
    
    public void stopTraceTag() throws Exception {
        this.stopAllContinuousCommands();
    }
    
    public boolean isTraceTagRunning() {
        return this.mTraceRunning;
    }
    
    public void startInventoryStream() throws Exception {
        this.exchangeCommand(new NurCmdInventoryStream(false));
        this.mInventoryStreamRunning = true;
    }
    
    public void stopInventoryStream() throws Exception {
        this.exchangeCommand(new NurCmdInventoryStream(true));
        this.mInventoryStreamRunning = false;
    }
    
    public boolean isInventoryStreamRunning() {
        return this.mInventoryStreamRunning;
    }
    
    public void resetToTarget(final int session, final boolean targetIsA) throws Exception {
        this.exchangeCommand(new NurCmdResetTarget(session, targetIsA));
    }
    
    public NurRespInventory inventoryExtended(final NurInventoryExtended invEx, final NurInventoryExtendedFilter filter) throws Exception {
        return ((NurCmdInventoryEx)this.exchangeCommand(new NurCmdInventoryEx(invEx, filter, false))).getResponse();
    }
    
    public void startInventoryExtendedStream(final NurInventoryExtended invEx, final NurInventoryExtendedFilter filter) throws Exception {
        this.exchangeCommand(new NurCmdInventoryEx(invEx, filter, true));
        this.mInventoryExtended = invEx;
        final NurInventoryExtendedFilter[] filters = { filter };
        this.mInventoryExtendedFilters = filters;
        this.mInventoryExtendedStreamRunning = true;
    }
    
    public NurRespInventory inventoryExtended(final NurInventoryExtended invEx, final NurInventoryExtendedFilter[] filters, final int filterCount) throws Exception {
        return ((NurCmdInventoryEx)this.exchangeCommand(new NurCmdInventoryEx(invEx, filters, false))).getResponse();
    }
    
    public void startInventoryExtendedStream(final NurInventoryExtended invEx, final NurInventoryExtendedFilter[] filters) throws Exception {
        this.exchangeCommand(new NurCmdInventoryEx(invEx, filters, true));
        this.mInventoryExtended = invEx;
        this.mInventoryExtendedFilters = filters;
        this.mInventoryExtendedStreamRunning = true;
    }
    
    public void startInventoryExtendedStream() throws Exception {
        this.exchangeCommand(new NurCmdInventoryEx(true));
    }
    
    public NurRespInventory inventoryExtended() throws Exception {
        return ((NurCmdInventoryEx)this.exchangeCommand(new NurCmdInventoryEx(false))).getResponse();
    }
    
    public void stopInventoryExtendedStream() throws Exception {
        this.exchangeCommand(new NurCmdInventoryEx());
        this.mInventoryExtended = null;
        this.mInventoryExtendedFilters = null;
        this.mInventoryExtendedStreamRunning = false;
    }
    
    public boolean isInventoryExtendedStreamRunning() {
        return this.mInventoryExtendedStreamRunning;
    }
    
    public void nxpReadProtect(final int passwd, final boolean set, final int sBank, final int sAddress, final int sMaskBitLength, final byte[] sMask) throws Exception {
        this.exchangeCommand(new NurCmdNxpReadProtect(passwd, set, sBank, sAddress, sMaskBitLength, sMask));
    }
    
    public void nxpEas(final int passwd, final boolean set, final int sBank, final int sAddress, final int sMaskBitLength, final byte[] sMask) throws Exception {
        this.exchangeCommand(new NurCmdNxpEas(passwd, set, sBank, sAddress, sMaskBitLength, sMask));
    }
    
    public void nxpAlarm() throws Exception {
        this.exchangeCommand(new NurCmdNxpEasAlarm());
    }
    
    public void nxpStartAlarmStream() throws Exception {
        this.exchangeCommand(new NurCmdNxpEasAlarm(true));
        this.mNxpAlarmStreamRunning = true;
    }
    
    public void nxpStopAlarmStream() throws Exception {
        this.exchangeCommand(new NurCmdNxpEasAlarm(false));
        this.mNxpAlarmStreamRunning = false;
    }
    
    public boolean isNxpAlarmStreamRunning() {
        return this.mNxpAlarmStreamRunning;
    }
    
    public NurRespEpcEnum startEpcEnumStream(final NurEpcEnumParam params) throws Exception {
        final NurRespEpcEnum resp = ((NurCmdEpcEnum)this.exchangeCommand(new NurCmdEpcEnum(params))).getResponse();
        this.mEpcEnumStreamRunning = (params != null);
        return resp;
    }
    
    public void stopEpcEnumStream() throws Exception {
        this.exchangeCommand(new NurCmdEpcEnum(null));
        this.mEpcEnumStreamRunning = false;
    }
    
    public boolean isEpcEnumRunning() {
        return this.mEpcEnumStreamRunning;
    }
    
    public NurRespMonzaQT monza4QtRead(final int passwd, final int sBank, final int sAddress, final int sMaskBitLength, final byte[] sMask) throws Exception {
        return ((NurCmdMz4Qt)this.exchangeCommand(new NurCmdMz4Qt(passwd, sBank, sAddress, sMaskBitLength, sMask))).getResponse();
    }
    
    public void monza4QtWrite(final int passwd, final boolean reduce, final boolean pubMem, final int sBank, final int sAddress, final int sMaskBitLength, final byte[] sMask) throws Exception {
        this.exchangeCommand(new NurCmdMz4Qt(passwd, reduce, pubMem, sBank, sAddress, sMaskBitLength, sMask));
    }
    
    public NurTuneResponse[] tuneAntenna(final boolean wideTune, final boolean saveResult) throws Exception {
        return ((NurCmdTuneAntenna)this.exchangeCommand(new NurCmdTuneAntenna(wideTune, saveResult), 20000)).getResponse();
    }
    
    public NurTuneResponse[] tuneAntenna(final int antenna, final boolean wideTune, final boolean saveResult) throws Exception {
        return ((NurCmdTuneAntenna)this.exchangeCommand(new NurCmdTuneAntenna(antenna, wideTune, saveResult), 20000)).getResponse();
    }
    
    public NurTuneResponse[] tuneAntenna(final int antenna, final int band, final boolean wideTune, final boolean saveResult) throws Exception {
        return ((NurCmdTuneAntenna)this.exchangeCommand(new NurCmdTuneAntenna(antenna, band, wideTune, saveResult), 10000)).getResponse();
    }
    
    public NurTuneResponse[] tuneEUBand(final int antenna, final boolean saveResult) throws Exception {
        return ((NurCmdTuneAntenna)this.exchangeCommand(new NurCmdTuneAntenna(antenna, 1, true, saveResult), 12000)).getResponse();
    }
    
    public NurTuneResponse[] tuneFCCBands(final int antenna, final boolean saveResult) throws Exception {
        final NurTuneResponse[] resp = { ((NurCmdTuneAntenna)this.exchangeCommand(new NurCmdTuneAntenna(antenna, 3, true, saveResult), 12000)).getResponse()[0], ((NurCmdTuneAntenna)this.exchangeCommand(new NurCmdTuneAntenna(antenna, 4, true, saveResult), 12000)).getResponse()[0] };
        return resp;
    }
    
    public void revertTune(final int revertType) throws Exception {
        this.exchangeCommand(new NurCmdRevertTune(revertType));
    }
    
    public NurRespScanChannelInfo[] scanChannels() throws Exception {
        return ((NurCmdScanChannels)this.exchangeCommand(new NurCmdScanChannels())).getResponse();
    }
    
    public NurRespScanChannelInfo scanChannel(final int channel) throws Exception {
        return ((NurCmdScanChannels)this.exchangeCommand(new NurCmdScanChannels(channel))).getSingleResponse();
    }
    
    private int bootLoaderValidate(final int size, final long crc) throws Exception {
        return ((NurCmdBlValidate)this.exchangeCommand(new NurCmdBlValidate(size, crc))).getStatus();
    }
    
    private NurBinaryHeader byteArrayToBinaryHeader(final byte[] buffer) throws NurApiException {
        if (buffer.length >= 124) {
            final NurBinaryHeader hdr = new NurBinaryHeader();
            int offset = 0;
            hdr.id = NurPacket.BytesToDword(buffer, offset);
            offset += 4;
            hdr.szHdr = NurPacket.BytesToDword(buffer, offset);
            offset += 4;
            hdr.hdrVersion = NurPacket.BytesToDword(buffer, offset);
            offset += 4;
            hdr.type = NurPacket.BytesToDword(buffer, offset);
            offset += 4;
            hdr.arch = NurPacket.BytesToDword(buffer, offset);
            offset += 4;
            hdr.vMajor = NurPacket.BytesToByte(buffer, offset++);
            hdr.vMinor = NurPacket.BytesToByte(buffer, offset++);
            hdr.devbuild = NurPacket.BytesToByte(buffer, offset++);
            hdr.verRsvrd = NurPacket.BytesToByte(buffer, offset++);
            hdr.flags = NurPacket.BytesToDword(buffer, offset);
            offset += 4;
            hdr.first = NurPacket.BytesToDword(buffer, offset);
            offset += 4;
            hdr.count = NurPacket.BytesToDword(buffer, offset);
            offset += 4;
            hdr.payloadCRC = NurPacket.BytesToDword(buffer, offset);
            offset += 4;
            hdr.actualsize = NurPacket.BytesToDword(buffer, offset);
            offset += 4;
            System.arraycopy(buffer, offset, hdr.checksum, 0, 16);
            offset += 16;
            for (int i = 0; i < 16; ++i) {
                hdr.resrvd[i] = NurPacket.BytesToDword(buffer, offset);
                offset += 4;
            }
            return hdr;
        }
        return null;
    }
    
    private int checkForNurHeader(final byte[] buffer, final int bufferLength) throws NurApiException {
        final NurBinaryHeader hdr = this.byteArrayToBinaryHeader(buffer);
        int checkRes = 2;
        int errors = 0;
        MD5_CTX theMD5Ctx = null;
        if (hdr != null && hdr.id == 1112692046) {
            int szData = bufferLength - 124;
            if (szData != hdr.count * 260 || hdr.count == 0) {
                this.ELog(String.format("checkForNurHeader() data size mismatch; %d bytes when expecting %d bytes.", szData, hdr.count * 260));
                ++errors;
            }
            else {
                theMD5Ctx = new MD5_CTX();
                MD5.MD5Init(theMD5Ctx);
                MD5.MD5Update(theMD5Ctx, buffer, 124, szData);
                MD5.MD5Final(theMD5Ctx);
                if (Arrays.equals(hdr.checksum, theMD5Ctx.digest)) {
                    this.ELog("checkForNurHeader() checksum failure.");
                    ++errors;
                }
            }
            szData = hdr.count * 256;
            if (hdr.type == 886863401) {
                if (hdr.first != 256 || szData > 180224 || szData < 32768) {
                    this.ELog(String.format("checkForNurHeader() APP first page (%d) or size (%d bytes) mismatch.", hdr.first, szData));
                    ++errors;
                }
                else if (hdr.actualsize == 0 || hdr.actualsize > szData || hdr.actualsize > 180224) {
                    this.ELog(String.format("checkForNurHeader() APP actual size %d bytes is invalid.", hdr.actualsize));
                    ++errors;
                }
            }
            else if (hdr.type == -2114012070) {
                if (hdr.first != 0 || szData > 65536) {
                    this.ELog(String.format("checkForNurHeader() BL first page (%d) or size (%d bytes) mismatch.", hdr.first, szData));
                    ++errors;
                }
                else if (hdr.actualsize == 0 || hdr.actualsize > szData || hdr.actualsize > 65536) {
                    this.ELog(String.format("checkForNurHeader() BL actual size %d bytes is invalid.", hdr.actualsize));
                    ++errors;
                }
            }
            else {
                this.ELog(String.format("checkForNurHeader() unexpected header type; 0x%d08lX.", hdr.type));
                ++errors;
            }
            if (hdr.arch != 794702455 && hdr.arch != -1996702370) {
                this.ELog(String.format("checkForNurHeader() MCU architecture 0x%d08lX is beyond comprehension.", hdr.arch));
                ++errors;
            }
            checkRes = ((errors != 0) ? 1 : 0);
        }
        this.VLog(String.format("checkForNurHeader() %d errors in check.", errors));
        return checkRes;
    }
    
    private void checkAppSizeThrow(final long szBinBuffer) throws Exception {
        if (szBinBuffer < 32768L || szBinBuffer > 180224L) {
            throw new NurApiException("Invalid application binary size", 2);
        }
    }
    
    private void checkLoaderSizeThrow(final long szBinBuffer) throws Exception {
        if (szBinBuffer < 16384L || szBinBuffer > 65536L) {
            throw new NurApiException("Invalid bootloader binary size", 2);
        }
    }
    
    private int programBuffer(final int startPage, final int validateCmd, final byte[] buffer, final int bufferLength) throws Exception {
        if (validateCmd != 116 && validateCmd != 119) {
            throw new NurApiException(5);
        }
        int error = 0;
        int numPages = 0;
        int numPagesReminder = 0;
        int writePos = 0;
        int currentPage = startPage;
        int writeRetries = 3;
        long appCRC = 0L;
        boolean nurFormat = false;
        NurBinaryHeader binHdr = null;
        final NurPageWrite pageWriteParams = new NurPageWrite();
        final NurEventProgrammingProgress notificationData = new NurEventProgrammingProgress();
        error = this.checkForNurHeader(buffer, bufferLength);
        if (error == 0) {
            binHdr = this.byteArrayToBinaryHeader(buffer);
            nurFormat = true;
            final NurRespMCUArch mMCUArch = this.confirmMCUArch(binHdr.arch);
            error = mMCUArch.error;
            if (error != 0) {
                if (error != 1) {
                    this.ELog(String.format("programBuffer() MCU architecture mismatch. Is 0x%d08lX, expected 0x%d08lX. Error = %d", mMCUArch.actualArch, binHdr.arch, error));
                }
                else {
                    this.ELog("programBuffer() module does not support architecture query.");
                }
                throw new NurApiException(4109);
            }
            numPages = binHdr.count;
            numPagesReminder = 0;
            currentPage = binHdr.first;
            writePos = 124;
        }
        else {
            if (error == 1) {
                throw new NurApiException("programBuffer()", 4108);
            }
            numPages = bufferLength / 256;
            numPagesReminder = bufferLength % 256;
            if (numPagesReminder > 0) {
                ++numPages;
            }
        }
        this.VLog(String.format("NurApiProgramBuffer() numPages %d; startPage %d", numPages, startPage));
        notificationData.error = 0;
        notificationData.totalPages = numPages;
        notificationData.currentPage = -1;
        this.sendProgrammingEvent(notificationData);
        if (!nurFormat) {
            appCRC = CRC32.calc(0L, buffer, 0, bufferLength);
        }
        if (startPage == 10064) {
            this.mValidDevCaps = false;
        }
        while (writePos < bufferLength) {
            if (!nurFormat) {
                int pageSize = bufferLength - writePos;
                if (pageSize > 256) {
                    pageSize = 256;
                }
                pageWriteParams.pageToWrite = currentPage;
                System.arraycopy(buffer, writePos, pageWriteParams.data, 0, pageSize);
                if (pageSize < 256) {
                    for (int i = pageSize; i < 256; ++i) {
                        pageWriteParams.data[i] = -1;
                    }
                }
                notificationData.currentPage = currentPage - startPage;
                this.sendProgrammingEvent(notificationData);
                writeRetries = 3;
                while (writeRetries-- > 0) {
                    try {
                        if (startPage == 10064) {
                            error = this.pageWrite(pageWriteParams.pageToWrite, pageWriteParams.data, 10000);
                        }
                        else {
                            error = this.pageWrite(pageWriteParams.pageToWrite, pageWriteParams.data);
                        }
                    }
                    catch (Exception e) {
                        this.ELog("error: " + e);
                        error = 10;
                        if (startPage != 10064) {
                            continue;
                        }
                        this.ELog("secChip wr ERROR: Page=" + pageWriteParams.pageToWrite);
                        continue;
                    }
                    break;
                }
                if (error != 0) {
                    break;
                }
                ++currentPage;
                writePos += pageSize;
            }
            else {
                notificationData.currentPage = currentPage - startPage;
                this.sendProgrammingEvent(notificationData);
                pageWriteParams.pageToWrite = currentPage;
                System.arraycopy(buffer, writePos + 4, pageWriteParams.data, 0, 256);
                writeRetries = 3;
                while (writeRetries-- > 0) {
                    try {
                        if (startPage == 10064) {
                            error = this.pageWrite(pageWriteParams.pageToWrite, pageWriteParams.data, 10000);
                        }
                        else {
                            error = this.pageWrite(pageWriteParams.pageToWrite, pageWriteParams.data);
                        }
                    }
                    catch (Exception e) {
                        this.ELog("error: " + e);
                        error = 10;
                        continue;
                    }
                    break;
                }
                if (error != 0) {
                    break;
                }
                ++currentPage;
                writePos += 260;
            }
        }
        if (error == 0 && validateCmd != 0) {
            final long crcParam = nurFormat ? binHdr.payloadCRC : appCRC;
            final int sizeParam = nurFormat ? binHdr.actualsize : bufferLength;
            writeRetries = 3;
            while (writeRetries-- > 0) {
                try {
                    error = ((validateCmd == 116) ? this.applicationValidate(sizeParam, crcParam) : this.bootLoaderValidate(sizeParam, crcParam));
                }
                catch (NurApiException e2) {
                    this.ELog("validate error: " + e2);
                    error = e2.error;
                    continue;
                }
                break;
            }
        }
        if (error != 0) {
            notificationData.error = error;
            this.sendProgrammingEvent(notificationData);
        }
        else {
            notificationData.error = error;
            this.sendProgrammingEvent(notificationData);
        }
        return error;
    }
    
    private int programFile(final int startPage, final int validateCmd, final String filename) throws Exception {
        final File file = new File(filename);
        final FileInputStream fileInputStream = new FileInputStream(file);
        final int bufferLength = (int)file.length();
        final byte[] buffer = new byte[bufferLength];
        fileInputStream.read(buffer);
        fileInputStream.close();
        if (validateCmd == 116) {
            this.checkAppSizeThrow(buffer.length);
        }
        else if (validateCmd == 119) {
            this.checkLoaderSizeThrow(buffer.length);
        }
        return this.programBuffer(startPage, validateCmd, buffer, bufferLength);
    }
    
    private NurRespMCUArch confirmMCUArch(final int requiredArch) throws Exception {
        return ((NurCmdMCUArch)this.exchangeCommand(new NurCmdMCUArch(requiredArch))).getResponse();
    }
    
    private int pageWrite(final int pageToWrite, final byte[] data) throws Exception {
        return ((NurCmdPageWrite)this.exchangeCommand(new NurCmdPageWrite(pageToWrite, data))).getStatus();
    }
    
    private int pageWrite(final int pageToWrite, final byte[] data, final int timeout) throws Exception {
        return ((NurCmdPageWrite)this.exchangeCommand(new NurCmdPageWrite(pageToWrite, data), timeout)).getStatus();
    }
    
    private int applicationValidate(final int size, final long crc) throws Exception {
        return ((NurCmdAppValidate)this.exchangeCommand(new NurCmdAppValidate(size, crc))).getStatus();
    }
    
    public void programApplication(final byte[] buffer, final int bufferLength) throws Exception {
        final NurBinFileType binType = this.checkNurFwBinary(buffer, this.getFwInfo().get("MODULE"));
        final int devType = this.getDeviceCaps().moduleType;
        if (binType.fwType != 2) {
            throw new NurApiException("programApplicationFile: file check failed");
        }
        final int error = this.programBuffer(256, 116, buffer, bufferLength);
        if (error != 0) {
            throw new NurApiException(error);
        }
    }
    
    public void programApplicationFile(final String filename) throws Exception {
        final NurBinFileType binType = this.checkNurFwBinaryFile(filename, this.getFwInfo().get("MODULE"));
        final int devType = this.getDeviceCaps().moduleType;
        if (binType.fwType != 2) {
            throw new NurApiException("programApplicationFile: file check failed");
        }
        final int error = this.programFile(256, 116, filename);
        if (error != 0) {
            throw new NurApiException(error);
        }
    }
    
    public void programSecChipFile(final String filename) throws Exception {
        final int error = this.programFile(10064, 116, filename);
        if (error != 0) {
            throw new NurApiException(error);
        }
    }
    
    public void programSecChipBuffer(final byte[] buffer) throws Exception {
        final int error = this.programBuffer(10064, 116, buffer, buffer.length);
        if (error != 0) {
            throw new NurApiException(error);
        }
    }
    
    public void programBootLoader(final byte[] buffer, final int bufferLength) throws Exception {
        final NurBinFileType binType = this.checkNurFwBinary(buffer, this.getFwInfo().get("MODULE"));
        final int devType = this.getDeviceCaps().moduleType;
        if (binType.fwType != 1) {
            throw new NurApiException("programApplicationFile: file check failed");
        }
        final int error = this.programBuffer(0, 119, buffer, bufferLength);
        if (error != 0) {
            throw new NurApiException(error);
        }
    }
    
    public void programBootloaderFile(final String filename) throws Exception {
        final NurBinFileType binType = this.checkNurFwBinaryFile(filename, this.getFwInfo().get("MODULE"));
        final int devType = this.getDeviceCaps().moduleType;
        if (binType.fwType != 1) {
            throw new NurApiException("programBootloaderFile: file check failed");
        }
        final int error = this.programFile(0, 119, filename);
        if (error != 0) {
            throw new NurApiException(error);
        }
    }
    
    public void enterBootLoader() throws Exception {
        if (this.getMode().equals("A")) {
            this.moduleBoot(false);
        }
    }
    
    public void exitBootLoader() throws Exception {
        if (this.getMode().equals("B")) {
            this.moduleBoot(false);
        }
    }
    
    public void moduleBoot(final boolean restartOnly) throws Exception {
        if (restartOnly) {
            this.exchangeCommand(new NurCmdRestart());
        }
        else {
            this.exchangeCommand(new NurCmdEnterBoot());
        }
        Thread.sleep(200L);
    }
    
    public int getSetupLinkFreq() throws Exception {
        return this.getModuleSetup(1).linkFreq;
    }
    
    public void setSetupLinkFreq(final int linkFreq) throws Exception {
        final NurSetup setup = new NurSetup();
        setup.linkFreq = linkFreq;
        this.setModuleSetup(setup, 1);
    }
    
    public int getSetupRxDecoding() throws Exception {
        return this.getModuleSetup(2).rxDecoding;
    }
    
    public void setSetupRxDecoding(final int rxDecoding) throws Exception {
        final NurSetup setup = new NurSetup();
        setup.rxDecoding = rxDecoding;
        this.setModuleSetup(setup, 2);
    }
    
    public int getSetupTxLevel() throws Exception {
        return this.getModuleSetup(4).txLevel;
    }
    
    public void setSetupTxLevel(final int txLevel) throws Exception {
        final NurSetup setup = new NurSetup();
        setup.txLevel = txLevel;
        this.setModuleSetup(setup, 4);
    }
    
    public int getSetupTxModulation() throws Exception {
        return this.getModuleSetup(8).txModulation;
    }
    
    public void setSetupTxModulation(final int txModulation) throws Exception {
        final NurSetup setup = new NurSetup();
        setup.txModulation = txModulation;
        this.setModuleSetup(setup, 8);
    }
    
    public int getSetupRegionId() throws Exception {
        return this.getModuleSetup(16).regionId;
    }
    
    public void setSetupRegionId(final int regionId) throws Exception {
        final NurSetup setup = new NurSetup();
        setup.regionId = regionId;
        this.setModuleSetup(setup, 16);
    }
    
    public int getSetupInventoryQ() throws Exception {
        return this.getModuleSetup(32).inventoryQ;
    }
    
    public void setSetupInventoryQ(final int inventoryQ) throws Exception {
        final NurSetup setup = new NurSetup();
        setup.inventoryQ = inventoryQ;
        this.setModuleSetup(setup, 32);
    }
    
    public int getSetupInventorySession() throws Exception {
        return this.getModuleSetup(64).inventorySession;
    }
    
    public void setSetupInventorySession(final int inventorySession) throws Exception {
        final NurSetup setup = new NurSetup();
        setup.inventorySession = inventorySession;
        this.setModuleSetup(setup, 64);
    }
    
    public int getSetupInventoryRounds() throws Exception {
        return this.getModuleSetup(128).inventoryRounds;
    }
    
    public void setSetupInventoryRounds(final int inventoryRounds) throws Exception {
        final NurSetup setup = new NurSetup();
        setup.inventoryRounds = inventoryRounds;
        this.setModuleSetup(setup, 128);
    }
    
    public int getSetupAntennaMask() throws Exception {
        return this.getModuleSetup(256).antennaMask;
    }
    
    public int getSetupAntennaMaskEx() throws Exception {
        return this.getModuleSetup(33554432).antennaMaskEx;
    }
    
    public void setSetupAntennaMask(final int antennaMask) throws Exception {
        final NurSetup setup = new NurSetup();
        setup.antennaMask = antennaMask;
        this.setModuleSetup(setup, 256);
    }
    
    public void setSetupAntennaMaskEx(final int antennaMaskEx) throws Exception {
        final NurSetup setup = new NurSetup();
        setup.antennaMaskEx = antennaMaskEx;
        this.setModuleSetup(setup, 33554432);
    }
    
    public int getSetupScanSingleTriggerTimeout() throws Exception {
        return this.getModuleSetup(512).scanSingleTriggerTimeout;
    }
    
    public void setSetupScanSingleTriggerTimeout(final int scanSingleTriggerTimeout) throws Exception {
        final NurSetup setup = new NurSetup();
        setup.scanSingleTriggerTimeout = scanSingleTriggerTimeout;
        this.setModuleSetup(setup, 512);
    }
    
    public int getSetupInventoryTriggerTimeout() throws Exception {
        return this.getModuleSetup(1024).inventoryTriggerTimeout;
    }
    
    public void setSetupInventoryTriggerTimeout(final int inventoryTriggerTimeout) throws Exception {
        final NurSetup setup = new NurSetup();
        setup.inventoryTriggerTimeout = inventoryTriggerTimeout;
        this.setModuleSetup(setup, 1024);
    }
    
    public int getSetupSelectedAntenna() throws Exception {
        return this.getModuleSetup(2048).selectedAntenna;
    }
    
    public void setSetupSelectedAntenna(final int selectedAntenna) throws Exception {
        final NurSetup setup = new NurSetup();
        setup.selectedAntenna = selectedAntenna;
        this.setModuleSetup(setup, 2048);
    }
    
    public int getSetupOpFlags() throws Exception {
        return this.getModuleSetup(4096).opFlags;
    }
    
    public void setSetupOpFlags(final int opFlags) throws Exception {
        final NurSetup setup = new NurSetup();
        setup.opFlags = opFlags;
        this.setModuleSetup(setup, 4096);
    }
    
    public void enableInventoryStreamZeros(final boolean enable) throws Exception {
        final int flags = this.getSetupOpFlags();
        if (enable && (flags & 0x2) == 0x0) {
            this.setSetupOpFlags(flags | 0x2);
        }
        else if (!enable && (flags & 0x2) != 0x0) {
            this.setSetupOpFlags(flags & 0xFFFFFFFD);
        }
    }
    
    public int getSetupInventoryTarget() throws Exception {
        return this.getModuleSetup(8192).inventoryTarget;
    }
    
    public void setSetupInventoryTarget(final int inventoryTarget) throws Exception {
        final NurSetup setup = new NurSetup();
        setup.inventoryTarget = inventoryTarget;
        this.setModuleSetup(setup, 8192);
    }
    
    public int getSetupInventoryEpcLength() throws Exception {
        return this.getModuleSetup(16384).inventoryEpcLength;
    }
    
    public void setSetupInventoryEpcLength(final int inventoryEpcLength) throws Exception {
        final NurSetup setup = new NurSetup();
        setup.inventoryEpcLength = inventoryEpcLength;
        this.setModuleSetup(setup, 16384);
    }
    
    public RssiFilter getSetupReadRssiFilter() throws Exception {
        return this.getModuleSetup(32768).readRssiFilter;
    }
    
    public void setSetupReadRssiFilter(final RssiFilter readRssiFilter) throws Exception {
        final NurSetup setup = new NurSetup();
        setup.readRssiFilter = readRssiFilter;
        this.setModuleSetup(setup, 32768);
    }
    
    public void setSetupReadRssiFilter(final int min, final int max) throws Exception {
        final NurSetup setup = new NurSetup();
        setup.readRssiFilter.min = min;
        setup.readRssiFilter.max = max;
        this.setModuleSetup(setup, 32768);
    }
    
    public RssiFilter getSetupWriteRssiFilter() throws Exception {
        return this.getModuleSetup(65536).writeRssiFilter;
    }
    
    public void setSetupWriteRssiFilter(final RssiFilter writeRssiFilter) throws Exception {
        final NurSetup setup = new NurSetup();
        setup.writeRssiFilter = writeRssiFilter;
        this.setModuleSetup(setup, 65536);
    }
    
    public void setSetupWriteRssiFilter(final int min, final int max) throws Exception {
        final NurSetup setup = new NurSetup();
        setup.writeRssiFilter.min = min;
        setup.writeRssiFilter.max = max;
        this.setModuleSetup(setup, 65536);
    }
    
    public RssiFilter getSetupInventoryRssiFilter() throws Exception {
        return this.getModuleSetup(131072).inventoryRssiFilter;
    }
    
    public void setSetupInventoryRssiFilter(final RssiFilter inventoryRssiFilter) throws Exception {
        final NurSetup setup = new NurSetup();
        setup.inventoryRssiFilter = inventoryRssiFilter;
        this.setModuleSetup(setup, 131072);
    }
    
    public void setSetupInventoryRssiFilter(final int min, final int max) throws Exception {
        final NurSetup setup = new NurSetup();
        setup.inventoryRssiFilter.min = min;
        setup.inventoryRssiFilter.max = max;
        this.setModuleSetup(setup, 131072);
    }
    
    public void setSetupReadTimeout(final int timeout) throws Exception {
        final NurSetup setup = new NurSetup();
        setup.readTimeout = timeout;
        this.setModuleSetup(setup, 262144);
    }
    
    public int getSetupReadTimeout() throws Exception {
        return this.getModuleSetup(262144).readTimeout;
    }
    
    public void setSetupWriteTimeout(final int timeout) throws Exception {
        final NurSetup setup = new NurSetup();
        setup.writeTimeout = timeout;
        this.setModuleSetup(setup, 524288);
    }
    
    public int getSetupWriteTimeout() throws Exception {
        return this.getModuleSetup(524288).writeTimeout;
    }
    
    public void setSetupLockTimeout(final int timeout) throws Exception {
        final NurSetup setup = new NurSetup();
        setup.lockTimeout = timeout;
        this.setModuleSetup(setup, 1048576);
    }
    
    public int getSetupLockTimeout() throws Exception {
        return this.getModuleSetup(1048576).lockTimeout;
    }
    
    public void setSetupKillTimeout(final int timeout) throws Exception {
        final NurSetup setup = new NurSetup();
        setup.killTimeout = timeout;
        this.setModuleSetup(setup, 2097152);
    }
    
    public int getSetupKillTimeout() throws Exception {
        return this.getModuleSetup(2097152).killTimeout;
    }
    
    public void setSetupAutoPeriod(final int autoSetup) throws Exception {
        final NurSetup setup = new NurSetup();
        setup.periodSetup = autoSetup;
        this.setModuleSetup(setup, 4194304);
    }
    
    public int getSetupAutoPeriod() throws Exception {
        return this.getModuleSetup(4194304).periodSetup;
    }
    
    public void setPerAntennaPower(final int[] antPower) throws Exception {
        final NurSetup setup = new NurSetup();
        if (antPower == null || antPower.length != 4) {
            throw new NurApiException("setPerAntennaPower : invalid parameter.");
        }
        System.arraycopy(antPower, 0, setup.antPower, 0, 4);
        this.setModuleSetup(setup, 8388608);
    }
    
    public int[] getPerAntennaPower() throws Exception {
        return this.getModuleSetup(8388608).antPower;
    }
    
    public void setPerAntennaPowerEx(final int[] antPowerEx) throws Exception {
        final NurSetup setup = new NurSetup();
        if (antPowerEx == null || antPowerEx.length != 32) {
            throw new NurApiException("setPerAntennaPowerEx : invalid parameter.");
        }
        System.arraycopy(antPowerEx, 0, setup.antPowerEx, 0, 32);
        this.setModuleSetup(setup, 134217728);
    }
    
    public int[] getPerAntennaPowerEx() throws Exception {
        return this.getModuleSetup(134217728).antPowerEx;
    }
    
    public void setPowerOffset(final int offset) throws Exception {
        final NurSetup setup = new NurSetup();
        setup.powerOffset = offset;
        this.setModuleSetup(setup, 16777216);
    }
    
    public int getSetupRxSensitivity() throws Exception {
        return this.getModuleSetup(268435456).rxSensitivity;
    }
    
    public void setSetupRxSensitivity(final int rxSens) throws Exception {
        final NurSetup setup = new NurSetup();
        setup.rxSensitivity = rxSens;
        this.setModuleSetup(setup, 268435456);
    }
    
    public int getSetupRfProfile() throws Exception {
        return this.getModuleSetup(536870912).rfProfile;
    }
    
    public void setSetupRfProfile(final int rfProfile) throws Exception {
        final NurSetup setup = new NurSetup();
        setup.rfProfile = rfProfile;
        this.setModuleSetup(setup, 536870912);
    }
    
    public AutotuneSetup getSetupAutotune() throws Exception {
        return this.getModuleSetup(67108864).autotune;
    }
    
    public void setSetupAutotune(final AutotuneSetup atSetup) throws Exception {
        final NurSetup setup = new NurSetup();
        setup.autotune = atSetup;
        this.setModuleSetup(setup, 67108864);
    }
    
    public int getPowerOffset() throws Exception {
        return this.getModuleSetup(16777216).powerOffset;
    }
    
    public void setExtendedCarrier(final boolean on) throws Exception {
        this.exchangeCommand(new NurCmdExtendedCarrier(on));
    }
    
    public void setIRConfig(final int type, final int bank, final int addr, final int nWords) throws Exception {
        this.exchangeCommand(new NurCmdInvReadConfig(true, type, bank, addr, nWords));
    }
    
    public void setIRConfig(final NurIRConfig config) throws Exception {
        this.exchangeCommand(new NurCmdInvReadConfig(config.IsRunning, config.irType, config.irBank, config.irAddr, config.irWordCount));
    }
    
    public NurIRConfig getIRConfig() throws Exception {
        return ((NurCmdInvReadConfig)this.exchangeCommand(new NurCmdInvReadConfig())).getResponse();
    }
    
    public void setIRState(final boolean running) throws Exception {
        this.exchangeCommand(new NurCmdInvReadConfig(running));
    }
    
    public void tidInventory(final int firstWord, final int nWords) throws Exception {
        this.exchangeCommand(new NurCmdInvReadConfig(true, 1, 2, firstWord, nWords));
    }
    
    public void userMemInventory(final int firstWord, final int nWords) throws Exception {
        this.exchangeCommand(new NurCmdInvReadConfig(true, 1, 3, firstWord, nWords));
    }
    
    public NurRespCustomExchange customExchange(final int passwd, final boolean secured, final int sBank, final int sAddress, final int sMaskBitLength, final byte[] sMask, final CustomExchangeParams params) throws Exception {
        return ((NurCmdCustomExchange)this.exchangeCommand(new NurCmdCustomExchange(this, passwd, secured, sBank, sAddress, sMaskBitLength, sMask, params))).getResponse();
    }
    
    public NurRespCustomExchange customExchangeByEpc(final int passwd, final boolean secured, final byte[] epc, final CustomExchangeParams params) throws Exception {
        return ((NurCmdCustomExchange)this.exchangeCommand(new NurCmdCustomExchange(this, passwd, secured, 1, 32, epc.length * 8, epc, params))).getResponse();
    }
    
    public NurRespCustomExchange customExchange(final int sBank, final int sAddress, final int sMaskBitLength, final byte[] sMask, final CustomExchangeParams params) throws Exception {
        return ((NurCmdCustomExchange)this.exchangeCommand(new NurCmdCustomExchange(this, 0, false, sBank, sAddress, sMaskBitLength, sMask, params))).getResponse();
    }
    
    public NurRespCustomExchange customExchangeByEpc(final byte[] epc, final CustomExchangeParams params) throws Exception {
        return ((NurCmdCustomExchange)this.exchangeCommand(new NurCmdCustomExchange(this, 0, false, 1, 32, epc.length * 8, epc, params))).getResponse();
    }
    
    public boolean xpcRemovalEnabled() {
        return this.mXPCRemovalEnabled;
    }
    
    public void enableXPCRemoval(final boolean enable) {
        this.mXPCRemovalEnabled = enable;
        this.mStorage.enableXPCRemoval(enable);
    }
    
    public static XPCSpec getEpcXpcSpec(final int pc, final byte[] epc) {
        int xpc_w1 = 0;
        int xpc_w2 = 0;
        int epcLen = 0;
        if ((pc & 0x200) == 0x0) {
            return null;
        }
        XPCSpec spec = new XPCSpec();
        epcLen = epc.length;
        xpc_w1 = epc[0];
        xpc_w1 <<= 8;
        xpc_w1 |= epc[1];
        if ((xpc_w1 & 0x8000) != 0x0) {
            xpc_w2 = epc[2];
            xpc_w2 <<= 8;
            xpc_w2 |= epc[3];
            if (epcLen > 4) {
                System.arraycopy(epc, 2, spec.modifiedEpc = new byte[epcLen - 4], 0, epcLen - 4);
                spec.xpc_w1 = xpc_w1;
                spec.xpc_w2 = xpc_w2;
            }
            else {
                spec = null;
            }
        }
        else if (epcLen > 2) {
            System.arraycopy(epc, 2, spec.modifiedEpc = new byte[epcLen - 2], 0, epcLen - 2);
            spec.xpc_w1 = xpc_w1;
        }
        else {
            spec = null;
        }
        return spec;
    }
    
    private static boolean longBitIsSet(final long longValue, final int valueBit) {
        long longMask = 1L;
        longMask <<= valueBit;
        return (longMask & longValue) != 0x0L;
    }
    
    private static byte[] AES_ECB(final byte[] input, final byte[] key, final boolean encrypt) throws NurApiException {
        byte[] output = null;
        String strExc = "AES128 ECB ";
        int mode = 0;
        final SecretKeySpec sk = new SecretKeySpec(key, "AES");
        if (encrypt) {
            mode = 1;
            strExc += "encrypt ";
        }
        else {
            mode = 2;
            strExc += "decrypt ";
        }
        strExc += "error: ";
        try {
            final Cipher c = Cipher.getInstance("AES/ECB/NoPadding");
            c.init(mode, sk);
            output = c.doFinal(input);
        }
        catch (Exception e) {
            throw new NurApiException(strExc + e.getMessage(), 5);
        }
        return output;
    }
    
    private static byte[] AES_CBC(final byte[] input, final byte[] key, final byte[] iv, final boolean encrypt) throws NurApiException {
        byte[] output = null;
        String strExc = "AES128 CBC ";
        int mode = 0;
        final SecretKeySpec sk = new SecretKeySpec(key, "AES");
        if (encrypt) {
            mode = 1;
            strExc += "encrypt ";
        }
        else {
            mode = 2;
            strExc += "decrypt ";
        }
        strExc += "error: ";
        try {
            final Cipher c = Cipher.getInstance("AES/CBC/NoPadding");
            final IvParameterSpec initVect = new IvParameterSpec(iv);
            c.init(mode, sk, initVect);
            output = c.doFinal(input);
        }
        catch (Exception e) {
            throw new NurApiException(strExc + e.getMessage(), 5);
        }
        return output;
    }
    
    public static byte[] AES128_ECBEncrypt(final byte[] input, final byte[] key) throws NurApiException {
        return AES_ECB(input, key, true);
    }
    
    public static byte[] AES128_ECBDecrypt(final byte[] input, final byte[] key) throws NurApiException {
        return AES_ECB(input, key, false);
    }
    
    public static byte[] AES128_CBCEncrypt(final byte[] input, final byte[] key, final byte[] iv) throws NurApiException {
        return AES_CBC(input, key, iv, true);
    }
    
    public static byte[] AES128_CBCDecrypt(final byte[] input, final byte[] key, final byte[] iv) throws NurApiException {
        return AES_CBC(input, key, iv, false);
    }
    
    public static int bitBufferSetBit(final byte[] bitBuffer, int bitNumber, final boolean set) throws Exception {
        if (bitBuffer == null || bitNumber < 0 || bitNumber >= bitBuffer.length * 8) {
            throw new Exception("setBit(): invalid parameter(s).");
        }
        final int bytePtr = bitNumber / 8;
        final int byteShift = bitNumber % 8;
        int byteMask = 128;
        byteMask >>= byteShift;
        int b = bitBuffer[bytePtr];
        if (!set) {
            b &= (~byteMask & 0xFF);
        }
        else {
            b |= byteMask;
        }
        bitBuffer[bytePtr] = (byte)(b & 0xFF);
        return ++bitNumber;
    }
    
    public static int bitBufferGetBit(final byte[] bitBuffer, final int bitNumber) throws Exception {
        if (bitBuffer == null || bitNumber < 0 || bitNumber >= bitBuffer.length * 8) {
            throw new Exception("getBit(): invalid parameter(s).");
        }
        final int bytePtr = bitNumber / 8;
        final int byteShift = bitNumber % 8;
        int byteMask = 128;
        byteMask >>= byteShift;
        final int b = bitBuffer[bytePtr];
        if ((b & byteMask) != 0x0) {
            return 1;
        }
        return 0;
    }
    
    public static int bitBufferAddValue(final byte[] buffer, final long longValue, int nrBits, int curPointer) throws Exception {
        if (buffer == null || curPointer < 0 || nrBits < 1 || nrBits > 64) {
            throw new Exception("bitBufferAddValue: invalid parameter(s)");
        }
        if (curPointer + nrBits > buffer.length * 8) {
            throw new Exception("bitBufferAddValue: out of range error");
        }
        for (int valueBit = nrBits - 1; nrBits > 0; --nrBits, --valueBit) {
            curPointer = bitBufferSetBit(buffer, curPointer, longBitIsSet(longValue, valueBit));
        }
        return curPointer;
    }
    
    public static long getValueFromBitBuffer(final byte[] bitBuffer, final int bitAddress, final int nrOfBits) throws Exception {
        long rc = 0L;
        if (bitBuffer == null || bitAddress < 0 || nrOfBits < 1 || nrOfBits > 64 || bitAddress + nrOfBits >= bitBuffer.length * 8) {
            throw new Exception("getValueFromBitBuffer: invalid parameter(s)");
        }
        for (int lastBit = bitAddress + nrOfBits, currentBit = bitAddress; currentBit < lastBit; ++currentBit) {
            rc |= bitBufferGetBit(bitBuffer, currentBit);
            rc <<= 1;
        }
        return rc;
    }
    
    public static int bitBufferAddEBV32(final byte[] bitBuffer, final int ebvValue, int curPointer) throws Exception {
        if (bitBuffer == null || bitBuffer.length == 0) {
            throw new Exception("bitBufferAddEBV32: invalid parameter(s)");
        }
        if (ebvValue < 128) {
            return bitBufferAddValue(bitBuffer, ebvValue, 8, curPointer);
        }
        long addValue = 0L;
        int dwMask = 266338304;
        int shift = 21;
        final int highMask = -268435456;
        if ((ebvValue & highMask) != 0x0) {
            addValue = ebvValue;
            addValue >>= 28;
            addValue &= 0xFL;
            addValue |= 0x80L;
            curPointer = bitBufferAddValue(bitBuffer, addValue, 8, curPointer);
            while (dwMask > 127) {
                addValue = ebvValue;
                addValue >>= shift;
                addValue &= 0x7FL;
                addValue |= 0x80L;
                curPointer = bitBufferAddValue(bitBuffer, addValue, 8, curPointer);
                dwMask >>= 7;
                shift -= 7;
            }
        }
        else {
            boolean started = false;
            while (dwMask > 127) {
                if (!started) {
                    if ((ebvValue & dwMask) != 0x0) {
                        started = true;
                        addValue = ebvValue;
                        addValue >>= shift;
                        addValue &= 0x7FL;
                        addValue |= 0x80L;
                        curPointer = bitBufferAddValue(bitBuffer, addValue, 8, curPointer);
                    }
                }
                else {
                    addValue = ebvValue;
                    addValue >>= shift;
                    addValue &= 0x7FL;
                    addValue |= 0x80L;
                    curPointer = bitBufferAddValue(bitBuffer, addValue, 8, curPointer);
                }
                dwMask >>= 7;
                shift -= 7;
            }
        }
        curPointer = bitBufferAddValue(bitBuffer, ebvValue & 0x7F, 8, curPointer);
        return curPointer;
    }
    
    public static int bitLenToByteLen(final int nrBits) {
        int rc = 0;
        if (nrBits <= 0) {
            return 0;
        }
        rc = nrBits / 8;
        if (nrBits % 8 != 0) {
            ++rc;
        }
        return rc;
    }
    
    public ReflectedPower getReflectedPower(final int frequency) throws Exception {
        final ReflectedPower rp = ((NurCmdReflectedPower)this.exchangeCommand(new NurCmdReflectedPower(frequency))).getResponseEx();
        return rp;
    }
    
    public ReflectedPower getReflectedPower() throws Exception {
        final ReflectedPower rp = ((NurCmdReflectedPower)this.exchangeCommand(new NurCmdReflectedPower())).getResponseEx();
        return rp;
    }
    
    public float getReflectedPowerF(final int frequency) throws Exception {
        return ((NurCmdReflectedPower)this.exchangeCommand(new NurCmdReflectedPower(frequency))).getResponse();
    }
    
    public float getReflectedPowerF() throws Exception {
        return ((NurCmdReflectedPower)this.exchangeCommand(new NurCmdReflectedPower())).getResponse();
    }
    
    public NurAuthenticateResp gen2v2Authenticate(final NurAuthenticateParam authParam) throws Exception, NurApiException {
        return ((NurCmdGen2V2)this.exchangeCommand(new NurCmdGen2V2(authParam))).getAuthResponse();
    }
    
    public NurAuthenticateResp gen2v2AuthenticateByEpc(final byte[] epc, final NurAuthenticateParam authParam) throws Exception, NurApiException {
        return ((NurCmdGen2V2)this.exchangeCommand(new NurCmdGen2V2(epc, authParam))).getAuthResponse();
    }
    
    public NurAuthenticateResp gen2v2Authenticate(final int sBank, final int sAddress, final int sMaskBitLength, final byte[] sMask, final NurAuthenticateParam authParam) throws Exception, NurApiException {
        return ((NurCmdGen2V2)this.exchangeCommand(new NurCmdGen2V2(sBank, sAddress, sMaskBitLength, sMask, authParam))).getAuthResponse();
    }
    
    public void gen2v2Untraceable(final int password, final NurUntraceableParam utrParam) throws Exception {
        this.exchangeCommand(new NurCmdGen2V2(password, utrParam));
    }
    
    public void gen2v2UntraceableByEpc(final int password, final byte[] epc, final NurUntraceableParam utrParam) throws Exception {
        this.exchangeCommand(new NurCmdGen2V2(password, epc, utrParam));
    }
    
    public void gen2v2Untraceable(final int password, final int sBank, final int sAddress, final int sMaskBitLength, final byte[] sMask, final NurUntraceableParam utrParam) throws Exception {
        this.exchangeCommand(new NurCmdGen2V2(password, sBank, sAddress, sMaskBitLength, sMask, utrParam));
    }
    
    public NurRespV2ReadBuffer gen2v2ReadBuffer(final int password, final int sBank, final int sAddress, final int sMaskBitLength, final byte[] sMask, final int bitAddress, final int bitCount) throws Exception {
        final NurV2ReadBufferParam rdParam = new NurV2ReadBufferParam(bitAddress, bitCount);
        return ((NurCmdGen2V2)this.exchangeCommand(new NurCmdGen2V2(true, password, sBank, sAddress, sMaskBitLength, sMask, rdParam))).getRdBufferResp();
    }
    
    public NurRespV2ReadBuffer gen2v2ReadBufferByEpc(final int password, final byte[] epc, final int bitAddress, final int bitCount) throws Exception {
        final NurV2ReadBufferParam rdParam = new NurV2ReadBufferParam(bitAddress, bitCount);
        return ((NurCmdGen2V2)this.exchangeCommand(new NurCmdGen2V2(true, password, 1, 32, epc.length * 8, epc, rdParam))).getRdBufferResp();
    }
    
    public NurRespV2ReadBuffer gen2v2ReadBuffer(final int sBank, final int sAddress, final int sMaskBitLength, final byte[] sMask, final int bitAddress, final int bitCount) throws Exception {
        final NurV2ReadBufferParam rdParam = new NurV2ReadBufferParam(bitAddress, bitCount);
        return ((NurCmdGen2V2)this.exchangeCommand(new NurCmdGen2V2(false, 0, sBank, sAddress, sMaskBitLength, sMask, rdParam))).getRdBufferResp();
    }
    
    public NurRespV2ReadBuffer gen2v2ReadBufferByEpc(final byte[] epc, final int bitAddress, final int bitCount) throws Exception {
        final NurV2ReadBufferParam rdParam = new NurV2ReadBufferParam(bitAddress, bitCount);
        return ((NurCmdGen2V2)this.exchangeCommand(new NurCmdGen2V2(false, 0, 1, 32, epc.length * 8, epc, rdParam))).getRdBufferResp();
    }
    
    private void copySingleInventoryFilter(final NurInventoryExtendedFilter src, final NurInventoryExtendedFilter dest) {
        dest.action = src.action;
        dest.address = src.address;
        dest.bank = src.bank;
        dest.maskBitLength = src.maskBitLength;
        dest.targetSession = src.targetSession;
        dest.truncate = src.truncate;
        if (src.maskBitLength > 0 && src.maskdata != null) {
            int length = src.maskBitLength / 8;
            if (src.maskBitLength % 8 != 0) {
                ++length;
            }
            dest.maskdata = new byte[length];
            System.arraycopy(src.maskdata, 0, dest.maskdata, 0, length);
        }
    }
    
    private void copyComplexFilters(final NurTagTrackingConfig src, final NurTagTrackingConfig dest) {
        if (src.complexFilters != null && src.complexFilters.length > 0) {
            final int n = src.complexFilters.length;
            dest.complexFilters = new NurInventoryExtendedFilter[n];
            for (int i = 0; i < n; ++i) {
                this.copySingleInventoryFilter(src.complexFilters[i], dest.complexFilters[i]);
            }
        }
        else {
            dest.complexFilters = null;
        }
    }
    
    private void copySingleInventoryExParams(final NurInventoryExtended src, final NurInventoryExtended dest) {
        dest.inventorySelState = src.inventorySelState;
        dest.inventoryTarget = src.inventoryTarget;
        dest.Q = src.Q;
        dest.rounds = src.rounds;
        dest.session = src.session;
        dest.transitTime = src.transitTime;
    }
    
    private void copyInventoryParams(final NurTagTrackingConfig src, final NurTagTrackingConfig dest) {
        if (src.complexFilterParams != null && dest.complexFilterParams != null) {
            this.copySingleInventoryExParams(src.complexFilterParams, dest.complexFilterParams);
        }
        else {
            dest.complexFilterParams = null;
        }
    }
    
    private void copyTTConfig(final NurTagTrackingConfig newConfig, final boolean isValid) {
        if (this.mTTConfig == null) {
            this.mTTConfig = new NurTagTrackingConfig();
        }
        this.mTTConfig.flags = newConfig.flags;
        this.mTTConfig.events = newConfig.events;
        this.mTTConfig.rssiDeltaFilter = newConfig.rssiDeltaFilter;
        this.mTTConfig.positionDeltaFilter = newConfig.positionDeltaFilter;
        this.mTTConfig.scanUntilNewTagsCount = newConfig.scanUntilNewTagsCount;
        this.mTTConfig.visibilityTimeout = newConfig.visibilityTimeout;
        this.mTTConfig.selectBank = newConfig.selectBank;
        this.mTTConfig.selectAddress = newConfig.selectAddress;
        this.mTTConfig.selectMaskBitLength = newConfig.selectMaskBitLength;
        if (newConfig.selectMask != null && newConfig.selectMask.length > 0) {
            this.mTTConfig.selectMask = new byte[newConfig.selectMask.length];
            System.arraycopy(newConfig.selectMask, 0, this.mTTConfig.selectMask, 0, newConfig.selectMask.length);
        }
        else {
            this.mTTConfig.selectMask = null;
        }
        this.mTTConfig.inAntennaMask = newConfig.inAntennaMask;
        this.mTTConfig.outAntennaMask = newConfig.outAntennaMask;
        this.copyComplexFilters(newConfig, this.mTTConfig);
        this.copyInventoryParams(newConfig, this.mTTConfig);
        this.mValidTTConfig = isValid;
    }
    
    private static String[] splitByChar(final String stringToSplit, final char separator, final boolean removeEmpty) {
        final ArrayList<String> strList = new ArrayList<String>();
        final String expression = String.format("\\%c", separator);
        final String[] split;
        final String[] arr = split = stringToSplit.split(expression);
        for (final String s : split) {
            final String tmp = s.trim();
            if (!removeEmpty || !tmp.isEmpty()) {
                strList.add(tmp);
            }
        }
        return strList.toArray(new String[0]);
    }
    
    private static String extractFwInfoField(final String sourceString, final String field) {
        String fieldValue = null;
        if (sourceString == null || field == null || field.isEmpty()) {
            return null;
        }
        int fieldIndex = sourceString.indexOf(field + "=");
        if (fieldIndex >= 0) {
            fieldValue = sourceString.substring(fieldIndex);
            fieldIndex = fieldValue.indexOf(";");
            if (fieldIndex >= 0) {
                fieldValue = fieldValue.substring(0, fieldIndex);
                final String[] split = splitByChar(fieldValue, '=', true);
                if (split != null && split.length == 2) {
                    fieldValue = split[1];
                }
            }
        }
        return fieldValue;
    }
    
    private boolean byteCompare(final byte[] a, final int offset, final byte[] b) {
        if (a != null && b != null) {
            for (int max = b.length, i = offset, j = 0; i < a.length && j < max; ++i, ++j) {
                if (a[i] != b[j]) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    private int findByteArrayOffset(final byte[] source, final byte[] pat) {
        if (source != null && pat != null && pat.length <= source.length && pat.length > 0) {
            for (int last = source.length - pat.length, offset = 0; offset <= last; ++offset) {
                if (this.byteCompare(source, offset, pat)) {
                    return offset;
                }
            }
        }
        return -1;
    }
    
    private String fwInfoFromBytes(final byte[] binary) throws NurApiException {
        if (binary == null || binary.length < 16384 || binary.length > 180224) {
            throw new NurApiException("FW: invalid binary", 5);
        }
        int startOffset = -1;
        int endOffset = -1;
        final char[] infoStart = "<FWINFO>".toCharArray();
        final char[] infoEnd = "</FWINFO>".toCharArray();
        final int startLen = infoStart.length;
        final int endLen = startLen + 1;
        final byte[] start = new byte[startLen];
        final byte[] end = new byte[endLen];
        int i;
        for (i = 0; i < startLen; ++i) {
            start[i] = (byte)(infoStart[i] & '\u00ff');
            end[i] = (byte)(infoEnd[i] & '\u00ff');
        }
        end[i] = (byte)(infoEnd[i] & '\u00ff');
        startOffset = this.findByteArrayOffset(binary, start);
        endOffset = this.findByteArrayOffset(binary, end);
        if (startOffset < 0 || endOffset < 0 || startOffset > endOffset) {
            throw new NurApiException("Missing FWINFO in binary");
        }
        startOffset += startLen;
        i = endOffset - startOffset;
        if (i < 20) {
            throw new NurApiException("Invalid FWINFO in binary, size = " + i);
        }
        final byte[] info = new byte[i];
        System.arraycopy(binary, startOffset, info, 0, i);
        return new String(info, 0, i, StandardCharsets.UTF_8);
    }
    
    private static boolean splitVersion(final String strVersion, final NurBinFileType type) {
        int versionNumber = 0;
        int major = 0;
        int minor = 0;
        char build = '\0';
        final String[] arr1 = splitByChar(strVersion, '.', true);
        if (arr1 == null || arr1.length != 2) {
            return false;
        }
        final String[] arr2 = splitByChar(arr1[1], '-', true);
        if (arr2 == null || arr2.length != 2) {
            return false;
        }
        final String strMajor = arr1[0];
        final String strMinor = arr2[0];
        final char[] buildChar = arr2[1].toUpperCase().toCharArray();
        if (buildChar.length != 1 || buildChar[0] < 'A' || buildChar[0] > 'Z') {
            return false;
        }
        try {
            major = Integer.parseInt(strMajor);
            minor = Integer.parseInt(strMinor);
            build = buildChar[0];
        }
        catch (Exception ex) {
            return false;
        }
        if (major < 0 || minor < 0) {
            return false;
        }
        versionNumber = (major & 0xFF);
        versionNumber <<= 8;
        versionNumber |= (minor & 0xFF);
        versionNumber <<= 8;
        versionNumber |= (build & '\u00ff');
        type.versionMajor = major;
        type.versionMinor = minor;
        type.build = build;
        type.versionNumber = versionNumber;
        return true;
    }
    
    private NurBinFileType binTypeFromFwInfo(String fwInfo, final int sizeOfBinary, final String desiredModuleType) throws NurApiException {
        final NurBinFileType type = new NurBinFileType();
        String fieldValue = "";
        if (fwInfo == null || fwInfo.isEmpty()) {
            throw new NurApiException("FW error: information is empty or null");
        }
        fwInfo = fwInfo.toUpperCase();
        fieldValue = extractFwInfoField(fwInfo, "MODULE");
        try {
            if (fieldValue == null || !fieldValue.contains(desiredModuleType.toUpperCase())) {
                throw new NurApiException("FW error: L2 identifier not found", 5);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new NurApiException("Failed to check NUR model name", 16);
        }
        fieldValue = extractFwInfoField(fwInfo, "TYPE");
        if (fieldValue != null && fieldValue.equalsIgnoreCase("LOADER")) {
            type.fwType = 1;
        }
        else if (fieldValue != null && fieldValue.equalsIgnoreCase("APP")) {
            type.fwType = 2;
        }
        if (type.fwType == 0) {
            throw new NurApiException("FW error: cannot detect type (got \"" + fieldValue + "\")", 5);
        }
        fieldValue = extractFwInfoField(fwInfo, "VER");
        if (fieldValue == null) {
            throw new NurApiException("FW error: version information not extracted");
        }
        if (!splitVersion(fieldValue, type)) {
            throw new NurApiException("FW error: version was invalid");
        }
        return type;
    }
    
    public NurBinFileType checkNurFwBinary(final byte[] nurFwBinary, final String desiredModuleType) throws NurApiException {
        NurBinFileType type = null;
        String fwInfo = null;
        fwInfo = this.fwInfoFromBytes(nurFwBinary);
        type = this.binTypeFromFwInfo(fwInfo, nurFwBinary.length, desiredModuleType);
        return type;
    }
    
    public NurBinFileType checkNurFwBinaryFile(final String fileName, final String desiredModuleType) throws NurApiException {
        int szBinBuffer = 0;
        byte[] fwBinary = null;
        File file = null;
        FileInputStream fileInputStream = null;
        boolean invalidFile = true;
        try {
            file = new File(fileName);
            fileInputStream = new FileInputStream(file);
            final long fileLength = file.length();
            if (fileLength >= 16384L && fileLength <= 180224L) {
                szBinBuffer = (int)fileLength;
                fwBinary = new byte[szBinBuffer];
                fileInputStream.read(fwBinary);
                invalidFile = false;
            }
            fileInputStream.close();
        }
        catch (Exception ex) {
            this.ELog("FW check error " + ex.getMessage());
        }
        if (invalidFile) {
            throw new NurApiException("checkBinaryFile: invalid file.");
        }
        return this.checkNurFwBinary(fwBinary, desiredModuleType);
    }
    
    public NurRespDiagConfig getDiagConfig() throws Exception {
        return ((NurCmdDiagConfig)this.exchangeCommand(new NurCmdDiagConfig())).getResponse();
    }
    
    public void setDiagConfig(final int flags, final int interval) throws Exception {
        this.exchangeCommand(new NurCmdDiagConfig(flags, interval));
    }
    
    public NurRespDiagGetReport getDiagReport(final int flags) throws Exception {
        return ((NurCmdDiagGetReport)this.exchangeCommand(new NurCmdDiagGetReport(flags))).getResponse();
    }
    
    static {
        BeamCoords = new float[][] { { 0.05f, 0.05f }, { 0.5f, 0.0f }, { 0.95f, 0.05f }, { 0.0f, 0.5f }, { 0.5f, 0.5f }, { 1.0f, 0.5f }, { 0.05f, 0.95f }, { 0.5f, 1.0f }, { 0.95f, 0.95f }, { 1.1f, 1.1f } };
        BeamNumbers = new int[] { 2, 1, 0, 5, 4, 3, 8, 7, 6, 9 };
        SectorPos = new float[][] { { 0.666f, 0.0f }, { 0.333f, 0.0f }, { 0.0f, 0.0f }, { 0.666f, 0.333f }, { 0.333f, 0.333f }, { 0.0f, 0.333f }, { 0.666f, 0.666f }, { 0.333f, 0.666f }, { 0.0f, 0.666f }, { 1.099f, 1.099f } };
        ackBuf = new byte[] { -91, 3, 0, 0, 0, 89, 2, -78, -63 };
    }
    
    private class AsyncNotification
    {
        NurCmd cmd;
        Runnable runFunc;
        
        AsyncNotification(final NurCmd cmd, final Runnable runFunc) {
            this.cmd = null;
            this.runFunc = null;
            this.cmd = cmd;
            this.runFunc = runFunc;
        }
    }
    
    private class NurUnsolicitedRunnable implements Runnable
    {
        private NurCmd cmd;
        private NurEventInventory eventInventory;
        private NurEventNxpAlarm eventNxpAlarm;
        private NurEventEpcEnum eventEpcEnum;
        private NurEventAutotune eventAutotune;
        
        public NurUnsolicitedRunnable(final NurCmd cmd) {
            this.eventInventory = new NurEventInventory();
            this.eventNxpAlarm = new NurEventNxpAlarm();
            this.eventEpcEnum = new NurEventEpcEnum();
            this.eventAutotune = new NurEventAutotune();
            this.cmd = cmd;
        }
        
        @Override
        public void run() {
            boolean tryUnknown = false;
            switch (this.cmd.getCommand()) {
                case 128: {
                    final NurCmdNotifyBoot bootEvt = (NurCmdNotifyBoot)this.cmd;
                    NurApi.this.VLog("NurUnsolicitedRunnable() NurCmdNotifyBoot; needBootSetup " + bootEvt.needBootSetup);
                    if (bootEvt.needBootSetup) {
                        final String bootStr = bootEvt.getResponse();
                        final boolean app = bootStr.compareToIgnoreCase("app") == 0;
                        synchronized (NurApi.this) {
                            NurApi.this.bootSetup(app);
                        }
                    }
                    if (NurApi.this.mListener != null) {
                        NurApi.this.mListener.bootEvent(bootEvt.getResponse());
                    }
                    break;
                }
                case 129: {
                    if (NurApi.this.mListener != null) {
                        NurApi.this.mListener.IOChangeEvent(((NurCmdNotifyIOChange)this.cmd).getResponse());
                        break;
                    }
                    break;
                }
                case 130: {
                    this.eventInventory = ((NurCmdNotifyInventory)this.cmd).getResponse();
                    NurApi.this.mInventoryStreamRunning = (NurApi.this.mInventoryStreamRunning && !this.eventInventory.stopped);
                    if (NurApi.this.mListener != null) {
                        NurApi.this.mListener.inventoryStreamEvent(this.eventInventory);
                        break;
                    }
                    break;
                }
                case 132: {
                    if (NurApi.this.mListener != null) {
                        NurApi.this.mListener.traceTagEvent(((NurCmdNotifyTraceTag)this.cmd).getResponse());
                        break;
                    }
                    break;
                }
                case 133: {
                    if (NurApi.this.mListener != null) {
                        NurApi.this.mListener.triggeredReadEvent(((NurCmdNotifyTriggeredRead)this.cmd).getResponse());
                        break;
                    }
                    break;
                }
                case 134: {
                    if (NurApi.this.mListener != null) {
                        NurApi.this.mListener.frequencyHopEvent(((NurCmdNotifyFrequencyHop)this.cmd).getResponse());
                        break;
                    }
                    break;
                }
                case 135: {
                    if (NurApi.this.mListener != null) {
                        NurApi.this.mListener.debugMessageEvent(((NurCmdNotifyDebugMsg)this.cmd).getResponse());
                        break;
                    }
                    break;
                }
                case 136: {
                    this.eventInventory = ((NurCmdNotifyInventoryEx)this.cmd).getResponse();
                    NurApi.this.mInventoryExtendedStreamRunning = (NurApi.this.mInventoryExtendedStreamRunning && !this.eventInventory.stopped);
                    if (NurApi.this.mListener != null) {
                        NurApi.this.mListener.inventoryExtendedStreamEvent(this.eventInventory);
                        break;
                    }
                    break;
                }
                case 137: {
                    this.eventNxpAlarm = ((NurCmdNotifyNxpAlarm)this.cmd).getResponse();
                    NurApi.this.mNxpAlarmStreamRunning = (NurApi.this.mNxpAlarmStreamRunning && !this.eventNxpAlarm.stopped);
                    if (NurApi.this.mListener != null) {
                        NurApi.this.mListener.nxpEasAlarmEvent(this.eventNxpAlarm);
                        break;
                    }
                    break;
                }
                case 138: {
                    this.eventEpcEnum = ((NurCmdNotifyEpcEnum)this.cmd).getResponse();
                    if (NurApi.this.mListener != null) {
                        NurApi.this.mListener.epcEnumEvent(this.eventEpcEnum);
                        break;
                    }
                    break;
                }
                case 141: {
                    this.eventAutotune = ((NurCmdNotifyAutotune)this.cmd).getResponse();
                    if (NurApi.this.mListener != null) {
                        NurApi.this.mListener.autotuneEvent(this.eventAutotune);
                        break;
                    }
                    break;
                }
                case 143: {
                    if (NurApi.this.mDiagReportListener != null) {
                        NurApi.this.mDiagReportListener.diagReportEvent(((NurCmdNotifyDiagReport)this.cmd).getResponse());
                        break;
                    }
                    break;
                }
                case 144: {
                    if (NurApi.this.mAccessoryEventListener != null) {
                        NurApi.this.mAccessoryEventListener.accessoryEvent(((NurCmdNotifyAccessory)this.cmd).getResponse());
                        break;
                    }
                    tryUnknown = true;
                    break;
                }
                default: {
                    tryUnknown = true;
                    break;
                }
            }
            if (tryUnknown && NurApi.this.mUnknownEventListeners.size() > 0) {
                try {
                    final NurEventUnknown theEvent = ((NurCmdUnknownEvent)this.cmd).getResponse();
                    final Iterator<NurApiUnknownEventListener> iter = (Iterator<NurApiUnknownEventListener>)NurApi.this.mUnknownEventListeners.iterator();
                    while (iter.hasNext()) {
                        iter.next().handleUnknownEvent(theEvent);
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public static class XPCSpec
    {
        public int xpc_w1;
        public int xpc_w2;
        public byte[] modifiedEpc;
        
        public XPCSpec() {
            this.xpc_w1 = 0;
            this.xpc_w2 = 0;
            this.modifiedEpc = null;
        }
    }
}
