package com.zebra.demo.rfidreader.settings.adminlogin;

import static com.zebra.demo.scanner.helpers.ActiveDeviceAdapter.ADMIN_CHANGE_PASSWORD_PAGE;
import static com.zebra.demo.scanner.helpers.ActiveDeviceAdapter.MAIN_HOME_SETTINGS_TAB;
import static com.zebra.demo.scanner.helpers.ActiveDeviceAdapter.SETTINGS_TAB;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.snackbar.Snackbar;
import com.zebra.demo.ActiveDeviceActivity;
import com.zebra.demo.R;
import com.zebra.demo.rfidreader.common.Constants;
import com.zebra.demo.rfidreader.rfid.RFIDController;
import com.zebra.demo.rfidreader.settings.BackPressedFragment;
import com.zebra.demo.rfidreader.settings.SettingsDetailActivity;
import com.zebra.rfid.api3.InvalidUsageException;
import com.zebra.rfid.api3.OperationFailureException;
import com.zebra.rfid.api3.RFIDResults;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

public class AdminLoginFragment extends BackPressedFragment implements View.OnClickListener {

    private static final String TAG = "AdminConnectFragment";
    private EditText passwordEditText;
    private Button loginbtn;
    private Button changePasswordButton;
    private ImageButton passwordInfoButton;

    @Override
    public void onBackPressed() {
        if(getActivity() instanceof SettingsDetailActivity)
            ((SettingsDetailActivity) getActivity()).callBackPressed();
        if(getActivity() instanceof ActiveDeviceActivity) {
            ((ActiveDeviceActivity) getActivity()).callBackPressed();
            ((ActiveDeviceActivity) getActivity()).loadNextFragment(MAIN_HOME_SETTINGS_TAB);
        }
    }

    public static AdminLoginFragment newInstance() {
        return new AdminLoginFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_connect, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        passwordEditText = view.findViewById(R.id.admin_password);
        loginbtn = view.findViewById(R.id.btn_login);
        passwordInfoButton = view.findViewById(R.id.password_info);
        changePasswordButton = view.findViewById(R.id.btn_change_password);

        loginbtn.setOnClickListener(this);
        changePasswordButton.setOnClickListener(this);
        passwordInfoButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_login) {
            attemptConnect(v);
        } else if (id == R.id.btn_change_password) {
            navigateToChangePassword();
        } else if (id== R.id.password_info ) {
            showPasswordRequirements();
        }
    }

    private void showPasswordRequirements() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Password Requirements")
                .setMessage("• Minimum length: 9 characters, maximum length: 31 characters\n• At least one uppercase letter\n• At least one lowercase letter\n• At least one number\n• At least one special character")
                .setPositiveButton("OK", null)
                .show();
    }

    private void attemptConnect(View v) {
        String enteredPassword = passwordEditText.getText().toString();
        if(enteredPassword == null || enteredPassword.isEmpty()) {
            passwordEditText.setError("Password cannot be empty");
            return;
        }
       if(enteredPassword.contains(" ")){
            passwordEditText.setError("Password cannot contain spaces");
            return;
        }

        Handler handler = new Handler(Looper.getMainLooper());
        ExecutorService executor = Executors.newSingleThreadExecutor();
        AtomicReference<RFIDResults> rfidResultsAtomicReference = new AtomicReference<>();
        executor.execute(() -> {
            try {

                rfidResultsAtomicReference.set( RFIDController.mConnectedReader.Config.adminLogin(enteredPassword));
            } catch ( OperationFailureException e) {
                   handler.post(() -> {
                       ((ActiveDeviceActivity) getContext()).sendNotification(Constants.ACTION_READER_STATUS_OBTAINED,   e.getVendorMessage());
                       showResultDialog(false, e.getVendorMessage());
                   });
                if (e.getStackTrace().length > 0) {
                    Log.e(TAG, e.getStackTrace()[0].toString());
                }
            }catch ( InvalidUsageException e) {
                    handler.post(() -> {
                        ((ActiveDeviceActivity) getContext()).sendNotification(Constants.ACTION_READER_STATUS_OBTAINED, e.getInfo().toString() + "\n" + e.getVendorMessage());
                        showResultDialog(false, e.getInfo().toString() + "\n" + e.getVendorMessage());
                    });
                if (e.getStackTrace().length > 0) {
                    Log.e(TAG, e.getStackTrace()[0].toString());
                }
            }

            handler.post(() -> {
                RFIDResults result = rfidResultsAtomicReference.get();
                if (result != null && RFIDResults.RFID_API_SUCCESS == result) {
                    showResultDialog(true, "Admin login successful!");
                }
            });
        });
    }

    private void navigateToChangePassword() {
        if(getActivity() instanceof SettingsDetailActivity)
            ((SettingsDetailActivity) getActivity()).callBackPressed();
        if(getActivity() instanceof ActiveDeviceActivity) {
            ((ActiveDeviceActivity) getActivity()).callBackPressed();
            ((ActiveDeviceActivity) getActivity()).loadNextFragment(ADMIN_CHANGE_PASSWORD_PAGE);
        }
    }

    private void showResultDialog(boolean isSuccess, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle( isSuccess ? "Success" : "Error!")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> {
                    if (isSuccess) {
                        onBackPressed();
                    }
                    dialog.dismiss();
                })
                .show();
    }

}
