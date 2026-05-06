package com.technowave.techno_rfid.NurApi;

/**
 * Interface representing the UART service's events.
 */
public interface UartServiceEvents {
	public void onConnStateChanged();
	public void onDataAvailable(byte []data);
	public void onReadRemoteRssi(int rssi);
}
