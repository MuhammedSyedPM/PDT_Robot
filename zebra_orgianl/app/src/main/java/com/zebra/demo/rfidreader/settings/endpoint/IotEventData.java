package com.zebra.demo.rfidreader.settings.endpoint;

public class IotEventData {
    private String cause;
    private String epType;
    private String epName;
    private String status;
    private String reason;

    public IotEventData() {

    }

    public String getCause() { return cause; }
    public void setCause(String cause) { this.cause = cause; }

    public String getEpType() { return epType; }
    public void setEpType(String epType) { this.epType = epType; }

    public String getEpName() { return epName; }
    public void setEpName(String epName) { this.epName = epName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}