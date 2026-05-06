package com.zebra.demo.rfidreader.settings;

public interface SelectedAntennaCallback {
    void sendSelectedAntennas(short[] antennaIdList);
}
