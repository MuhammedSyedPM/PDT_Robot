// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

import java.io.IOException;
import java.util.Set;
import java.net.SocketException;
import java.util.Iterator;
import java.util.Enumeration;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.DatagramSocket;

public class SendBroadcastMessage implements Runnable
{
    public static final int BROADCAST_PORT = 4331;
    private static final int BUFLEN = 512;
    private static final int RESP_DEV_INFO = 1;
    private static final int RESP_ACK = 4;
    private static final int DEFAULT_TMO_MILLIS = 2500;
    private static int mTimeoutMillis;
    private NurApi mApi;
    private boolean mInitDone;
    private static byte[] mBuffer;
    private static byte[] mRBuffer;
    private static byte[] mFDataTemp;
    private static DatagramSocket mSocket;
    private static InetAddress mBroadcast;
    private static DatagramPacket mSendPacket;
    private static DatagramPacket mRcvPacket;
    private NurBroadcastMessage mNurBroadcast;
    private int mAnswers;
    private boolean mDoEvents;
    private ArrayList<NurEthConfig> mResponses;
    
    public int getAnswerCount() {
        return this.mAnswers;
    }
    
    public ArrayList<NurEthConfig> getEthResponses() {
        return this.mResponses;
    }
    
    public void init(final boolean receiveEvents) throws Exception {
        this.mDoEvents = receiveEvents;
        this.init();
    }
    
    public void init() throws Exception {
        int packetOffset = 0;
        packetOffset += NurPacket.PacketByte(SendBroadcastMessage.mBuffer, packetOffset, this.mNurBroadcast.bc_tag);
        packetOffset += NurPacket.PacketByte(SendBroadcastMessage.mBuffer, packetOffset, this.mNurBroadcast.bc_devtype);
        packetOffset += NurPacket.PacketByte(SendBroadcastMessage.mBuffer, packetOffset, this.mNurBroadcast.bc_company);
        packetOffset += NurPacket.PacketByte(SendBroadcastMessage.mBuffer, packetOffset, this.mNurBroadcast.f_type);
        packetOffset += NurPacket.PacketByte(SendBroadcastMessage.mBuffer, packetOffset, this.mNurBroadcast.f_op);
        if (this.mNurBroadcast.f_data != null) {
            for (int i = 0; i < this.mNurBroadcast.f_size; ++i) {
                SendBroadcastMessage.mFDataTemp[i] = this.mNurBroadcast.f_data[i];
            }
        }
        packetOffset += NurPacket.PacketBytes(SendBroadcastMessage.mBuffer, packetOffset, SendBroadcastMessage.mFDataTemp, 16);
        packetOffset += NurPacket.PacketByte(SendBroadcastMessage.mBuffer, packetOffset, this.mNurBroadcast.bc_cmd);
        packetOffset += NurPacket.PacketByte(SendBroadcastMessage.mBuffer, packetOffset, this.mNurBroadcast.bc_len);
        final int iMax;
        packetOffset = (iMax = packetOffset + NurPacket.PacketBytes(SendBroadcastMessage.mBuffer, packetOffset, this.mNurBroadcast.bc_data, this.mNurBroadcast.bc_len));
        SendBroadcastMessage.mBuffer[iMax] = (byte)(iMax & 0xFF);
        for (int i = 0; i < iMax; ++i) {
            final byte[] mBuffer = SendBroadcastMessage.mBuffer;
            final int n = iMax;
            mBuffer[n] += SendBroadcastMessage.mBuffer[i];
        }
        SendBroadcastMessage.mSocket = null;
        final byte[] bcip = new byte[4];
        try {
            final String[] arr = this.mApi.getBroadcastAddress().split(".");
            for (int j = 0; j < 4; ++j) {
                bcip[j] = (byte)Integer.parseInt(arr[j]);
            }
        }
        catch (Exception e) {
            bcip[1] = (bcip[0] = -1);
            bcip[3] = (bcip[2] = -1);
        }
        SendBroadcastMessage.mBroadcast = null;
        SendBroadcastMessage.mBroadcast = InetAddress.getByAddress(bcip);
        (SendBroadcastMessage.mSocket = new DatagramSocket()).setBroadcast(true);
        SendBroadcastMessage.mSendPacket = new DatagramPacket(SendBroadcastMessage.mBuffer, iMax + 1, SendBroadcastMessage.mBroadcast, 4331);
        SendBroadcastMessage.mRcvPacket = new DatagramPacket(SendBroadcastMessage.mRBuffer, SendBroadcastMessage.mRBuffer.length);
        this.mInitDone = true;
    }
    
