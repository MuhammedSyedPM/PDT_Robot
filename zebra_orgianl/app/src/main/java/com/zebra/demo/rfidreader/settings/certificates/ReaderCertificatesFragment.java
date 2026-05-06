package com.zebra.demo.rfidreader.settings.certificates;


import static android.text.TextUtils.isEmpty;
import static com.zebra.demo.scanner.helpers.ActiveDeviceAdapter.SETTINGS_TAB;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.zebra.demo.ActiveDeviceActivity;
import com.zebra.demo.R;
import com.zebra.demo.rfidreader.rfid.RFIDController;
import com.zebra.demo.rfidreader.settings.BackPressedFragment;
import com.zebra.demo.rfidreader.settings.SettingsDetailActivity;
import com.zebra.demo.rfidreader.settings.adminlogin.ErrorLoginDialogFragment;
import com.zebra.rfid.api3.InvalidUsageException;
import com.zebra.rfid.api3.OperationFailureException;
import com.zebra.rfid.api3.RFIDResults;

import org.apache.commons.io.FilenameUtils;

import java.io.Closeable;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

public class ReaderCertificatesFragment extends BackPressedFragment {
    Context context;
    private static final String TAG = "ReaderCertificatesFragment";
    private CertificatesAdapter certificatesAdapter;

    ArrayList<String>  wpaCertificates = new ArrayList<>();

    TextView tvCertName;

    private String certificate;
    private String type;
    private String certType;
    private String userFileName;


