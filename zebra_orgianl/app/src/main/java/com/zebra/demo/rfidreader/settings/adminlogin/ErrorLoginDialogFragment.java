package com.zebra.demo.rfidreader.settings.adminlogin;

import static com.zebra.demo.scanner.helpers.ActiveDeviceAdapter.ADMIN_CHANGE_PASSWORD_PAGE;
import static com.zebra.demo.scanner.helpers.ActiveDeviceAdapter.ADMIN_LOGIN_PAGE;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.button.MaterialButton;
import com.zebra.demo.ActiveDeviceActivity;
import com.zebra.demo.R;
import com.zebra.demo.rfidreader.settings.SettingsDetailActivity;

public class ErrorLoginDialogFragment extends DialogFragment {
    private Runnable dismissListener;

    public void setDismissListener(Runnable listener) {
        this.dismissListener = listener;
    }

    @Override
    public void onDismiss(@NonNull android.content.DialogInterface dialog) {
        super.onDismiss(dialog);
        if (dismissListener != null) {
            dismissListener.run();
        }
    }

    private static boolean isDialogShowing = false;

    /**
     * Central method to handle admin authentication errors
     * Shows the error dialog only if one isn't already displayed
     *
     * @param fragmentManager The fragment manager to use for showing the dialog
     */
    public static void handleAdminAuthenticationError(FragmentManager fragmentManager) {

        if (!isDialogShowing) {
            isDialogShowing = true;
            ErrorLoginDialogFragment dialogFragment = newInstance();
            dialogFragment.setDismissListener(() -> isDialogShowing = false);

            if (fragmentManager != null && !fragmentManager.isDestroyed()) {
                dialogFragment.show(fragmentManager, "ErrorLoginDialogFragment");
            } else {
                isDialogShowing = false;
            }
        }
    }

    public static ErrorLoginDialogFragment newInstance() {
        return new ErrorLoginDialogFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.dialog_error_login, null);

        MaterialButton btnLogin = view.findViewById(R.id.btn_login);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setView(view);

        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        btnLogin.setOnClickListener(v -> {
            redirectToAdminLogin();
            dismiss();
        });

        dialog.setOnKeyListener((dialogInterface, keyCode, keyEvent) -> {
            if (keyCode == android.view.KeyEvent.KEYCODE_BACK &&
                    keyEvent.getAction() == android.view.KeyEvent.ACTION_UP) {

                return true;
            }
            return false;
        });

        return dialog;
    }

    private void redirectToAdminLogin() {
        if (getActivity() instanceof SettingsDetailActivity) {
            ((SettingsDetailActivity) getActivity()).callBackPressed();
        }
        if (getActivity() instanceof ActiveDeviceActivity) {
            ((ActiveDeviceActivity) getActivity()).callBackPressed();
            ((ActiveDeviceActivity) getActivity()).loadNextFragment(ADMIN_LOGIN_PAGE);
        }
    }
}