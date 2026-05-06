package com.zebra.demo.rfidreader.settings;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.zebra.demo.R;
import com.zebra.demo.rfidreader.rfid.RFIDController;
import com.zebra.rfid.api3.BatteryStatistics;
import com.zebra.rfid.api3.InvalidUsageException;
import com.zebra.rfid.api3.OperationFailureException;
import com.zebra.rfid.api3.RFIDResults;
import static com.zebra.rfid.api3.GenericUtil.isCharmanderDevice;

import java.util.ArrayList;
import java.util.List;

public class BatteryStatsFragment extends Fragment {

    private static final String TAG = "BatteryStatsFragment";
    List<BatteryStatisticsData> batteryTitleList = new ArrayList<>();
    BatteryStatistics batteryStats = new BatteryStatistics();

    public static BatteryStatsFragment newInstance() {
        return new BatteryStatsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_battery_stats,container, false);
        RecyclerView batteryRecyclerView = view.findViewById(R.id.battery_stats_recyclerview);
        BatteryStaticsAdapter batteryStaticsAdapter = new BatteryStaticsAdapter(batteryTitleList);
        batteryRecyclerView.setAdapter(batteryStaticsAdapter);
        batteryRecyclerView.addItemDecoration(new DividerItemDecoration(requireActivity(),DividerItemDecoration.VERTICAL));

        return view;

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchAndUpdateBatteryStats();
    }

    private void fetchAndUpdateBatteryStats() {

        if(RFIDController.mConnectedReader == null ) {
            Toast.makeText(getActivity(), "No device in connected state", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!RFIDController.mIsInventoryRunning) {
            try {
                if (RFIDController.mConnectedReader.Config != null)
                    batteryStats = RFIDController.mConnectedReader.Config.getBatteryStats();
                else
                    return;
            } catch (InvalidUsageException | NullPointerException e) {
                Log.e(TAG, e.getStackTrace()[0].toString());
            } catch (OperationFailureException e) {
                if (e.getResults() == RFIDResults.RFID_OPERATION_IN_PROGRESS) {
                    Toast.makeText(getActivity(), "Operation in progress, Battery statistics cannot be fetched", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(getActivity(), "Inventory is in progress, Battery statistics cannot be fetched", Toast.LENGTH_SHORT).show();
        }

        String modelName = RFIDController.mConnectedDevice.getName();
        String model = RFIDController.mConnectedDevice.getDeviceCapability(modelName);
        // Check if the device is either model "RE40" or matches the broader criteria defined by isCharmanderDevice().
        if ("RE40".equalsIgnoreCase(model) || isCharmanderDevice()) {
            String header = "Battery Asset Information";
            List<String> itemTitle = new ArrayList<>();
            List<String> itemValue = new ArrayList<>();
            itemTitle.add("Model Number");
            itemValue.add(batteryStats.getModelNumber());

            String header1 = "Battery Life Statistics";
            List<String> itemTitle1 = new ArrayList<>();
            List<String> itemValue1 = new ArrayList<>();
            itemTitle1.add("State of Health");
            itemValue1.add(batteryStats.getHealthState());
            itemTitle1.add("Battery Critical Level");
            itemValue1.add(String.valueOf(batteryStats.getCriticalLevel()) + "%");

            String header2 = "Battery Status";
            List<String> itemTitle2 = new ArrayList<>();
            List<String> itemValue2 = new ArrayList<>();
            itemTitle2.add("Full Charge Percentage");
            itemValue2.add(batteryStats.getFullChargeCapacity() +"%");
            itemTitle2.add("Charge Percentage");
            itemValue2.add(batteryStats.getPercentage() + "%");
            itemTitle2.add("Power Source");
            itemValue2.add(String.valueOf(batteryStats.getPowerSourceType()));
            itemTitle2.add("Status");
            itemValue2.add(String.valueOf(batteryStats.getCurrentState()));

            String header3 = "Battery Temperature and Voltage";
            List<String> itemTitle3 = new ArrayList<>();
            List<String> itemValue3 = new ArrayList<>();
            itemTitle3.add("Temperature");
            itemValue3.add(batteryStats.getTemperature() + "\u00B0" + "C");
            itemTitle3.add("Voltage");
            itemValue3.add(batteryStats.getVoltage() +" mV");

            batteryTitleList.add(new BatteryStatisticsData(header,itemTitle,itemValue));
            batteryTitleList.add(new BatteryStatisticsData(header1,itemTitle1,itemValue1));
            batteryTitleList.add(new BatteryStatisticsData(header2,itemTitle2,itemValue2));
            batteryTitleList.add(new BatteryStatisticsData(header3,itemTitle3,itemValue3));
        }
        else {
            String header = "Battery Asset Information";
            List<String> itemTitle = new ArrayList<>();
            List<String> itemValue = new ArrayList<>();
            itemTitle.add("Manufacture Date");
            itemValue.add(batteryStats.getManufactureDate());
//        itemTitle.add("Serial Number");
//        itemValue.add(String.valueOf(batteryStats.getSerialNumber()));
            itemTitle.add("Model Number");
            itemValue.add(batteryStats.getModelNumber());
            itemTitle.add("Battery ID");
            itemValue.add(batteryStats.getBatteryId());
//        itemTitle.add("Design Capacity");
//        itemValue.add(batteryStats.getDesignCapacity() +" mAh");

            String header1 = "Battery Life Statistics";
            List<String> itemTitle1 = new ArrayList<>();
            List<String> itemValue1 = new ArrayList<>();
            itemTitle1.add("State of Health");
            itemValue1.add(batteryStats.getHealth() + "%");
            itemTitle1.add("Charge Cycles Consumed");
            itemValue1.add(String.valueOf(batteryStats.getCycleCount()));

            String header2 = "Battery Status";
            List<String> itemTitle2 = new ArrayList<>();
            List<String> itemValue2 = new ArrayList<>();
//        itemTitle2.add("Voltage");
//        itemValue2.add(batteryStats.getVoltage() +" mV");
//        itemTitle2.add("Current");
//        itemValue2.add(batteryStats.getCurrent() + " mA");
//        itemTitle2.add("Fully Charge Capacity");
//        itemValue2.add(batteryStats.getFullChargeCapacity() +" mAh");
            itemTitle2.add("Charge Percentage");
            itemValue2.add(batteryStats.getPercentage() + "%");
//        itemTitle2.add("Remaining Capacity");
//        itemValue2.add(batteryStats.getRemainingCapacity() +" mAh");
            itemTitle2.add("Charge Status");
            itemValue2.add(String.valueOf(batteryStats.getChargeStatus()));
//        itemTitle2.add("Time To Full Charge");
//        itemValue2.add(batteryStats.getTimeToFullCharge() +" ms");
//        itemTitle2.add("Charging Status");
//        itemValue2.add(String.valueOf(batteryStats.getCharging()));
//        itemTitle2.add("Status");
//        itemValue2.add(String.valueOf(batteryStats.getStatus()));

            String header3 = "Battery Temperature";
            List<String> itemTitle3 = new ArrayList<>();
            List<String> itemValue3 = new ArrayList<>();
            itemTitle3.add("Present");
            itemValue3.add(batteryStats.getTemperature() + "\u00B0" + "C");

            batteryTitleList.add(new BatteryStatisticsData(header,itemTitle,itemValue));
            batteryTitleList.add(new BatteryStatisticsData(header1,itemTitle1,itemValue1));
            batteryTitleList.add(new BatteryStatisticsData(header2,itemTitle2,itemValue2));
            batteryTitleList.add(new BatteryStatisticsData(header3,itemTitle3,itemValue3));
        }
    }


}