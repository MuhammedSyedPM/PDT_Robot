package com.zebra.demo.rfidreader.settings;

import static android.content.Context.MODE_PRIVATE;
import static com.zebra.demo.rfidreader.rfid.RFIDController.TAG;
import static com.zebra.demo.rfidreader.rfid.RFIDController.antennaRfConfig;
import static com.zebra.demo.rfidreader.rfid.RFIDController.dynamicPowerSettings;
import static com.zebra.demo.rfidreader.rfid.RFIDController.mConnectedReader;
import static com.zebra.demo.rfidreader.rfid.RFIDController.singulationControl;
import static com.zebra.demo.rfidreader.settings.ProfileContent.linkProfileUtil;
import static com.zebra.rfid.api3.ENUM_DUTY_CYCLE.MAX;
import static com.zebra.rfid.api3.ENUM_DUTY_CYCLE.MEDIUM;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zebra.demo.R;
import com.zebra.demo.rfidreader.common.Constants;
import com.zebra.demo.rfidreader.common.LinkProfileUtil;
import com.zebra.demo.rfidreader.rfid.RFIDController;
import com.zebra.rfid.api3.InvalidUsageException;
import com.zebra.rfid.api3.OperationFailureException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class ProfileRecyclerViewAdapterRE40 extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    List<ProfileContentRE40> items;
    public ProfileContent.ProfilesItem mItem;
    public int selectedItemPosition = -1;
    public ArrayAdapter<CharSequence> mDutyCycleAdapter;
    public ArrayAdapter<String> mLinkProfileAdapter = null;
    public ArrayAdapter<CharSequence> mSessionAdapter;
    public ArrayList<UserProfileViewHolder> mUserProfileAdapter = new ArrayList<>();
    public static volatile boolean mIsInventoryRunning;
    public ProfileRecyclerViewAdapterRE40(Context context, List<ProfileContentRE40> items) {
        this.context = context;
        this.items = items;

        String defaultProfile = null;
        try {
            defaultProfile = mConnectedReader.Config.getDefaultProfile();
        } catch (OperationFailureException | InvalidUsageException e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String stackTraceString = sw.toString();
            Log.e("TAG", stackTraceString);        }
        for (int i = 0; i < items.size(); i++) {
            if (defaultProfile.equals(items.get(i).profileId)) {
                selectedItemPosition = i;
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ProfileContentRE40.LayoutTwo:
                return new UserProfileViewHolder(LayoutInflater.from(context).inflate(R.layout.fragment_user_profile, parent, false));
            default:
                return new ProfileViewHolderRE40(LayoutInflater.from(context).inflate(R.layout.fragment_profile_re40, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ProfileViewHolderRE40) {
            ((ProfileViewHolderRE40)holder).setData(position, items.get(position));
        } else if(holder instanceof UserProfileViewHolder) {
            ((UserProfileViewHolder)holder).setData2(position, items.get(position));
            mUserProfileAdapter.add( (UserProfileViewHolder) holder);
            ProfileFragmentRE40 contentRE40 = new ProfileFragmentRE40();
            contentRE40.saveDataToAntenna();
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getViewType();
    }

    public class ProfileViewHolderRE40 extends RecyclerView.ViewHolder {
        public final View mItemView;
        TextView contentView;
        public ProfileContentRE40 mItem;
        public int position = -1;

        public ProfileViewHolderRE40(View itemView) {
            super(itemView);
            mItemView = itemView;
            contentView = itemView.findViewById(R.id.content_re40);
            if(RFIDController.mIsInventoryRunning){
                itemView.findViewById(R.id.titleLayout).setClickable(false);
            }else {
                itemView.findViewById(R.id.titleLayout).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mConnectedReader != null ) {
                            int previousSelectedPosition = selectedItemPosition;
                            selectedItemPosition = position;
                            notifyItemChanged(previousSelectedPosition);
                            notifyItemChanged(selectedItemPosition);
                            boolean setDefaultProfile = false;
                            try {
                                setDefaultProfile = mConnectedReader.Config.setRFIDProfile(mItem.profileId);
                            } catch (OperationFailureException | InvalidUsageException e) {
                                StringWriter sw = new StringWriter();
                                PrintWriter pw = new PrintWriter(sw);
                                e.printStackTrace(pw);
                                String stackTraceString = sw.toString();
                                Log.e("TAG", stackTraceString);                            }
                            Log.d(TAG, "Default Profile is Set" + setDefaultProfile);

                            ProfileFragmentRE40 contentRE40 = new ProfileFragmentRE40();
                            contentRE40.saveDataToAntenna();
                        }else{
                            Toast.makeText(context,"Reader unavailable\nPlease Try again later",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }

        public void setData(int position, ProfileContentRE40 itemData) {
            this.position = position;
            this.mItem = itemData;
            contentView.setText(itemData.getContent());
            if (selectedItemPosition == position && !ProfileContentRE40.profileChnageToUser) {
                contentView.setTextColor(0xFFFF7043);
            } else {
                contentView.setTextColor(Color.BLACK);
            }
        }
    }

    public class UserProfileViewHolder extends RecyclerView.ViewHolder {
        public final View mItemView;
        public TextView mTextViewDetails;
        public EditText mTextPower;
        public Spinner mLinkProfileSpinner;
        public Spinner mDutyCycleSpinner;
        public ProfileContentRE40 mItem;
        public Spinner mSession;
        public CheckBox mDynamicPower;
        public int position = -1;
        TextView contentView;
        private int[] powerLevels;
        private final LinearLayout detailLayout;

        public UserProfileViewHolder(@NonNull View itemView) {
            super(itemView);
            mItemView = itemView;
            mTextViewDetails = itemView.findViewById(R.id.contentDetails);
            mTextPower = itemView.findViewById(R.id.powerLevelProfile);
            mLinkProfileSpinner = itemView.findViewById(R.id.linkProfile);
            contentView = itemView.findViewById(R.id.content2_re40);
            detailLayout = itemView.findViewById(R.id.userprofileSettings);
            mDynamicPower = itemView.findViewById(R.id.dynamicPower);
            mSession = itemView.findViewById(R.id.session);
            mDutyCycleSpinner = itemView.findViewById(R.id.dutyCycle);
            if(RFIDController.mIsInventoryRunning){
                itemView.findViewById(R.id.titleLayout).setClickable(false);
            }else{
                itemView.findViewById(R.id.titleLayout).setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if(mConnectedReader == null || !mConnectedReader.isConnected()) {
                            Toast.makeText(context,"Reader unavailable\nPlease Try again later",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        contentView.setTextColor(0xFFFF7043);
                        int previousSelectedPosition = selectedItemPosition;
                        selectedItemPosition = position;
                        notifyItemChanged(previousSelectedPosition);
                        try {
                            mConnectedReader.Config.setRFIDProfile(mItem.profileId);
                        } catch (OperationFailureException | InvalidUsageException e) {
                            StringWriter sw = new StringWriter();
                            PrintWriter pw = new PrintWriter(sw);
                            e.printStackTrace(pw);
                            String stackTraceString = sw.toString();
                            Log.e("TAG", stackTraceString);                        }
                        detailLayout.setVisibility(View.VISIBLE);

                        if (mConnectedReader != null && mConnectedReader.isConnected()) {
                            powerLevels = mConnectedReader.ReaderCapabilities.getTransmitPowerLevelValues();
                            linkProfileUtil = LinkProfileUtil.getInstance();

                            if (antennaRfConfig != null) {
                                mItem.powerLevel = powerLevels[antennaRfConfig.getTransmitPowerIndex()];
                                mItem.LinkProfileIndex = (int) antennaRfConfig.getrfModeTableIndex();//linkProfileUtil.getSelectedLinkProfilePosition(antennaRfConfig.getrfModeTableIndex());
                            }
                            if (singulationControl != null && singulationControl.getSession() != null)
                                mItem.SessionIndex = singulationControl.getSession().getValue();
                            if (dynamicPowerSettings != null) {
                                if (dynamicPowerSettings.getValue() == 0)
                                    mItem.DPO_On = false;
                                else if (dynamicPowerSettings.getValue() == 1)
                                    mItem.DPO_On = true;
                            }
                            try {
                                mItem.dutyCycleIndex = mConnectedReader.Config.getUserDutyCycle().ordinal();
                            } catch (OperationFailureException | InvalidUsageException e) {
                                StringWriter sw = new StringWriter();
                                PrintWriter pw = new PrintWriter(sw);
                                e.printStackTrace(pw);
                                String stackTraceString = sw.toString();
                                Log.e("TAG", stackTraceString);                            }
                        }
                        linkProfileUtil = LinkProfileUtil.getInstance();
                        mTextViewDetails.setText(mItem.contentDetails);
                        mTextPower.setText(String.valueOf(mItem.powerLevel));
                        mDutyCycleSpinner.setAdapter(mDutyCycleAdapter);
                        mLinkProfileSpinner.setAdapter(mLinkProfileAdapter);
                        mSession.setAdapter(mSessionAdapter);
                        mItem.LinkProfileIndex = linkProfileUtil.getSelectedLinkProfilePosition(antennaRfConfig.getrfModeTableIndex());
                        mDynamicPower.setChecked(items.get(position).DPO_On);
                        mSession.setSelection(items.get(position).SessionIndex);
                        mLinkProfileSpinner.setSelection(items.get(position).LinkProfileIndex);
                        mDutyCycleSpinner.setSelection(items.get(position).dutyCycleIndex);
                        ProfileFragmentRE40 contentRE40 = new ProfileFragmentRE40();
                        contentRE40.saveDataToAntenna();
                    }
                });
            }
        }
        public void setData2(int position, ProfileContentRE40 itemData) {
            this.position = position;
            this.mItem = itemData;
            contentView.setText(itemData.getContent());
            if (selectedItemPosition == position || ProfileContentRE40.profileChnageToUser) {
                contentView.setTextColor(0xFFFF7043);
//                int previousSelectedPosition = selectedItemPosition;
                selectedItemPosition = position;
                try {
                    mConnectedReader.Config.setRFIDProfile(mItem.profileId);
                } catch (OperationFailureException | InvalidUsageException e) {
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    e.printStackTrace(pw);
                    String stackTraceString = sw.toString();
                    Log.e("TAG", stackTraceString);                }
                detailLayout.setVisibility(View.VISIBLE);

                if (mConnectedReader != null && mConnectedReader.isConnected()) {
                    powerLevels = mConnectedReader.ReaderCapabilities.getTransmitPowerLevelValues();
                    linkProfileUtil = LinkProfileUtil.getInstance();

                    if (antennaRfConfig != null) {
                        mItem.powerLevel = powerLevels[antennaRfConfig.getTransmitPowerIndex()];
                        mItem.LinkProfileIndex = linkProfileUtil.getSelectedLinkProfilePosition(antennaRfConfig.getrfModeTableIndex());
                    }
                    if (singulationControl != null && singulationControl.getSession() != null)
                        mItem.SessionIndex = singulationControl.getSession().getValue();
                    if (dynamicPowerSettings != null) {
                        if (dynamicPowerSettings.getValue() == 0)
                            mItem.DPO_On = false;
                        else if (dynamicPowerSettings.getValue() == 1)
                            mItem.DPO_On = true;
                    }
                    try {
                        mItem.dutyCycleIndex = mConnectedReader.Config.getUserDutyCycle().ordinal();
                    } catch (OperationFailureException | InvalidUsageException e) {
                        StringWriter sw = new StringWriter();
                        PrintWriter pw = new PrintWriter(sw);
                        e.printStackTrace(pw);
                        String stackTraceString = sw.toString();
                        Log.e("TAG", stackTraceString);                    }
                }

                linkProfileUtil = LinkProfileUtil.getInstance();
                mTextViewDetails.setText(mItem.contentDetails);
                mTextPower.setText(String.valueOf(mItem.powerLevel));
                mDutyCycleSpinner.setAdapter(mDutyCycleAdapter);
                mLinkProfileSpinner.setAdapter(mLinkProfileAdapter);
                mSession.setAdapter(mSessionAdapter);
                mItem.LinkProfileIndex = linkProfileUtil.getSelectedLinkProfilePosition(antennaRfConfig.getrfModeTableIndex());
                mDynamicPower.setChecked(items.get(position).DPO_On);
                mSession.setSelection(items.get(position).SessionIndex);
                mLinkProfileSpinner.setSelection(items.get(position).LinkProfileIndex);
                mDutyCycleSpinner.setSelection(items.get(position).dutyCycleIndex);
            } else {
                detailLayout.setVisibility(View.GONE);
                contentView.setTextColor(Color.BLACK);
            }
            ProfileContentRE40.profileChnageToUser = false;
        }
    }
}
