// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

import java.util.ArrayList;
import java.nio.charset.StandardCharsets;

public class AccessoryExtension
{
    public static final String TAG = "AccessoryExtension";
    private NurApi mApi;
    private AccBarcodeResultListener mBarcodeResultListener;
    private AccSensorEventListener mSensorEventListener;
    public static final int BARCODE_READER_NOT_PRESENT_ERROR = 13;
    public static final int TRIGGER_SOURCE = 100;
    public static final int INTVALUE_NOT_VALID = -1;
    public static final short SHORTVALUE_NOT_VALID = -1;
    public static final int NUR_CMD_ACC_EXT = 85;
    protected static final byte BLE_EXT_PRODUCTION = 99;
    public static final int ACC_EXT_GET_FWVERSION = 0;
    public static final int ACC_EXT_RESTART = 5;
    public static final byte RESET_BOOTLOADER_DFU_START = -79;
    public static final byte RESET_POWEROFF = -15;
    public static final int ACC_EXT_READ_BARCODE_ASYNC = 6;
    public static final int ACC_EXT_SET_LED_OP = 7;
    public static final int ACC_EXT_BEEP_ASYNC = 8;
    public static final int ACC_EXT_GET_BATT_INFO = 9;
    public static final int ACC_EXT_IMAGER = 13;
    public static final int ACC_EXT_IMAGER_CMD = 4;
    public static final int ACC_EXT_IMAGER_POWER = 5;
    public static final int ACC_EXT_IMAGER_AIM = 6;
    public static final int ACC_EXT_GET_HEALTHSTATE = 11;
    public static final int ACC_EXT_WIRELESS_CHARGE = 12;
    public static final int ACC_EXT_VIBRATE = 14;
    public static final int ACC_EXT_CLEAR_PAIRS = 15;
    public static final int BLE_EXT_GET_MODEL_INFORMATION = 16;
    public static final int ACC_EXT_GET_CONNECTION_INFO = 18;
    public static final int SENSOR_CONFIG_WIRE_SIZE = 5;
    public static final int SENSOR_FILTER_WIRE_SIZE = 10;
    public static final int ACC_EXT_SENSOR_ENUMERATE = 19;
    public static final int ACC_EXT_SENSOR_SET_CONFIG = 20;
    public static final int ACC_EXT_SENSOR_GET_CONFIG = 21;
    public static final int ACC_EXT_SENSOR_SET_FILTER = 22;
    public static final int ACC_EXT_SENSOR_GET_FILTER = 23;
    public static final int ACC_EXT_SENSOR_GET_VALUE = 24;
    public static final int ACC_EXT_SET_BLE_PASSKEY = 25;
    public static final int ACC_EXT_GET_BLE_PASSKEY = 26;
    public static final int ACC_EXT_CLEAR_BLE_PASSKEY = 27;
    public static final int BATT_GOOD_mV = 3900;
    public static final int BATT_MODERATE_mV = 3700;
    public static final int BATT_LOW_mV = 3500;
    private String mBarcodeCharSet;
    AccEventListener mAccessoryEventListener;
    
    public AccessoryExtension(final NurApi api) {
        this.mApi = null;
        this.mBarcodeCharSet = "UTF-8";
        this.mAccessoryEventListener = new AccEventListener() {
            @Override
            public void accessoryEvent(final NurEventAccessory event) {
                if (event.type == ACC_EVENT_TYPE.Barcode) {
                    if (AccessoryExtension.this.mBarcodeResultListener != null) {
                        final AccBarcodeResult br = new AccBarcodeResult();
                        br.status = (event.source & 0xFF);
                        if (AccessoryExtension.this.interpretEventData(event.dataBuffer, br)) {
                            AccessoryExtension.this.mBarcodeResultListener.onBarcodeResult(br);
                        }
                    }
                }
                else if (event.type == ACC_EVENT_TYPE.SensorChanged) {
                    if (AccessoryExtension.this.mSensorEventListener != null) {
                        final AccSensorChanged ach = new AccSensorChanged();
                        ach.source = ACC_SENSOR_SOURCE.valueOf(event.source & 0xFF);
                        ach.removed = event.dataBuffer[1];
                        AccessoryExtension.this.mSensorEventListener.onSensorChanged(ach);
                    }
                }
                else if (event.type == ACC_EVENT_TYPE.SensorRangeData && AccessoryExtension.this.mSensorEventListener != null) {
                    final AccSensorRangeData acr = new AccSensorRangeData();
                    acr.source = ACC_SENSOR_SOURCE.valueOf(event.source & 0xFF);
                    acr.range = NurPacket.BytesToDword(event.dataBuffer, 0);
                    AccessoryExtension.this.mSensorEventListener.onRangeData(acr);
                }
            }
        };
        (this.mApi = api).setAccessoryEventListener(this.mAccessoryEventListener);
    }
    
