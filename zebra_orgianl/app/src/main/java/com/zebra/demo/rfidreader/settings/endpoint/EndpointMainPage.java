package com.zebra.demo.rfidreader.settings.endpoint;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.zebra.demo.R;


public class EndpointMainPage extends Fragment {

    public EndpointMainPage() {
    }

    public static EndpointMainPage newInstance() {
        return new EndpointMainPage();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_endpoint_config_and_status, container, false);
    }


    @Override
    public void onResume() {
        super.onResume();

    }
}
