package com.zebra.demo.rfidreader.settings;

import static com.zebra.demo.rfidreader.rfid.RFIDController.antennaRfConfig;
import static com.zebra.demo.rfidreader.rfid.RFIDController.mConnectedReader;
import static com.zebra.demo.rfidreader.rfid.RFIDController.singulationControl;

import android.util.Log;

import com.zebra.demo.rfidreader.rfid.RFIDController;
import com.zebra.rfid.api3.InvalidUsageException;
import com.zebra.rfid.api3.OperationFailureException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Objects;


public class ProfileContentRE40 {
    public static final int LayoutOne = 0;
    public static final  int LayoutTwo = 2;
    private int viewType;
    String Content;
    static Boolean profileChnageToUser = false;
    String profileId;

    public ProfileContentRE40(String content, String profileid) {
        Content = content;
        profileId = profileid;
        viewType = LayoutOne;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    // Variables for the item of second layout
    public String contentDetails;
    public int powerLevel ;
    public int dutyCycleIndex ;
    public int LinkProfileIndex;
    public int SessionIndex;
    private static int[] powerLevels;
    public boolean DPO_On = true;


    // public constructor for the second layout
    public ProfileContentRE40(String content, String profileid, String contentDetails,
                              int powerLevel, int dutyCycleIndex, int LinkProfileIndex, int SessionIndex, boolean DPO_On)
    {
        Content = content;
        profileId = profileid;
        this.contentDetails = contentDetails;
        this.powerLevel = powerLevel;
        this.dutyCycleIndex = dutyCycleIndex;
        this.LinkProfileIndex = LinkProfileIndex;
        this.viewType = LayoutTwo;
        this.SessionIndex = SessionIndex;
        this.DPO_On = DPO_On;
    }

    public int getViewType() {
        return viewType;
    }
    public static void UpdateActiveProfile() {
        String modelName = RFIDController.mConnectedDevice.getName();
        String model = RFIDController.mConnectedDevice.getDeviceCapability(modelName);
        if ("RE40".equalsIgnoreCase(model)) {
            try {
                mConnectedReader.Config.setRFIDProfile("USER_DEFINED");
            } catch (OperationFailureException | InvalidUsageException e) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                String stackTraceString = sw.toString();
                Log.e("TAG", stackTraceString);
            }
            ProfileFragmentRE40 contentRE40 = new ProfileFragmentRE40();

            if (mConnectedReader != null && mConnectedReader.isConnected()) {
                powerLevels = mConnectedReader.ReaderCapabilities.getTransmitPowerLevelValues();

                if (antennaRfConfig != null) {
                    contentRE40.mPowerlevel = powerLevels[antennaRfConfig.getTransmitPowerIndex()];
                    contentRE40.mLinkIndex = (int) antennaRfConfig.getrfModeTableIndex();//linkProfileUtil.getSelectedLinkProfilePosition(antennaRfConfig.getrfModeTableIndex());
                }
                if (singulationControl != null) {
                    contentRE40.mSessionIndex = singulationControl.getSession().getValue();
                    contentRE40.mInventoryState = Objects.requireNonNull(singulationControl).Action.getInventoryState().getValue();
                    contentRE40.mTagPopulation = singulationControl.getTagPopulation();
                    contentRE40.mSlFlag = singulationControl.Action.getSLFlag().getValue();
                }
            }
            contentRE40.StoreProfile();
            profileChnageToUser = true;
        }
    }

}