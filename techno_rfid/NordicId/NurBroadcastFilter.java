// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurBroadcastFilter
{
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
    public int filterType;
    public int filterOp;
    public byte[] filterData;
    public int filterSize;
    
    public NurBroadcastFilter() {
        this.filterData = new byte[16];
    }
    
    public void createMacFilter(final String mac, final int filterOperation) {
        this.filterType = 1;
        this.filterOp = filterOperation;
        final String str = mac.replaceAll("[^a-fA-F0-9]", "");
        this.filterSize = str.length();
        for (int i = 0; i < this.filterSize; ++i) {
            this.filterData[i] = (byte)str.charAt(i);
        }
    }
    
    public void createIPFilter(final String ip, final int filterOperation) {
        this.filterType = 2;
        this.filterOp = filterOperation;
        final String str = ip.replaceAll("[^0-9]", "");
        this.filterSize = str.length();
        for (int i = 0; i < this.filterSize; ++i) {
            this.filterData[i] = (byte)str.charAt(i);
        }
    }
    
    public void createVersionFilter(final int version, final int filterOperation) {
        this.filterType = 3;
        this.filterOp = filterOperation;
        this.filterData = Integer.toString(version).getBytes();
        this.filterSize = this.filterData.length;
    }
    
    public void createServerPortFilter(final int serverPort, final int filterOperation) {
        this.filterType = 4;
        this.filterOp = filterOperation;
        this.filterData = Integer.toString(serverPort).getBytes();
        this.filterSize = this.filterData.length;
    }
    
    public void createAddressTypeFilter(final boolean usesDHCPAddressing, final int filterOperation) {
        this.filterType = 5;
        this.filterOp = filterOperation;
        this.filterData[0] = (byte)(usesDHCPAddressing ? 48 : 49);
        this.filterSize = 1;
    }
    
    public void createHostModeFilter(final boolean isClient, final int filterOperation) {
        this.filterType = 6;
        this.filterOp = filterOperation;
        this.filterData[0] = (byte)(isClient ? 49 : 48);
        this.filterSize = 1;
    }
    
    public void createNurVersionFilter(final String nurVersion, final int filterOperation) {
        this.filterType = 7;
        this.filterOp = filterOperation;
        final String str = nurVersion.replaceAll("[^a-zA-Z0-9]", "");
        this.filterSize = str.length();
        for (int i = 0; i < this.filterSize; ++i) {
            this.filterData[i] = (byte)str.charAt(i);
        }
    }
    
    public void createStatusFilter(final boolean isConnected, final int filterOperation) {
        this.filterType = 8;
        this.filterOp = filterOperation;
        this.filterData[0] = (byte)(isConnected ? 49 : 48);
        this.filterSize = 1;
    }
    
    public void createNameFilter(final String name, final int filterOperation) {
        this.filterType = 9;
        this.filterOp = filterOperation;
        this.filterData = name.getBytes();
        this.filterSize = ((this.filterData.length <= 16) ? this.filterData.length : 16);
    }
}
