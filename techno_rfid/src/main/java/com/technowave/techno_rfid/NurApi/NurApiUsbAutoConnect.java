/* 
  Copyright 2016- Nordic ID
  NORDIC ID SOFTWARE DISCLAIMER

  You are about to use Nordic ID Demo Software ("Software"). 
  It is explicitly stated that Nordic ID does not give any kind of warranties, 
  expressed or implied, for this Software. Software is provided "as is" and with 
  all faults. Under no circumstances is Nordic ID liable for any direct, special, 
  incidental or indirect damages or for any economic consequential damages to you 
  or to any third party.

  The use of this software indicates your complete and unconditional understanding 
  of the terms of this disclaimer. 
  
  IF YOU DO NOT AGREE OF THE TERMS OF THIS DISCLAIMER, DO NOT USE THE SOFTWARE.  
*/
package com.technowave.techno_rfid.NurApi;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.util.Log;

import com.technowave.techno_rfid.NordicId.NurApi;

public class NurApiUsbAutoConnect implements NurApiAutoConnectTransport
{
	static final String TAG = "NurApiUsbAutoConnect";

	private NurApi mApi = null;
	private Context mContext = null;
	private UsbManager mUsbManager = null;
	private UsbDevice mUsbDevice = null;
	private static final String ACTION_USB_PERMISSION = "com.nordicid.nurapi.USB_PERMISSION";
	private boolean mReceiverRegistered = false;
	private PendingIntent mPermissionIntent;
	private boolean mRequestingPermission = false;

	private String mReceiverRegisteredAction = "";

	private boolean mEnabled = false;

	void registerReceiver(String action)
	{
		if (action.length() == 0)
		{
			if (mReceiverRegistered)
			{
				mReceiverRegistered = false;
				mReceiverRegisteredAction = "";
				mContext.unregisterReceiver(mUsbReceiver);
				Log.d(TAG, "registerReceiver unregistered");
			} else {
				Log.d(TAG, "registerReceiver ALREADY unregistered");
			}
		}
		else {

			if (mReceiverRegisteredAction.equals(action))
			{
				Log.d(TAG, "registerReceiver "+action+" ALREADY registered");
				return;
			}

			if (mReceiverRegistered)
				mContext.unregisterReceiver(mUsbReceiver);

			IntentFilter intentFilter = new IntentFilter();
			intentFilter.addAction(ACTION_USB_PERMISSION);
			intentFilter.addAction(action);
			Log.d(TAG, "registerReceiver "+action+" registered");
			mContext.registerReceiver(mUsbReceiver, intentFilter);
			mReceiverRegistered = true;
			mReceiverRegisteredAction = action;
		}
	}

	public NurApiUsbAutoConnect(Context c, NurApi na) 
	{
		this.mContext = c;
		this.mApi = na;
		this.mUsbManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
		
		int flags = PendingIntent.FLAG_UPDATE_CURRENT;
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
			flags |= PendingIntent.FLAG_MUTABLE;
		}
	    mPermissionIntent = PendingIntent.getBroadcast(mContext, 0, new Intent(ACTION_USB_PERMISSION), flags);
	}

	private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() 
	{
		@Override
		public void onReceive(Context context, Intent intent) 
		{
			String action = intent.getAction();
			if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action))
			{
				synchronized (this) {
					mUsbDevice = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
					Log.d(TAG, "ACTION_USB_DEVICE_ATTACHED " + mUsbDevice);
					if (mUsbDevice != null && checkIsNurDevice(mUsbDevice)) {
						if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false))
						{
							NurApiUsbAutoConnect.this.setAddress(getAddress());
						} 
						else {
							Log.d(TAG, "ACTION_USB_DEVICE_ATTACHED permission not yet granted, requesting...");
							mUsbManager.requestPermission(mUsbDevice, mPermissionIntent);
						}
					}
				}
			} 
			else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
				Log.d(TAG, "ACTION_USB_DEVICE_DETACHED");
				disconnect();
			}
			else if (ACTION_USB_PERMISSION.equals(action))
			{
				mRequestingPermission = false;
				boolean granted = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false);
				Log.d(TAG, "ACTION_USB_PERMISSION granted: " + granted);

				if (granted)
					NurApiUsbAutoConnect.this.setAddress(getAddress());
			}
		}
	};

	private static boolean checkIsNurDevice(UsbDevice device) {
		if (device == null) return false;
		int vid = device.getVendorId();
		int pid = device.getProductId();
		int cls = device.getDeviceClass();
		
		// Nordic ID VIDs
		if (vid == 1254 || vid == 3589 || vid == 5593) return true;
		
		// Common USB-Serial VIDs
		if (vid == 1027 || vid == 4292 || vid == 1155 || vid == 9025 || vid == 6790 || vid == 1659) return true;
		
		// CDC Class (2 = Communications, 10 = CDC Data)
		if (cls == 2 || cls == 10) return true;
		
		// Check interface class as well
		for (int i = 0; i < device.getInterfaceCount(); i++) {
			int icls = device.getInterface(i).getInterfaceClass();
			if (icls == 2 || icls == 10) return true;
		}

		return false;
	}

	private void connect()
	{
		if (mUsbDevice != null && mUsbManager.hasPermission(mUsbDevice)) 
		{
			try {
				mApi.setTransport(null);
			} catch (Exception e) {
				e.printStackTrace();
			}
			NurApiUsbTransport tr = new NurApiUsbTransport(mUsbManager, mUsbDevice);
			try {
				mApi.setTransport(tr);
				mApi.connect();

				registerReceiver(UsbManager.ACTION_USB_DEVICE_DETACHED);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		else if(mUsbDevice != null && !mUsbManager.hasPermission(mUsbDevice))
		{
			mRequestingPermission = true;
			mUsbManager.requestPermission(mUsbDevice,mPermissionIntent);
		}
	}

	private void disconnect() 
	{
		if (mApi.isConnected()) {
			try {
				registerReceiver(UsbManager.ACTION_USB_DEVICE_ATTACHED);
				mApi.disconnect();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void onResume() 
	{
		if (mApi.isConnected())
			return;

		if (!mRequestingPermission)
			this.setAddress(getAddress());
	}
	
	@Override
	public void onDestroy() 
	{
		
	}

	@Override
	public void setAddress(String addr)
	{
		if (addr == null)
			addr = "";

		Log.d(TAG, "setAddress " + addr);

		mEnabled = addr.length() > 0;

		this.mUsbDevice = null;
		for (UsbDevice device : mUsbManager.getDeviceList().values()) {
			if(checkIsNurDevice(device)) {
				this.mUsbDevice = device;
				break;
			}
		}

		if (mUsbDevice == null) {
			registerReceiver(UsbManager.ACTION_USB_DEVICE_ATTACHED);
		} else {
			registerReceiver(UsbManager.ACTION_USB_DEVICE_DETACHED);
		}

		if (mUsbDevice != null) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Thread.sleep(200);
						connect();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
		}
	}


	@Override
	public void onPause() {
	}

	@Override
	public void onStop() {
		if (mApi.isConnected()) {
			try {
				mApi.disconnect(); 
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		registerReceiver("");
	}

	@Override
	public void dispose() {
		onStop();
	}

	@Override
	public String getType() {
		return "USB";
	}

	@Override
	public String getAddress() { return mEnabled ? "USB" : ""; }

	@Override
	public String getDetails() {
		if (mApi.isConnected())
			return "Connected to USB";
		else if (!mEnabled)
			return "Disabled";

		return "Disconnected from USB";
	}
}
