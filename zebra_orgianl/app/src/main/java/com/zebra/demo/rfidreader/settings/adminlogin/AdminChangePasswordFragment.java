package com.zebra.demo.rfidreader.settings.adminlogin;

import static com.zebra.demo.scanner.helpers.ActiveDeviceAdapter.ADMIN_LOGIN_PAGE;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

public class AdminChangePasswordFragment extends BackPressedFragment implements View.OnClickListener {

    private EditText oldPasswordEditText;
    private EditText newPasswordEditText;
    private EditText confirmPasswordEditText;
    private Button saveButton;
    private ImageButton oldPasswordInfoButton;
    private ImageButton newPasswordInfoButton;
    private ImageButton confirmPasswordInfoButton;
    private static final String TAG = "AdminChangePasswordFragment";

    public static AdminChangePasswordFragment newInstance() {
        return new AdminChangePasswordFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_change_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        oldPasswordEditText = view.findViewById(R.id.old_password);
        newPasswordEditText = view.findViewById(R.id.new_password);
        confirmPasswordEditText = view.findViewById(R.id.confirm_password);
        saveButton = view.findViewById(R.id.btn_save);

        oldPasswordInfoButton = view.findViewById(R.id.old_password_info);
        newPasswordInfoButton = view.findViewById(R.id.new_password_info);
        confirmPasswordInfoButton = view.findViewById(R.id.confirm_password_info);

        saveButton.setOnClickListener(this);
        oldPasswordInfoButton.setOnClickListener(this);
        newPasswordInfoButton.setOnClickListener(this);
        confirmPasswordInfoButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_save) {
            changePassword(v);
        } else if (v.getId() == R.id.old_password_info ||
                   v.getId() == R.id.new_password_info ||
                   v.getId() == R.id.confirm_password_info) {
            showPasswordRequirements();
        }
    }

    private void showPasswordRequirements() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Password Requirements")
               .setMessage("• Minimum length: 9 characters, maximum length: 31 characters\n• At least one uppercase letter\n• At least one lowercase letter\n• At least one number\n• At least one special character")
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.dismiss();
                })
               .show();
    }

    private void changePassword(View view) {
        String oldPassword = oldPasswordEditText.getText().toString();
        String newPassword = newPasswordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();

        if( oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(getActivity(), "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }
        if(oldPassword.contains(" ")) {
            oldPasswordEditText.setError("Password cannot contain spaces");
            return;
        }
        if(newPassword.contains(" ")) {
            newPasswordEditText.setError("Password cannot contain spaces");
            return;
        }
        if(confirmPassword.contains(" ")) {
            confirmPasswordEditText.setError("Password cannot contain spaces");
            return;
        }

        Handler handler = new Handler(Looper.getMainLooper());
        ExecutorService executor = Executors.newSingleThreadExecutor();
        AtomicReference<RFIDResults> rfidResultsAtomicReference = new AtomicReference<>();
        executor.execute(() -> {
            try {
                rfidResultsAtomicReference.set(RFIDController.mConnectedReader.Config.adminSetPassword(oldPassword, newPassword, confirmPassword));
            } catch (InvalidUsageException e){
                ((ActiveDeviceActivity) getContext()).sendNotification(Constants.ACTION_READER_STATUS_OBTAINED,  e.getVendorMessage());
                showResultDialog(false, e.getVendorMessage());
                if (e.getStackTrace().length > 0) {
                    Log.e(TAG, e.getStackTrace()[0].toString());
                }

            } catch (OperationFailureException e) {
                handler.post(() -> {
                    showResultDialog(false,  e.getVendorMessage());
                    ((ActiveDeviceActivity) getContext()).sendNotification(Constants.ACTION_READER_STATUS_OBTAINED,  e.getVendorMessage());
                });

                Log.d(TAG, "Returned SDK Exception adminSetPassword "+ e.getVendorMessage());
                if (e.getStackTrace().length > 0) {
                    Log.e(TAG, e.getStackTrace()[0].toString());
                }
            }
            handler.post(() -> {
                if(RFIDResults.RFID_API_SUCCESS == rfidResultsAtomicReference.get()){
                    showResultDialog(true, "Password change successful! Login with new password.");
                }
            });

        });
    }


    @Override
    public void onBackPressed() {
        if(getActivity() instanceof SettingsDetailActivity)
            ((SettingsDetailActivity) getActivity()).callBackPressed();
        if(getActivity() instanceof ActiveDeviceActivity) {
            ((ActiveDeviceActivity) getActivity()).callBackPressed();
            ((ActiveDeviceActivity) getActivity()).loadNextFragment(ADMIN_LOGIN_PAGE);
        }
    }

    private void showResultDialog(boolean isSuccess, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(isSuccess ? "Success" : "Error")
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
