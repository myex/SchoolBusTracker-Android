package com.sjsu.edu.schoolbustracker.parentuser.model;

import java.util.UUID;

/**
 * Created by sai pranesh on 31-Mar-17.
 */


public class Driver extends Profile {

    private String mDrivingLicenceNumber;

    public String getDrivingLicenceNumber() {
        return mDrivingLicenceNumber;
    }

    public void setDrivingLicenceNumber(String drivingLicenceNumber) {
        mDrivingLicenceNumber = drivingLicenceNumber;
    }
}
