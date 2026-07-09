// 
// Decompiled by Procyon v0.5.36
// 

package com.technowave.techno_rfid.NordicId;

public interface NurApiListener
{
    void logEvent(final int p0, final String p1);
    
    void connectedEvent();
    
    void disconnectedEvent();
    
    void bootEvent(final String p0);
    
    void inventoryStreamEvent(final NurEventInventory p0);
    
    void IOChangeEvent(final NurEventIOChange p0);
    
    void traceTagEvent(final NurEventTraceTag p0);
    
    void triggeredReadEvent(final NurEventTriggeredRead p0);
    
    void frequencyHopEvent(final NurEventFrequencyHop p0);
    
    void debugMessageEvent(final String p0);
    
    void inventoryExtendedStreamEvent(final NurEventInventory p0);
    
    void programmingProgressEvent(final NurEventProgrammingProgress p0);
    
    void deviceSearchEvent(final NurEventDeviceInfo p0);
    
    void clientConnectedEvent(final NurEventClientInfo p0);
    
    void clientDisconnectedEvent(final NurEventClientInfo p0);
    
    void nxpEasAlarmEvent(final NurEventNxpAlarm p0);
    
    void epcEnumEvent(final NurEventEpcEnum p0);
    
    void autotuneEvent(final NurEventAutotune p0);
    
    void tagTrackingScanEvent(final NurEventTagTrackingData p0);
    
    void tagTrackingChangeEvent(final NurEventTagTrackingChange p0);
}