    private ReaderCertificatesFragment() {

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ReaderCertificatesFragment.
     */
    public static ReaderCertificatesFragment newInstance() {
        return new ReaderCertificatesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        String vndrMsg = "";

        final View rootview = inflater.inflate(R.layout.fragment_certificates, container, false);

        context = rootview.getContext();
        boolean isCertificateFetchFailed;
        try {
            wpaCertificates = RFIDController.mConnectedReader.Config.getCertificates();
            isCertificateFetchFailed = false;
        } catch (InvalidUsageException e) {
            Log.d(TAG, "Returned SDK Exception wifiGetCertificates" + e.getVendorMessage());
            isCertificateFetchFailed = true;
            vndrMsg = e.getVendorMessage();
        } catch (OperationFailureException ope){
            if(ope.getResults() == RFIDResults.ADMIN_CONNECT_ERROR){
                ErrorLoginDialogFragment.handleAdminAuthenticationError(getParentFragmentManager());
            }

            Log.d(TAG, "Returned SDK Exception wifiGetCertificates" + ope.getVendorMessage());
            isCertificateFetchFailed = true;
            vndrMsg = ope.getVendorMessage();
        }
        if(isCertificateFetchFailed) {
            TextView errorView = rootview.findViewById(R.id.errorInfoView);
            errorView.setVisibility(View.VISIBLE);
            errorView.setText("Failed to fetch the Available certificates" + vndrMsg  );
            rootview.findViewById(R.id.wifistatusrow).setVisibility(View.GONE);
            rootview.findViewById(R.id.wifisupportrow).setVisibility(View.GONE);
            return rootview;
        }
        RecyclerView rvCertificates = rootview.findViewById(R.id.rv_certificates);
        Button btAdd = rootview.findViewById(R.id.bt_add);
        Button btRemoveAll = rootview.findViewById(R.id.bt_remove_all);
        Button btSave = rootview.findViewById(R.id.bt_save);
        certificatesAdapter = new CertificatesAdapter(wpaCertificates);
        rvCertificates.setLayoutManager(new LinearLayoutManager(context));
        rvCertificates.setAdapter(certificatesAdapter);

        btAdd.setOnClickListener(v -> {

            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetTheme);
            View sheetView = LayoutInflater.from(context).inflate(R.layout.add_certificate_bottom_sheet_layout, v.findViewById(R.id.bottom_sheet));
            TableRow editCertRow = sheetView.findViewById(R.id.tbl_row_edit_cert_name);
            tvCertName = sheetView.findViewById(R.id.tv_cert_name);
            EditText etCertName = sheetView.findViewById(R.id.et_cert_name);

            ArrayAdapter<CharSequence> typeArrayAdapter = ArrayAdapter.createFromResource(context,
                    R.array.type, android.R.layout.simple_spinner_item);
            typeArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            Spinner spinnerType = sheetView.findViewById(R.id.spinner_type);
            spinnerType.setAdapter(typeArrayAdapter);
            String[] typeArray = context.getResources().getStringArray(R.array.type);
            spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    type = typeArray[position];
                    userFileName = null;
                    if("others".equals(type)) {
                        editCertRow.setVisibility(View.VISIBLE);
                    } else {
                        editCertRow.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            ArrayAdapter<CharSequence> certTypeArrayAdapter = ArrayAdapter.createFromResource(context,
                    R.array.cert_type, android.R.layout.simple_spinner_item);
            certTypeArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            Spinner spinnerCertType = sheetView.findViewById(R.id.spinner_cert_type);
            spinnerCertType.setAdapter(certTypeArrayAdapter);
            String[] certTypeArray = context.getResources().getStringArray(R.array.cert_type);
            spinnerCertType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    certType = certTypeArray[position];
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            sheetView.findViewById(R.id.button_select_certificate).setOnClickListener(v1 -> {
                if(editCertRow.getVisibility() == View.VISIBLE) {
                    userFileName = etCertName.getText().toString();
                    if(isEmpty(userFileName)) {
                        Toast.makeText(context,"Enter File Name and Proceed", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                selectCertificate();
            });

            sheetView.findViewById(R.id.upload_certificate).setOnClickListener(v2 -> {
                if(isEmpty(certificate)) {
                    Toast.makeText(context,"Select a Certificate to proceed",Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    RFIDController.mConnectedReader.Config.addCertificate(certificate);
                    certificate = null;
                } catch (InvalidUsageException e) {
                    Toast.makeText(context,"Failed to add Certificate : " + e.getVendorMessage(),Toast.LENGTH_SHORT).show();
                    bottomSheetDialog.dismiss();
                    return;
                } catch (OperationFailureException op) {
                    if(op.getResults() == RFIDResults.ADMIN_CONNECT_ERROR){
                        ErrorLoginDialogFragment.handleAdminAuthenticationError(getParentFragmentManager());
                        return;
                    }

                    Toast.makeText(context,"Failed to add Certificate : " + op.getVendorMessage(),Toast.LENGTH_SHORT).show();
                    bottomSheetDialog.dismiss();
                    return;
                }

                try {
                    wpaCertificates = RFIDController.mConnectedReader.Config.getCertificates();
                    certificatesAdapter.updateList(wpaCertificates);
                } catch (InvalidUsageException e) {

                    if (e.getStackTrace().length > 0) {
                        Log.e(TAG, e.getStackTrace()[0].toString());
                    }
                    Toast.makeText(context,"Failed to fetch updated Certificate list",Toast.LENGTH_SHORT).show();
                } catch (OperationFailureException e) {
                    if(e.getResults() == RFIDResults.ADMIN_CONNECT_ERROR){
                       ErrorLoginDialogFragment.handleAdminAuthenticationError(getParentFragmentManager());
                        return;
                    }

                    if (e.getStackTrace().length > 0) {
                        Log.e(TAG, e.getStackTrace()[0].toString());
                    }
                    Toast.makeText(context,"Failed to fetch updated Certificate list",Toast.LENGTH_SHORT).show();
                }
                bottomSheetDialog.dismiss();
            });

            sheetView.findViewById(R.id.bottom_sheet_close).setOnClickListener(v3 -> bottomSheetDialog.dismiss());
            bottomSheetDialog.setContentView(sheetView);
            bottomSheetDialog.show();

        });



        btRemoveAll.setOnClickListener(v -> {
            try {
                RFIDResults rfidResults = RFIDController.mConnectedReader.Config.removeAllCertificates();
                if(rfidResults.equals(RFIDResults.RFID_API_SUCCESS)){
                    wpaCertificates = RFIDController.mConnectedReader.Config.getCertificates();
                    certificatesAdapter.updateList(wpaCertificates);
                }
            } catch ( InvalidUsageException e) {
                if (e.getStackTrace().length > 0) {
                    Log.e(TAG, e.getStackTrace()[0].toString());
                }
            }
            catch ( OperationFailureException e) {
                if(e.getResults() == RFIDResults.ADMIN_CONNECT_ERROR){
                    ErrorLoginDialogFragment.handleAdminAuthenticationError(getParentFragmentManager());
                }
                if (e.getStackTrace().length > 0) {
                    Log.e(TAG, e.getStackTrace()[0].toString());
                }
            }
        });

        btSave.setOnClickListener(v -> {
            try {
                RFIDResults rfidResults = RFIDController.mConnectedReader.Config.saveCertificates();
                if(rfidResults == RFIDResults.RFID_API_SUCCESS){
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(context, "Certificates Saved", Toast.LENGTH_SHORT).show());
                }

            } catch (InvalidUsageException  e) {
                if (e.getStackTrace().length > 0) {
                    Log.e(TAG, e.getStackTrace()[0].toString());
                }
            }catch (OperationFailureException e) {
                if(e.getResults() == RFIDResults.ADMIN_CONNECT_ERROR){
                    ErrorLoginDialogFragment.handleAdminAuthenticationError(getParentFragmentManager());
                }
                if (e.getStackTrace().length > 0) {
                    Log.e(TAG, e.getStackTrace()[0].toString());
                }
            }
        });
        return rootview;
    }

    private void selectCertificate(){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"application/x-pem-file", "application/pgp-keys"});
        Uri uri = Uri.parse("content://com.android.externalstorage.documents/document/primary:Download");
        intent.putExtra("DocumentsContract.EXTRA_INITIAL_URI", uri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        certificatePickerLauncher.launch(intent);
    }

    @Override
    public void onBackPressed() {
        if(getActivity() instanceof SettingsDetailActivity)
            ((SettingsDetailActivity) getActivity()).callBackPressed();
        if(getActivity() instanceof ActiveDeviceActivity) {
            ((ActiveDeviceActivity) getActivity()).callBackPressed();
            ((ActiveDeviceActivity) getActivity()).loadNextFragment(SETTINGS_TAB);
        }
    }

    ActivityResultLauncher<Intent> certificatePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        Thread threadCert = captureCertificate(data);
                        threadCert.start();
                    }
                }
            });

    @NonNull
    private Thread captureCertificate(Intent data) {
        Uri uri = data.getData();
        Thread threadCert = new Thread(() -> {
            certificate = copyCertFromUri(uri);
            if(isEmpty(certificate)) {
                Toast.makeText(context, "Invalid File details\nPlease try again", Toast.LENGTH_SHORT).show();
            }
        });
        threadCert.setPriority(Thread.MAX_PRIORITY);
        return threadCert;
    }


    private String copyCertFromUri(Uri uri) {
        String extractPath = requireContext().getCacheDir().getAbsolutePath() + File.separator;
        String datFile = null;
        ParcelFileDescriptor parcelFileDescriptor = null;
        FileInputStream inStream = null;
        FileOutputStream outStream = null;
        try {

            String ext = FilenameUtils.getExtension(getFileName(uri));
            String fileName;
            if("others".equals(type)) {
                if(isEmpty(userFileName)) {
                    return null;
                } else {
                    fileName = String.format("%s_%s", userFileName, certType);
                }
            } else {
                fileName = String.format("%s_%s", type, certType);
            }
            requireActivity().runOnUiThread(() -> tvCertName.setText(fileName));

            parcelFileDescriptor = requireContext().getContentResolver().openFileDescriptor(uri, "rw");
            if(parcelFileDescriptor!=null && !TextUtils.isEmpty(fileName)) {
                FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                datFile = extractPath + fileName;
                inStream = new FileInputStream(fileDescriptor);
                outStream = new FileOutputStream(datFile);
                FileChannel inChannel = inStream.getChannel();
                FileChannel outChannel = outStream.getChannel();
                inChannel.transferTo(0, inChannel.size(), outChannel);
                inStream.close();
                outStream.close();
            }
        } catch (IOException e) {
            return null;
        } finally {
            closeSafely(parcelFileDescriptor);
            closeSafely(inStream);
            closeSafely(outStream);
        }
        return datFile;
    }

    private void closeSafely(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException ignored) {
            }
        }
    }

    public String getFileName(Uri uri) {
        String result = null;
        if ("content".equals(uri.getScheme())) {
            Cursor cursor = requireContext().getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if(columnIndex>=0)
                    result = cursor.getString(columnIndex);
            }
            if(cursor != null)
                cursor.close();
        }
        if (result == null) {
            result = uri.getPath();
            if(result!=null && !result.isEmpty()) {
                int cut = result.lastIndexOf('/');
                if (cut != -1) {
                    result = result.substring(cut + 1);
                }
            }
        }
        return result;
    }

    public void handleCertificateEvent(String certEvent) {
        requireActivity().runOnUiThread(() ->
                Toast.makeText(context, "Certificate Event: " + certEvent, Toast.LENGTH_LONG).show()
        );
    }

}