    public void setNurApi(final NurApi api) {
        this.mApi = api;
    }
    
    public void setBarcodeDecodingScheme(final String charsetToUse) {
        this.mBarcodeCharSet = charsetToUse;
    }
    
    public String getBarcodeDecodingScheme() {
        return this.mBarcodeCharSet;
    }
    
    public void restartBLEModule() throws Exception {
        this.doCustomCommand(new byte[] { 5 });
    }
    
    public void restartBLEModuleToDFU() throws Exception {
        this.doCustomCommand(new byte[] { 5, -79 });
    }
    
    public void powerDown() throws Exception {
        this.doCustomCommand(new byte[] { 5, -15 });
    }
    
    public void TOFCalibration(final byte mode, final byte calDistanceMillimeter) throws Exception {
        final byte[] payload = { 99, -64, -25, -27, -110, 7, 0, 0 };
        payload[6] = mode;
        payload[7] = calDistanceMillimeter;
        this.doCustomCommand(payload);
    }
    
    public void registerBarcodeResultListener(final AccBarcodeResultListener newListener) {
        this.mBarcodeResultListener = newListener;
    }
    
    public void registerSensorEventListener(final AccSensorEventListener newListener) {
        this.mSensorEventListener = newListener;
    }
    
    public boolean isNotSupportedError(final int error) {
        return error == 13;
    }
    
    public AccConfig getConfig() throws Exception {
        final byte[] payload = AccConfig.getQueryCommand();
        byte[] reply = null;
        reply = this.mApi.customCmd(85, payload);
        return AccConfig.deserializeConfigurationReply(reply);
    }
    
    public void setConfig(final AccConfig cfg) throws Exception {
        final byte[] payload = AccConfig.serializeConfiguration(cfg);
        this.mApi.customCmd(85, payload);
    }
    
    public String getBattState() throws Exception {
        final int volt = this.getBattVoltage();
        if (volt > 3900) {
            return "Good";
        }
        if (volt > 3700) {
            return "Moderate";
        }
        if (volt > 3500) {
            return "Low";
        }
        return "Critical";
    }
    
    public int getBattVoltage() throws Exception {
        final byte[] payload = { 3 };
        final byte[] resp = this.mApi.customCmd(85, payload);
        return NurPacket.BytesToWord(resp, 0);
    }
    
    public AccBattery getBatteryInfo() throws Exception {
        final byte[] payload = AccBattery.getQueryCommand();
        final byte[] reply = this.doCustomCommand(payload);
        return AccBattery.deserializeBatteryReply(reply);
    }
    
    private byte[] doCustomCommand(final byte[] commandParameters) throws Exception {
        if (this.mApi == null) {
            throw new NurApiException("Accessory extension: API is invalid");
        }
        return this.mApi.customCmd(85, commandParameters);
    }
    
    public boolean isSupported() {
        try {
            this.getFwVersion().getFullApplicationVersion();
        }
        catch (Exception e) {
            return false;
        }
        return true;
    }
    
    public void setLedOpMode(final int mode) throws Exception {
        this.doCustomCommand(new byte[] { 7, (byte)mode });
    }
    
    public void beepAsync(final int timeout) throws Exception {
        final byte[] payload = { 8, 0, 0 };
        NurPacket.PacketWord(payload, 1, timeout);
        this.mApi.customCmd(85, payload);
    }
    
    public void imagerPower(final boolean pwr) throws Exception {
        final byte[] payload = { 13, 5, 0 };
        if (pwr) {
            payload[2] = 1;
        }
        else {
            payload[2] = 0;
        }
        this.mApi.customCmd(85, payload);
    }
    
    public void imagerAIM(final boolean aim) throws Exception {
        final byte[] params = { 13, 6, 0 };
        if (aim) {
            params[2] = 1;
        }
        else {
            params[2] = 0;
        }
        this.mApi.customCmd(85, params);
    }
    
