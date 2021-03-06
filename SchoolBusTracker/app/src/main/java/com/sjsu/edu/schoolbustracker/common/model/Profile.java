package com.sjsu.edu.schoolbustracker.common.model;

/**
 * Created by sai pranesh on 12-Apr-17.
 */

public class Profile {

    private String mName;
    private String mUUID;
    private String mEmailId;

    public String getPhone() {
        return mPhone;
    }

    public void setPhone(String phone) {
        mPhone = phone;
    }

    private String mPhone;

    public String getPhotoUri() {
        return mPhotoUri;
    }

    public void setPhotoUri(String photoUri) {
        mPhotoUri = photoUri;
    }

    private String mPhotoUri;

    Profile(){}

    public Profile( String name , String UUID, String emailId){
        mName = name;
        mUUID = UUID;
        mEmailId = emailId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getUUID() {
        return mUUID;
    }

    public void setUUID(String UUID) {
        mUUID = UUID;
    }

    public String getEmailId() {
        return mEmailId;
    }

    public void setEmailId(String emailId) {
        mEmailId = emailId;
    }
}
