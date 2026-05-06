package com.zebra.demo.rfidreader.settings;

import static android.content.Context.MODE_PRIVATE;
import static com.zebra.demo.rfidreader.rfid.RFIDController.TAG;
import static com.zebra.demo.rfidreader.rfid.RFIDController.dynamicPowerSettings;
import static com.zebra.demo.rfidreader.rfid.RFIDController.isLocatingTag;
import static com.zebra.demo.rfidreader.rfid.RFIDController.mConnectedReader;
import static com.zebra.demo.rfidreader.rfid.RFIDController.mIsInventoryRunning;
import static com.zebra.demo.rfidreader.settings.ProfileContent.linkProfileUtil;
import static com.zebra.rfid.api3.ENUM_DUTY_CYCLE.MAX;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.zebra.demo.ActiveDeviceActivity;
import com.zebra.demo.R;
import com.zebra.demo.application.Application;
import com.zebra.demo.rfidreader.common.Constants;
import com.zebra.demo.rfidreader.common.LinkProfileUtil;
import com.zebra.demo.rfidreader.rfid.RFIDController;
import com.zebra.rfid.api3.Antennas;
import com.zebra.rfid.api3.DYNAMIC_POWER_OPTIMIZATION;
import com.zebra.rfid.api3.ENUM_DUTY_CYCLE;
import com.zebra.rfid.api3.INVENTORY_STATE;
import com.zebra.rfid.api3.InvalidUsageException;
import com.zebra.rfid.api3.OperationFailureException;
import com.zebra.rfid.api3.SESSION;
import com.zebra.rfid.api3.SL_FLAG;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProfileFragmentRE40 extends BackPressedFragment  {

    private static final String TAG = "ProfileFragmentRE40";
    private static ProfileRecyclerViewAdapterRE40 profileViewAdapter;
    private ProfileRecyclerViewAdapter.OnListFragmentInteractionListener mListener;
    public String mProfileName;
    public int mPowerlevel;
    public int mLinkIndex;
    public int mSessionIndex;
    public int mDutyCycle;
    public int mTagPopulation;
    public int mSlFlag;
    public int mInventoryState;
    public Boolean mDPO;
    ArrayAdapter<String> linkAdapter;
    ArrayAdapter<CharSequence> DCAdapter;
    ArrayAdapter<CharSequence> sessionAdapter;
    private static Context curr_context;

    public ProfileFragmentRE40() {
        // Required empty public constructor
    }

    public static ProfileFragmentRE40 newInstance() {
        ProfileFragmentRE40 fragment = new ProfileFragmentRE40();
        return fragment;
    }

    public static ProfileFragmentRE40 getInstance() {
        ProfileFragmentRE40 fragment = new ProfileFragmentRE40();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_list_re40, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.readerprofilelistRFD40);
        Context context = view.getContext();

        List<ProfileContentRE40> items = new ArrayList<ProfileContentRE40>();
        if (items.size() == 0) {
            items.add(new ProfileContentRE40("Fastest Read", "FASTEST_READ"));
            items.add(new ProfileContentRE40("Cycle Count", "CYCLE_COUNT"));
            items.add(new ProfileContentRE40("Max Range", "MAX_RANGE"));
            items.add(new ProfileContentRE40("Optimal Battery", "OPTIMAL_BATTERY"));
            items.add(new ProfileContentRE40("Balanced Performance", "BALANCED_PERFORMANCE"));
            items.add(new ProfileContentRE40("User Defined", "USER_DEFINED", "Custom profile \nUsed for custom requirement ", 240,0, 0, 1, true));
        }
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        profileViewAdapter = new ProfileRecyclerViewAdapterRE40(context, items);
        recyclerView.setAdapter(profileViewAdapter);
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                mLayoutManager.getOrientation());
        mDividerItemDecoration.setDrawable((getContext().getResources().getDrawable(R.drawable.profile_divider)));
        recyclerView.addItemDecoration(mDividerItemDecoration);
        return view;
    }

    public void GetStoredProfile() {
        SharedPreferences settingsprofile = curr_context.getSharedPreferences(Constants.APP_SETTINGS_PROFILE + "USER_DEFINED", 0);
        mPowerlevel = settingsprofile.getInt(Constants.PROFILE_POWER_RE40, 200);
        mLinkIndex = settingsprofile.getInt(Constants.PROFILE_LINK_PROFILE, 1);
        mSessionIndex = settingsprofile.getInt(Constants.PROFILE_SESSION, 0);
        mTagPopulation = settingsprofile.getInt(Constants.TAG_POPULATION,10);
        mSlFlag = settingsprofile.getInt(Constants.SL_FLAG,2);
        mDPO = settingsprofile.getBoolean(Constants.PROFILE_DPO, true);
        mInventoryState = settingsprofile.getInt(Constants.INVENTORY_STATE,2);
        mDutyCycle = settingsprofile.getInt(Constants.PROFILE_DUTY_CYCLE,0);
    }

    public void StoreProfile() {
        SharedPreferences settings = curr_context.getSharedPreferences(Constants.APP_SETTINGS_PROFILE + "USER_DEFINED", MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(Constants.PROFILE_POWER_RE40, mPowerlevel);
        editor.putInt(Constants.PROFILE_LINK_PROFILE, mLinkIndex);
        editor.putInt(Constants.PROFILE_SESSION, mSessionIndex);
        editor.putInt(Constants.TAG_POPULATION,mTagPopulation);
        editor.putInt(Constants.SL_FLAG,mSlFlag);
        editor.putInt(Constants.INVENTORY_STATE,mInventoryState);
        editor.putInt(Constants.PROFILE_DUTY_CYCLE,mDutyCycle);
        //editor.putBoolean(Constants.PROFILE_DPO, mDPO);
        editor.apply();

    }



    public void LoadDefaultProfiles(Context context) {
        List<ProfileContentRE40> items = new ArrayList<ProfileContentRE40>();
        int index = 0;
        curr_context = context;

        if (items.size() == 0) {
            items.add(new ProfileContentRE40("Fastest Read", "FASTEST_READ"));
            items.add(new ProfileContentRE40("Cycle Count", "CYCLE_COUNT"));
            items.add(new ProfileContentRE40("Max Range", "MAX_RANGE"));
            items.add(new ProfileContentRE40("Optimal Battery", "OPTIMAL_BATTERY"));
            items.add(new ProfileContentRE40("Balanced Performance", "BALANCED_PERFORMANCE"));
            items.add(new ProfileContentRE40("User Defined", "USER_DEFINED", "Custom profile \nUsed for custom requirement ", 240,0, 0, 1, true));
        }

        profileViewAdapter = new ProfileRecyclerViewAdapterRE40(context, items);

        String defaultProfile = null;
        try {
            defaultProfile = mConnectedReader.Config.getDefaultProfile();
        } catch (OperationFailureException | InvalidUsageException e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            String stackTraceString = sw.toString();
            Log.e("TAG", stackTraceString);        }
        switch (defaultProfile) {
            case "FASTEST_READ":
                RFIDController.ActiveProfile = new ProfileContent(context).new ProfilesItem(String.valueOf(index), "Fastest Read", "Read as many tags as fast as possible", 200, 2, 0, false, false, false);
                break;
            case "CYCLE_COUNT":
                RFIDController.ActiveProfile = new ProfileContent(context).new ProfilesItem(String.valueOf(++index), "Cycle Count", "Read as many unique tags possible", 240, 0, 2, false, false, false);
                break;
            case "MAX_RANGE":
                RFIDController.ActiveProfile = new ProfileContent(context).new ProfilesItem(String.valueOf(++index), "Max Range", "Reads as many tags as fast as possible in longer range", 240, 2, 0, false, false, false);
                break;
            case "OPTIMAL_BATTERY":
                RFIDController.ActiveProfile = new ProfileContent(context).new ProfilesItem(String.valueOf(++index), "Optimal Battery", "Gives best battery life", 200, 0, 0, false, true, false);
                break;
            case "BALANCED_PERFORMANCE":
                RFIDController.ActiveProfile = new ProfileContent(context).new ProfilesItem(String.valueOf(++index), "Balanced Performance", "Maintains balance between performance and battery life", 200, 0, 0, false, true, false);
                break;
            case "USER_DEFINED":
                RFIDController.ActiveProfile = new ProfileContent(context).new ProfilesItem(String.valueOf(++index), "User Defined", "Custom profile \nUsed for custom requirement", 200, 0, 0, false, true, true);
                break;
            default:
                RFIDController.ActiveProfile = new ProfileContent(context).new ProfilesItem(String.valueOf(++index), "User Defined", "Custom profile \nUsed for custom requirement", 200, 0, 0, false, true, true);
                break;
        }
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        linkProfileUtil = LinkProfileUtil.getInstance();

        linkAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_small_font, linkProfileUtil.getSimpleProfiles());
        linkAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sessionAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.session_array, R.layout.custom_spinner_layout);
        sessionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        DCAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.dyty_cycle, R.layout.custom_spinner_layout);
        DCAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        profileViewAdapter.mLinkProfileAdapter = linkAdapter;
        profileViewAdapter.mSessionAdapter = sessionAdapter;
        profileViewAdapter.mDutyCycleAdapter = DCAdapter;

        if (SettingsDetailActivity.mSettingOnFactory == false) {
            Button button = getActivity().findViewById(R.id.profilebutton);
            button.setVisibility(View.INVISIBLE);
        }
    }

    public void onBackPressed() {
        if (SettingsDetailActivity.mSettingOnFactory == true)
            return;
        saveDataToAntenna();
        if (getActivity() instanceof SettingsDetailActivity)
            ((SettingsDetailActivity) getActivity()).callBackPressed();
        else
            ((ActiveDeviceActivity) getActivity()).callBackPressed();
    }

    public void saveDataToAntenna() {
        Application.cycleCountProfileData = null;
        if (mConnectedReader != null && mConnectedReader.isConnected()) {
            String mRfidDefaultProfile = null;
            try {
                mRfidDefaultProfile = mConnectedReader.Config.getDefaultProfile();
            } catch (OperationFailureException | InvalidUsageException e) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                String stackTraceString = sw.toString();
                Log.e("TAG", stackTraceString);            }
            if (!(mIsInventoryRunning || isLocatingTag) && mConnectedReader.Config.Antennas != null) {
                if (Objects.equals(mRfidDefaultProfile, "USER_DEFINED")) {
                    GetStoredProfile();
                    if(!profileViewAdapter.mUserProfileAdapter.isEmpty()) {
                        for (ProfileRecyclerViewAdapterRE40.UserProfileViewHolder holder : profileViewAdapter.mUserProfileAdapter) {
                            if (!holder.mTextPower.getText().toString().equals("")) {
                                mPowerlevel = Integer.parseInt(holder.mTextPower.getText().toString());
                                mDutyCycle = holder.mDutyCycleSpinner.getSelectedItemPosition();
                                mLinkIndex = holder.mLinkProfileSpinner.getSelectedItemPosition();
                                mLinkIndex = LinkProfileUtil.getInstance().getSimpleProfileModeIndex(mLinkIndex);
                                mSessionIndex = holder.mSession.getSelectedItemPosition();
                                mDPO = holder.mDynamicPower.isChecked();
                            }
                        }
                    }

                        try {
                            for(short i = 1; i <= mConnectedReader.ReaderCapabilities.getNumAntennaSupported(); ++i) {
                                Antennas.AntennaRfConfig antennaRfConfig;
                                antennaRfConfig = mConnectedReader.Config.Antennas.getAntennaRfConfig(i);
                                int maxPower = LinkProfileUtil.getInstance().getMaxPower();
                                if (mPowerlevel > maxPower) {
                                    mPowerlevel = maxPower;
                                    if(getActivity() != null) {
                                        getActivity().runOnUiThread(() ->
                                                Toast.makeText(getActivity(), getResources().getString(R.string.invalid_antenna_power), Toast.LENGTH_SHORT).show());
                                    }
                                }
                                int powerIndex = LinkProfileUtil.getInstance().getPowerLevelIndex(mPowerlevel);
                                antennaRfConfig.setTransmitPowerIndex(powerIndex);
                                if (antennaRfConfig.getrfModeTableIndex() != mLinkIndex) {
                                    antennaRfConfig.setTari(0);
                                    antennaRfConfig.setrfModeTableIndex(mLinkIndex);
                                }
                                mConnectedReader.Config.Antennas.setAntennaRfConfig(i, antennaRfConfig);
                                RFIDController.antennaRfConfig = antennaRfConfig;

                                // Singulation
                                Antennas.SingulationControl singulationControl;
                                singulationControl = mConnectedReader.Config.Antennas.getSingulationControl(i);
                                singulationControl.setSession(SESSION.GetSession(mSessionIndex));
                                singulationControl.setTagPopulation((short) mTagPopulation);
                                singulationControl.Action.setSLFlag(SL_FLAG.GetSLFlag(mSlFlag));
                                singulationControl.Action.setInventoryState(INVENTORY_STATE.GetInventoryState(mInventoryState));
                                mConnectedReader.Config.Antennas.setSingulationControl(i, singulationControl);
                                RFIDController.singulationControl = singulationControl;

                                // DutyCycle
                                mConnectedReader.Config.setUserDutyCycle(ENUM_DUTY_CYCLE.values()[mDutyCycle]);
                            }
                            RFIDController.ActiveProfile = new ProfileContent(curr_context).new ProfilesItem(String.valueOf(5), "User Defined", "Custom profile \nUsed for custom requirement", 200, 0, 0, false, true, true);
                            StoreProfile();
                            // DPO
                            if (mDPO)
                                mConnectedReader.Config.setDPOState(DYNAMIC_POWER_OPTIMIZATION.ENABLE);
                            else
                                mConnectedReader.Config.setDPOState(DYNAMIC_POWER_OPTIMIZATION.DISABLE);
                            dynamicPowerSettings = mConnectedReader.Config.getDPOState();

                        } catch (InvalidUsageException e) {
                            if( e!= null && e.getStackTrace().length>0){ Log.e(TAG, e.getStackTrace()[0].toString()); }
                        } catch (OperationFailureException e) {
                            if( e!= null && e.getStackTrace().length>0){ Log.e(TAG, e.getStackTrace()[0].toString()); }
                        }


                } else if (!Objects.equals(mRfidDefaultProfile, "USER_DEFINED")) {
                    try {
                        String mRfidProfile = mConnectedReader.Config.getRFIDProfile();
                        JSONArray jsonArrayValue = new JSONArray(mRfidProfile);
                        for (int i = 0; i < jsonArrayValue.length(); i++) {
                            JSONObject jsonObjectValue = jsonArrayValue.getJSONObject(i);
                            mProfileName = jsonObjectValue.getString("profile_name");
                            if (mProfileName.equals(mRfidDefaultProfile)) {
                                if (mRfidDefaultProfile.equals("CYCLE_COUNT"))
                                    RFIDController.ActiveProfile = new ProfileContent(curr_context). new ProfilesItem( String.valueOf(1), "Cycle Count", "Read as many unique tags possible", 240, 0, 2, false, false, false);
                                else
                                    RFIDController.ActiveProfile = new ProfileContent(curr_context). new ProfilesItem( String.valueOf(4), "Balanced performance", "Maintains balance between perfomance and battery life", 200, 0, 0, false, false, false);

                                String sessionIndex = jsonObjectValue.getString("session");
                                mSessionIndex = Integer.parseInt(sessionIndex.substring(sessionIndex.length() -1));
                                mTagPopulation = jsonObjectValue.getInt("Tag_population");
                                mPowerlevel = jsonObjectValue.getInt("tx") * 10;
                                mLinkIndex = jsonObjectValue.getInt("link");
                                mLinkIndex = LinkProfileUtil.getInstance().getSimpleProfileModeIndex(mLinkIndex);
                                mInventoryState = jsonObjectValue.getInt("inventory_state");
                            }
                        }
                    } catch (JSONException | OperationFailureException | InvalidUsageException e) {
                        StringWriter sw = new StringWriter();
                        PrintWriter pw = new PrintWriter(sw);
                        e.printStackTrace(pw);
                        String stackTraceString = sw.toString();
                        Log.e("TAG", stackTraceString);                    }

                    try {
                        for(short i = 1; i <= mConnectedReader.ReaderCapabilities.getNumAntennaSupported(); ++i) {
                            // Antenna
                            Antennas.AntennaRfConfig antennaRfConfig;
                            antennaRfConfig = mConnectedReader.Config.Antennas.getAntennaRfConfig(i);
                            int maxPower = LinkProfileUtil.getInstance().getMaxPower();
                            if (mPowerlevel > maxPower) {
                                mPowerlevel = maxPower;
                            }
                            int powerIndex = LinkProfileUtil.getInstance().getPowerLevelIndex(mPowerlevel);
                            antennaRfConfig.setTransmitPowerIndex(powerIndex);
                            if (antennaRfConfig.getrfModeTableIndex() != mLinkIndex) {
                                antennaRfConfig.setTari(0);
                                antennaRfConfig.setrfModeTableIndex(mLinkIndex);
                            }
                            mConnectedReader.Config.Antennas.setAntennaRfConfig(i, antennaRfConfig);
                            RFIDController.antennaRfConfig = antennaRfConfig;

                            // Singulation
                            Antennas.SingulationControl singulationControl;
                            singulationControl = mConnectedReader.Config.Antennas.getSingulationControl(i);
                            singulationControl.setSession(SESSION.GetSession(mSessionIndex));
                            singulationControl.Action.setInventoryState(INVENTORY_STATE.GetInventoryState(mInventoryState));
                            mConnectedReader.Config.Antennas.setSingulationControl(i, singulationControl);
                            RFIDController.singulationControl = singulationControl;

                            //Tag population
                            singulationControl.setTagPopulation((short) mTagPopulation);
                        }

                    } catch (InvalidUsageException | OperationFailureException e) {
                        StringWriter sw = new StringWriter();
                        PrintWriter pw = new PrintWriter(sw);
                        e.printStackTrace(pw);
                        String stackTraceString = sw.toString();
                        Log.e("TAG", stackTraceString);                    }
                }
            }
        }
    }
}