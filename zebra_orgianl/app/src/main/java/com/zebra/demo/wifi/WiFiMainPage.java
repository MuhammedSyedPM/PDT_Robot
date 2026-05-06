
package com.zebra.demo.wifi;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.zebra.demo.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class WiFiMainPage extends Fragment {


    public static WiFiMainPage newInstance() {
        return new WiFiMainPage();
    }


    public WiFiMainPage() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wifi_main, container, false);
    }


    @Override
    public void onResume() {
        super.onResume();

    }



}