    public byte[] imagerCmd(final String cmd, final ACC_IMAGER_TYPE type) throws Exception {
        int x = 0;
        int len = 0;
        if (type != ACC_IMAGER_TYPE.Opticon) {
            throw new NurApiException("Accessory, imagerCmd: Imager type not supported", 13);
        }
        final byte[] m = this.ConvertMenuToSerial(cmd);
        if (m == null) {
            return null;
        }
        len = m.length + 5;
        final byte[] payload = new byte[len];
        payload[0] = 13;
        payload[1] = 4;
        payload[2] = (byte)(m.length + 2);
        payload[3] = 27;
        payload[len - 1] = 13;
        for (x = 0; x < m.length; ++x) {
            payload[x + 4] = m[x];
        }
        return this.mApi.customCmd(85, payload);
    }
    
    private int GetCmdLength(final byte[] arr, final int pos) {
        int cnt = 0;
        for (int x = 0; x < 5; ++x) {
            if (arr[pos + x] == 64) {
                return cnt;
            }
            ++cnt;
        }
        return cnt;
    }
    
    private byte[] ConvertMenuToSerial(final String txt) {
        int i = 0;
        int dest = 0;
        final byte[] b = txt.getBytes(StandardCharsets.UTF_8);
        final byte[] c = new byte[b.length];
        if (b[0] == 64 && b[1] == 77 && b[2] == 69 && b[3] == 78 && b[4] == 85 && b[5] == 95 && b[6] == 79 && b[7] == 80 && b[8] == 84 && b[9] == 79) {
            for (int x = 10; x < b.length; ++x) {
                if (b[x] == 64) {
                    final int len = this.GetCmdLength(b, x + 1);
                    if (len == 3) {
                        c[dest++] = 91;
                    }
                    else if (len > 3) {
                        break;
                    }
                    for (i = 0; i < len; ++i) {
                        c[dest + i] = b[x + 1 + i];
                    }
                    dest += len;
                }
            }
        }
        dest -= 4;
        if (dest <= 0) {
            return null;
        }
        final byte[] ret = new byte[dest];
        for (i = 0; i < dest; ++i) {
            ret[i] = c[i + 2];
        }
        return ret;
    }
    
    public String[][] getHwHealth() throws NurApiException {
        try {
            final byte[] reply = this.mApi.customCmd(85, new byte[] { 11 });
            if (reply != null && reply.length > 0) {
                final String[] pairs = new String(reply, 0, reply.length, StandardCharsets.US_ASCII).split(";");
                final String[][] result = new String[pairs.length][];
                for (int i = 0; i < result.length; ++i) {
                    result[i] = pairs[i].split("=");
                }
                return result;
            }
        }
        catch (Exception ex) {}
        throw new NurApiException("Accessory, hwHealth: cannot interpret reply or reply missing", 13);
    }
    
    public ACC_WIRELESS_CHARGE_STATUS getWirelessChargeStatus() {
        try {
            final byte[] reply = this.mApi.customCmd(85, new byte[] { 12 });
            return ACC_WIRELESS_CHARGE_STATUS.valueOf(reply[0] & 0xFF);
        }
        catch (NurApiException ne) {
            if (ne.error == 8) {
                return ACC_WIRELESS_CHARGE_STATUS.NotSupported;
            }
        }
        catch (Exception ex) {}
        return ACC_WIRELESS_CHARGE_STATUS.Fail;
    }
    
    public ACC_WIRELESS_CHARGE_STATUS setWirelessCharge(final boolean on) {
        ACC_WIRELESS_CHARGE_STATUS rc = ACC_WIRELESS_CHARGE_STATUS.Off;
        final byte[] payload = { 12, (byte)(on ? 1 : 0) };
        try {
            final byte[] reply = this.mApi.customCmd(85, payload);
            if (reply != null && reply.length > 0) {
                if (reply[0] == 0) {
                    rc = ACC_WIRELESS_CHARGE_STATUS.Off;
                }
                else {
                    rc = ACC_WIRELESS_CHARGE_STATUS.On;
                }
            }
            else {
                rc = ACC_WIRELESS_CHARGE_STATUS.Fail;
            }
        }
        catch (NurApiException ne) {
            if (ne.error == 13) {
                rc = ACC_WIRELESS_CHARGE_STATUS.Refused;
            }
            else if (ne.error == 8) {
                rc = ACC_WIRELESS_CHARGE_STATUS.NotSupported;
            }
            else {
                rc = ACC_WIRELESS_CHARGE_STATUS.Fail;
            }
        }
        catch (Exception ex) {
            rc = ACC_WIRELESS_CHARGE_STATUS.Fail;
        }
        return rc;
    }
    