    public SendBroadcastMessage(final NurApi api, final NurBroadcastMessage nurBroadcast) {
        this.mInitDone = false;
        this.mAnswers = 0;
        this.mDoEvents = false;
        this.mResponses = new ArrayList<NurEthConfig>();
        this.mNurBroadcast = nurBroadcast;
        this.mApi = api;
    }
    
    void sendToAllInterfaces(final DatagramSocket socket, final byte[] data, final int actLen, final int port) throws SocketException {
        final Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            final NetworkInterface networkInterface = interfaces.nextElement();
            if (!networkInterface.isLoopback()) {
                if (!networkInterface.isUp()) {
                    continue;
                }
                for (final InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                    final InetAddress broadcast = interfaceAddress.getBroadcast();
                    if (broadcast == null) {
                        continue;
                    }
                    try {
                        final DatagramPacket sendPacket = new DatagramPacket(data, actLen, broadcast, port);
                        socket.send(sendPacket);
                    }
                    catch (Exception ex) {}
                }
            }
        }
    }
    
    void AddResponse(final NurEventDeviceInfo info) {
        boolean found = false;
        for (int i = 0; i < this.mResponses.size(); ++i) {
            final NurEthConfig cfg = this.mResponses.get(i);
            if (cfg.mac.equals(info.eth.mac)) {
                found = true;
                break;
            }
        }
        if (!found) {
            if (this.mDoEvents) {
                this.mApi.sendDeviceSearchEvent(info);
            }
            this.mResponses.add(info.eth);
        }
    }
    
    void GetMDNSDevices(final InetAddress broadcast) {
        final Service service = Service.fromName("_nur._tcp.");
        final Query query = Query.createFor(service, Domain.LOCAL);
        try {
            final Set<Instance> instances = query.runOnceOn(broadcast);
            for (final Instance item : instances) {
                final Set<InetAddress> addresses = item.getAddresses();
                for (final InetAddress addr : addresses) {
                    String ip = addr.toString();
                    ip = ip.replace("/", "");
                    if (ip.length() <= 15) {
                        final NurEventDeviceInfo einfo = new NurEventDeviceInfo();
                        einfo.eth.title = item.getName();
                        einfo.eth.serverPort = item.getPort();
                        einfo.eth.ip = ip;
                        if (item.hasAttribute("MAC")) {
                            einfo.eth.mac = item.lookupAttribute("MAC");
                        }
                        if (item.hasAttribute("VERSION")) {
                            einfo.eth.version = Integer.parseInt(item.lookupAttribute("VERSION"));
                        }
                        if (item.hasAttribute("ADDRTYPE")) {
                            einfo.eth.addrType = Integer.parseInt(item.lookupAttribute("ADDRTYPE"));
                        }
                        if (item.hasAttribute("CONNSTAT")) {
                            einfo.status = Integer.parseInt(item.lookupAttribute("CONNSTAT"));
                        }
                        if (item.hasAttribute("TYPE")) {
                            final String txt = "wlan";
                            if (txt.equals(item.lookupAttribute("TYPE").toLowerCase())) {
                                einfo.eth.transport = 2;
                            }
                        }
                        this.AddResponse(einfo);
                    }
                }
            }
        }
        catch (Exception e) {
            this.mApi.ELog("GetMDNSDevices: " + e.getMessage());
        }
    }
    
    @Override
    public void run() {
        if (!this.mInitDone) {
            return;
        }
        String vendor = "";
        try {
            vendor = System.getProperty("java.specification.vendor").toLowerCase();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        this.mApi.VLog("Java vendor: " + vendor);
        final boolean android = vendor.contains("android");
        if (!android) {
            try {
                final Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
                while (interfaces.hasMoreElements()) {
                    final NetworkInterface networkInterface = interfaces.nextElement();
                    if (!networkInterface.isLoopback()) {
                        if (!networkInterface.isUp()) {
                            continue;
                        }
                        for (final InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                            final InetAddress broadcast = interfaceAddress.getAddress();
                            if (broadcast == null) {
                                continue;
                            }
                            final String ip = broadcast.toString();
                            if (ip.length() >= 16) {
                                continue;
                            }
                            this.GetMDNSDevices(broadcast);
                        }
                    }
                }
            }
            catch (Exception e2) {
                this.mApi.ELog(this.getClass().toString() + "MDNS fail. " + e2.getMessage());
            }
        }
        try {
            this.sendToAllInterfaces(SendBroadcastMessage.mSocket, SendBroadcastMessage.mSendPacket.getData(), SendBroadcastMessage.mSendPacket.getLength(), 4331);
        }
        catch (Exception e2) {
            this.mApi.ELog(this.getClass().toString() + ": sending failed. " + e2.getMessage());
            return;
        }
        try {
            SendBroadcastMessage.mSocket.setSoTimeout(SendBroadcastMessage.mTimeoutMillis);
        }
        catch (SocketException e3) {
            e3.printStackTrace();
        }
        final long endTimeMillis = System.currentTimeMillis() + SendBroadcastMessage.mTimeoutMillis;
        while (!this.mApi.isDisposed()) {
            final boolean timedOut = System.currentTimeMillis() > endTimeMillis;
            if (timedOut) {
                break;
            }
            try {
                SendBroadcastMessage.mSocket.receive(SendBroadcastMessage.mRcvPacket);
            }
            catch (IOException e4) {
                break;
            }
            final int totalBytesReceived = SendBroadcastMessage.mRcvPacket.getLength();
            if (totalBytesReceived < 5) {
                this.mApi.VLog("SendBroadcastMessage, total bytes received: " + totalBytesReceived + ".");
            }
            else {
                final byte[] recvBuf = SendBroadcastMessage.mRcvPacket.getData();
                if ((recvBuf[0] & 0xFF) != 0xFE || (recvBuf[1] & 0xFF) != 0x2 || (recvBuf[2] & 0xFF) != 0xA1) {
                    this.mApi.VLog(String.format("SendBroadcastMessage, Dev search header mismatch %02X %02X %02X %02X %02X", recvBuf[0], recvBuf[1], recvBuf[2], recvBuf[3], recvBuf[4]));
                }
                else {
                    final int iMax = (recvBuf[4] & 0xFF) + 5;
                    int crc = iMax & 0xFF;
                    for (int i = 0; i < iMax; ++i) {
                        crc = (crc + (recvBuf[i] & 0xFF) & 0xFFFF);
                    }
                    if ((crc & 0xFF) != (recvBuf[iMax] & 0xFF)) {
                        this.mApi.VLog("SendBroadcastMessage, mBroadcast answer CRC error.");
                    }
                    else if ((recvBuf[3] & 0xFF) == 0x1) {
                        ++this.mAnswers;
                        final NurEventDeviceInfo info = new NurEventDeviceInfo();
                        int packetOffset = 5;
                        info.eth.transport = 1;
                        info.eth.title = NurPacket.BytesToString(recvBuf, packetOffset, 32);
                        packetOffset += 32;
                        info.eth.ip = String.format("%d.%d.%d.%d", recvBuf[packetOffset++] & 0xFF, recvBuf[packetOffset++] & 0xFF, recvBuf[packetOffset++] & 0xFF, recvBuf[packetOffset++] & 0xFF);
                        info.eth.mask = String.format("%d.%d.%d.%d", recvBuf[packetOffset++] & 0xFF, recvBuf[packetOffset++] & 0xFF, recvBuf[packetOffset++] & 0xFF, recvBuf[packetOffset++] & 0xFF);
                        info.eth.gw = String.format("%d.%d.%d.%d", recvBuf[packetOffset++] & 0xFF, recvBuf[packetOffset++] & 0xFF, recvBuf[packetOffset++] & 0xFF, recvBuf[packetOffset++] & 0xFF);
                        info.eth.staticip = String.format("%d.%d.%d.%d", recvBuf[packetOffset++] & 0xFF, recvBuf[packetOffset++] & 0xFF, recvBuf[packetOffset++] & 0xFF, recvBuf[packetOffset++] & 0xFF);
                        info.eth.mac = String.format("%02X:%02X:%02X:%02X:%02X:%02X", recvBuf[packetOffset++], recvBuf[packetOffset++], recvBuf[packetOffset++], recvBuf[packetOffset++], recvBuf[packetOffset++], recvBuf[packetOffset++]);
                        info.eth.version = NurPacket.BytesToByte(recvBuf, packetOffset++);
                        final NurEthConfig eth = info.eth;
                        eth.version <<= 8;
                        final NurEthConfig eth2 = info.eth;
                        eth2.version += NurPacket.BytesToByte(recvBuf, packetOffset++);
                        final NurEthConfig eth3 = info.eth;
                        eth3.version <<= 8;
                        final NurEthConfig eth4 = info.eth;
                        eth4.version += NurPacket.BytesToByte(recvBuf, packetOffset++);
                        final NurEthConfig eth5 = info.eth;
                        eth5.version <<= 8;
                        final NurEthConfig eth6 = info.eth;
                        eth6.version += NurPacket.BytesToByte(recvBuf, packetOffset++);
                        info.eth.serverPort = NurPacket.BytesToByte(recvBuf, packetOffset++);
                        final NurEthConfig eth7 = info.eth;
                        eth7.serverPort <<= 8;
                        final NurEthConfig eth8 = info.eth;
                        eth8.serverPort += NurPacket.BytesToByte(recvBuf, packetOffset++);
                        info.eth.addrType = NurPacket.BytesToByte(recvBuf, packetOffset++);
                        info.status = NurPacket.BytesToByte(recvBuf, packetOffset++);
                        info.boardID = NurPacket.BytesToByte(recvBuf, packetOffset++);
                        info.boardType = NurPacket.BytesToByte(recvBuf, packetOffset++);
                        if (info.boardType == 29) {
                            info.eth.transport = 2;
                        }
                        int temperature = NurPacket.BytesToByte(recvBuf, packetOffset++);
                        temperature <<= 8;
                        temperature += NurPacket.BytesToByte(recvBuf, packetOffset++);
                        info.temp = 147.5 - 75.0 * (3.0 / (1024.0 / (temperature & 0xFFFF)));
                        info.lightADC = NurPacket.BytesToByte(recvBuf, packetOffset++);
                        final NurEventDeviceInfo nurEventDeviceInfo = info;
                        nurEventDeviceInfo.lightADC <<= 8;
                        final NurEventDeviceInfo nurEventDeviceInfo2 = info;
                        nurEventDeviceInfo2.lightADC += NurPacket.BytesToByte(recvBuf, packetOffset++);
                        info.tapADC = NurPacket.BytesToByte(recvBuf, packetOffset++);
                        final NurEventDeviceInfo nurEventDeviceInfo3 = info;
                        nurEventDeviceInfo3.tapADC <<= 8;
                        final NurEventDeviceInfo nurEventDeviceInfo4 = info;
                        nurEventDeviceInfo4.tapADC += NurPacket.BytesToByte(recvBuf, packetOffset++);
                        info.IOstate = NurPacket.BytesToByte(recvBuf, packetOffset++);
                        info.upTime = NurPacket.BytesToByte(recvBuf, packetOffset++);
                        final NurEventDeviceInfo nurEventDeviceInfo5 = info;
                        nurEventDeviceInfo5.upTime <<= 8;
                        final NurEventDeviceInfo nurEventDeviceInfo6 = info;
                        nurEventDeviceInfo6.upTime += NurPacket.BytesToByte(recvBuf, packetOffset++);
                        info.eth.hostMode = NurPacket.BytesToByte(recvBuf, packetOffset++);
                        info.eth.hostip = String.format("%d.%d.%d.%d", recvBuf[packetOffset++] & 0xFF, recvBuf[packetOffset++] & 0xFF, recvBuf[packetOffset++] & 0xFF, recvBuf[packetOffset++] & 0xFF);
                        info.eth.hostPort = NurPacket.BytesToByte(recvBuf, packetOffset++);
                        final NurEthConfig eth9 = info.eth;
                        eth9.hostPort <<= 8;
                        final NurEthConfig eth10 = info.eth;
                        eth10.hostPort += NurPacket.BytesToByte(recvBuf, packetOffset++);
                        info.nurVer = String.format("%d.%d.%c", recvBuf[packetOffset++] & 0xFF, recvBuf[packetOffset++] & 0xFF, recvBuf[packetOffset++]);
                        info.serial = NurPacket.BytesToString(recvBuf, packetOffset, 16);
                        packetOffset += 16;
                        info.altSerial = NurPacket.BytesToString(recvBuf, packetOffset, 16);
                        packetOffset += 16;
                        info.nurName = NurPacket.BytesToString(recvBuf, packetOffset, 16);
                        packetOffset += 16;
                        info.hwVer = NurPacket.BytesToString(recvBuf, packetOffset, 8);
                        packetOffset += 8;
                        info.nurAntNum = NurPacket.BytesToByte(recvBuf, packetOffset++);
                        this.AddResponse(info);
                    }
                    else if ((recvBuf[3] & 0xFF) == 0x4) {
                        ++this.mAnswers;
                        this.mApi.VLog("SendBroadcastMessage, ACK received.");
                    }
                    else {
                        this.mApi.ELog("SendBroadcastMessage, unknown recvBuf[3] = " + recvBuf[3]);
                    }
                }
            }
        }
        try {
            if (SendBroadcastMessage.mSocket != null) {
                SendBroadcastMessage.mSocket.close();
            }
        }
        catch (Exception e2) {
            e2.printStackTrace();
        }
    }
    
    static {
        SendBroadcastMessage.mTimeoutMillis = 2500;
        SendBroadcastMessage.mBuffer = new byte[512];
        SendBroadcastMessage.mRBuffer = new byte[512];
        SendBroadcastMessage.mFDataTemp = new byte[16];
        SendBroadcastMessage.mSocket = null;
        SendBroadcastMessage.mBroadcast = null;
        SendBroadcastMessage.mSendPacket = null;
        SendBroadcastMessage.mRcvPacket = null;
    }
}
