package com.zebra.demo.rfidreader.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.zebra.demo.R;
import com.zebra.demo.rfidreader.rfid.RFIDController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SelectAntennaDialog extends DialogFragment {
    private final String TAG = this.getClass().getSimpleName();
    private LinearLayout layout;
    private final Map<String, Short> antennaMap = new HashMap<>();
    private SelectedAntennaCallback mCallback;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.dialog_antenna_selection,container,false);
        layout = view.findViewById(R.id.antennaSelectionLayout);
        view.findViewById(R.id.applyButton).setOnClickListener(v -> {
            ArrayList<Short> selectedAntennas = new ArrayList<>();
            for(int i=0;i<layout.getChildCount();i++) {
                View childView = layout.getChildAt(i);
                if(childView instanceof CheckBox ) {
                    CheckBox mBox = ((CheckBox) childView);
                    if(mBox.isChecked()) {
                        selectedAntennas.add(antennaMap.get(mBox.getText().toString()));
                    }
                }
            }
            if(selectedAntennas.isEmpty()) {
                Toast.makeText(requireContext(),"Please select Atleast one Antenna to proceed", Toast.LENGTH_SHORT).show();
            } else {
                short[] selectedAntennaIDs = new short[selectedAntennas.size()];
                for(int i=0; i<selectedAntennas.size(); i++) {
                    selectedAntennaIDs[i] = selectedAntennas.get(i);
                }
                mCallback.sendSelectedAntennas(selectedAntennaIDs);
                this.dismiss();
            }

        });
        getAntennaDetails();
        return view;
    }

    private void getAntennaDetails() {
        if(RFIDController.mConnectedReader != null
                && RFIDController.mConnectedReader.ReaderCapabilities != null &&
                RFIDController.mConnectedReader.ReaderCapabilities.getNumAntennaSupported() > 0) {
            short[] preSelectedAntennas = RFIDController.getInstance().getSelectedAntennas();
            int preSelectedAntennaIndex = 0;
            for(short i = 1; i<= RFIDController.mConnectedReader.ReaderCapabilities.getNumAntennaSupported(); i++) {
                String antennaName = "Antenna "+i;
                antennaMap.put(antennaName, i);
                CheckBox checkBox = new CheckBox(requireContext());
                LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                int margin = getResources().getDimensionPixelSize(R.dimen.list_vertical_margin);
                mParams.setMargins(margin,margin,margin,margin);
                checkBox.setText(antennaName);
                checkBox.setTextSize(16);
                checkBox.setLayoutParams(mParams);
                if(preSelectedAntennas == null) {
                    checkBox.setChecked(true);
                } else {
                    boolean isAntennaAvailable = (preSelectedAntennaIndex < preSelectedAntennas.length
                            && preSelectedAntennas[preSelectedAntennaIndex] == i);
                    checkBox.setChecked(isAntennaAvailable);
                    if(isAntennaAvailable) {
                        preSelectedAntennaIndex++;
                    }
                }
                layout.addView(checkBox, i);
            }
        }
    }

    public void show(FragmentManager fm, SelectedAntennaCallback callback) {
        this.mCallback = callback;
        this.show(fm, TAG);
    }

}