    public void readBarcodeAsync(final int timeout) throws Exception {
        final byte[] payload = { 6, 0, 0 };
        NurPacket.PacketWord(payload, 1, timeout);
        this.doCustomCommand(payload);
    }
    
    public AccVersionInfo getFwVersion() throws Exception {
        final byte[] reply = this.doCustomCommand(new byte[] { 0 });
        final String strVersion = new String(reply, StandardCharsets.UTF_8);
        return new AccVersionInfo(strVersion);
    }
    
    public static String[] splitByChar(final String stringToSplit, final char separator, final boolean removeEmpty) {
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
    
    public static String[] splitByChar(final String stringToSplit, final char separator) {
        return splitByChar(stringToSplit, separator, true);
    }
    
    public int makeIntegerVersion(final String strVersion) {
        int intVersion = -1;
        if (strVersion == null) {
            return intVersion;
        }
        intVersion = -1;
        final String[] strArr = splitByChar(strVersion, '.');
        if (strArr == null || strArr.length != 3) {
            return intVersion;
        }
        try {
            final int a = Integer.parseInt(strArr[0], 10);
            final int b = Integer.parseInt(strArr[1], 10);
            final int c = Integer.parseInt(strArr[2], 10);
            if (a >= 0 && a <= 255 && b >= 0 && b <= 255 && c >= 0 && c <= 255) {
                intVersion = a;
                intVersion <<= 8;
                intVersion |= b;
                intVersion <<= 8;
                intVersion |= c;
            }
        }
        catch (Exception ex) {}
        return intVersion;
    }
    
    public void cancelBarcodeAsync() throws Exception {
        final byte[] payload = { -1 };
        this.mApi.getTransport().writeData(payload, 1);
    }
    
    public void setBLEPasskey(final String passkey) throws Exception {
        final byte[] payload = new byte[7];
        payload[0] = 25;
        if (passkey.matches("[0-9]+") && passkey.length() == 6) {
            final byte[] arr = passkey.getBytes();
            for (int x = 0; x < 6; ++x) {
                payload[x + 1] = arr[x];
            }
            this.mApi.customCmd(85, payload);
            return;
        }
        throw new Exception("Wrong passkey. 6-digit in ASCII('0' - '9' digits only)");
    }
    
    public String getBLEPasskey() throws Exception {
        final byte[] reply = this.doCustomCommand(new byte[] { 26 });
        return new String(reply, StandardCharsets.UTF_8);
    }
    
    public void clearBLEPasskey() throws Exception {
        this.doCustomCommand(new byte[] { 27 });
    }
    
    public void vibrate(final int length_ms, final int nTimes) throws Exception {
        final byte[] payload = { 14, (byte)nTimes, (byte)(length_ms & 0xFF), (byte)(length_ms >> 8 & 0xFF) };
        this.doCustomCommand(payload);
    }
    
    public void vibrate(final int length_ms) throws Exception {
        this.vibrate(length_ms, 1);
    }
    
    public void clearPairingData() throws Exception {
        this.doCustomCommand(new byte[] { 15 });
    }
    
    public String getConnectionInfo() throws Exception {
        final byte[] reply = this.doCustomCommand(new byte[] { 18 });
        return new String(reply, StandardCharsets.UTF_8);
    }
    
    public String getModelInfo() throws Exception {
        final byte[] reply = this.doCustomCommand(new byte[] { 16 });
        return new String(reply, StandardCharsets.UTF_8);
    }
    
    private boolean interpretEventData(final byte[] data, final AccBarcodeResult result) {
        boolean retry = false;
        if (data != null) {
            try {
                result.strBarcode = new String(data, 0, data.length, this.mBarcodeCharSet);
            }
            catch (Exception e) {
                retry = true;
            }
            if (retry) {
                try {
                    result.strBarcode = new String(data, 0, data.length, StandardCharsets.UTF_8);
                }
                catch (Exception ex) {
                    result.strBarcode = "";
                }
            }
        }
        else {
            result.strBarcode = "";
        }
        return true;
    }
    
    public ArrayList<AccSensorConfig> accSensorEnumerate() throws Exception {
        final ArrayList<AccSensorConfig> sensors = new ArrayList<AccSensorConfig>();
        final AccConfig acfg = this.getConfig();
        if (acfg.isDeviceEXA31() || acfg.isDeviceEXA51()) {
            return sensors;
        }
        final byte[] reply = this.doCustomCommand(new byte[] { 19 });
        final byte sensorConfProtocolSize = reply[0];
        if (sensorConfProtocolSize > 0) {
            for (int i = 1; i + 5 <= reply.length; i += sensorConfProtocolSize) {
                final AccSensorConfig cfg = new AccSensorConfig();
                cfg.deserialize(reply, i);
                sensors.add(cfg);
            }
        }
        return sensors;
    }
    
    public void accSensorSetConfig(final AccSensorConfig cfg) throws Exception {
        final byte[] params = { 20, cfg.source.getByteVal(), cfg.mode };
        this.mApi.customCmd(85, params);
    }
    
    public AccSensorConfig accSensorGetConfig(final ACC_SENSOR_SOURCE source) throws Exception {
        final byte[] params = { 21, source.getByteVal() };
        final byte[] reply = this.doCustomCommand(params);
        if (reply.length < 5) {
            throw new Exception("No proper reply from device:" + reply.length);
        }
        final AccSensorConfig cfg = new AccSensorConfig();
        cfg.deserialize(reply, 0);
        return cfg;
    }
    
    public void accSensorSetFilter(final ACC_SENSOR_SOURCE source, final AccSensorFilter filter) throws Exception {
        final byte[] params = new byte[12];
        params[0] = 22;
        params[1] = source.getByteVal();
        if ((filter.flags & ACC_SENSOR_FILTER_FLAG.Range.getNumVal()) == ACC_SENSOR_FILTER_FLAG.Range.getNumVal() && filter.rangeThreshold.lo > filter.rangeThreshold.hi) {
            throw new Exception("Invalid parameters");
        }
        NurPacket.PacketWord(params, 2, filter.flags);
        NurPacket.PacketWord(params, 4, filter.rangeThreshold.lo);
        NurPacket.PacketWord(params, 6, filter.rangeThreshold.hi);
        NurPacket.PacketWord(params, 8, filter.timeThreshold.lo);
        NurPacket.PacketWord(params, 10, filter.timeThreshold.hi);
        try {
            final byte[] reply = this.doCustomCommand(params);
            if (reply == null) {
                return;
            }
            if (reply.length < 10) {
                throw new Exception("Error: AccSensorSetFilter Invalid reply");
            }
        }
        catch (Exception e) {
            throw e;
        }
    }
    
    public AccSensorFilter accSensorGetFilter(final ACC_SENSOR_SOURCE source) throws Exception {
        final byte[] params = { 23, source.getByteVal() };
        final byte[] reply = this.doCustomCommand(params);
        if (reply.length < 10) {
            throw new Exception("Error: accSensorGetFilter Invalid reply");
        }
        final AccSensorFilter filterConfig = new AccSensorFilter();
        filterConfig.flags = NurPacket.BytesToWord(reply, 0);
        filterConfig.rangeThreshold.lo = NurPacket.BytesToWord(reply, 2);
        filterConfig.rangeThreshold.hi = NurPacket.BytesToWord(reply, 4);
        filterConfig.timeThreshold.lo = NurPacket.BytesToWord(reply, 6);
        filterConfig.timeThreshold.hi = NurPacket.BytesToWord(reply, 8);
        return filterConfig;
    }
    
    public AccSensorData accSensorGetValue(final ACC_SENSOR_SOURCE source) throws Exception {
        final byte[] params = { 24, source.getByteVal() };
        final byte[] reply = this.doCustomCommand(params);
        if (reply[0] == ACC_EVENT_TYPE.SensorRangeData.getNumVal()) {
            final AccSensorRangeData data = new AccSensorRangeData();
            data.source = ACC_SENSOR_SOURCE.valueOf(reply[1] & 0xFF);
            data.range = NurPacket.BytesToDword(reply, 2);
            return data;
        }
        if (reply[0] == ACC_EVENT_TYPE.SensorChanged.getNumVal()) {
            throw new NurApiException(4101);
        }
        if (reply[0] == ACC_EVENT_TYPE.Barcode.getNumVal()) {
            throw new NurApiException(4101);
        }
        if (reply[0] == ACC_EVENT_TYPE.None.getNumVal()) {
            throw new NurApiException(4101);
        }
        return null;
    }
}
