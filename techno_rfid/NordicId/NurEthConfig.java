// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public class NurEthConfig
{
    public static final int TRANSPORT_NUR = 0;
    public static final int TRANSPORT_BROADCAST = 1;
    public static final int ADDRESS_TYPE_DHCP = 0;
    public static final int ADDRESS_TYPE_STATIC = 1;
    public static final int HOST_MODE_SERVER = 0;
    public static final int HOST_MODE_CLIENT = 1;
    public int transport;
    public String title;
    public int version;
    public String ip;
    public String mask;
    public String gw;
    public int addrType;
    public String staticip;
    public String mac;
    public int serverPort;
    public int hostMode;
    public String hostip;
    public int hostPort;
    public byte[] reserved;
    
    public NurEthConfig() {
        this.title = "";
        this.ip = "";
        this.mask = "";
        this.gw = "";
        this.staticip = "";
        this.mac = "";
        this.hostip = "";
        this.reserved = new byte[8];
    }
    
    public NurEthConfig(final int transport, final String title, final String mask, final String gw, final int addrType, final String staticip, final String mac, final int serverPort, final int hostmode, final String hostip, final int hostPort) {
        this.title = "";
        this.ip = "";
        this.mask = "";
        this.gw = "";
        this.staticip = "";
        this.mac = "";
        this.hostip = "";
        this.reserved = new byte[8];
        this.transport = transport;
        this.title = title;
        this.mask = mask;
        this.gw = gw;
        this.addrType = addrType;
        this.staticip = staticip;
        this.mac = mac;
        this.serverPort = serverPort;
        this.hostMode = hostmode;
        this.hostip = hostip;
        this.hostPort = hostPort;
    }
}
