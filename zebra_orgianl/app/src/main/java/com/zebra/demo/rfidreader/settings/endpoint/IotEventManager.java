package com.zebra.demo.rfidreader.settings.endpoint;


import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class IotEventManager {

    private static final String TAG = "IotEventManager";
    private final List<IotEventListener> listeners = new ArrayList<>();

    private static IotEventManager instance;

    public static IotEventManager getInstance() {
        if (instance == null) {
            instance = new IotEventManager();
        }
        return instance;
    }


    // Register a listener
    public void registerListener(IotEventListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    // Unregister a listener
    public void unregisterListener(IotEventListener listener) {
        if (listener == null) {
            Log.w(TAG, "Attempted to unregister a null listener.");
            return;
        }
        if (!listeners.remove(listener)) {
            Log.w(TAG, "Attempted to unregister a listener that was not registered.");
        }
    }

    // Notify all listeners about an IoT event
    public void notifyEvent(IotEventData eventData) {
        Log.i(TAG, " notifyIotEvent IotEventManager "+ eventData.getCause() + " " + eventData.getEpType() + " " + eventData.getEpName() + " " + eventData.getStatus() + " " + eventData.getReason());
        if (listeners.isEmpty()) {
            Log.d(TAG, "Listener is empty, skipping notification.");

        }
        for (IotEventListener listener : listeners) {
            try {
                if (listener == null) {
                    Log.d(TAG, "Listener is null, skipping notification.");
                    continue;
                }
                listener.onIotEvent(eventData);
            } catch (Exception e) {
                Log.e(TAG, "Error notifying listener: " + e.getMessage(), e);
                // Optionally remove the listener to prevent repeated errors
                //TODO :  use Iterator to safely remove elements from the listeners list during iteration
                listeners.remove(listener);
            }
        }

    }
}