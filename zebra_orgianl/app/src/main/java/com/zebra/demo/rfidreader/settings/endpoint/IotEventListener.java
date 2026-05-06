package com.zebra.demo.rfidreader.settings.endpoint;

public interface IotEventListener {
    void onIotEvent(IotEventData eventData);
}