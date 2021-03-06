package com.sjsu.edu.schoolbustracker.common.model;

/**
 * Created by sai pranesh on 31-Mar-17.
 */

public class Student {

    private String mStudentName ;
    private String mStudentPicName; //For student image and for uniquely identifying
    private String mStudentUUID; //Unique Student ID
    private String mSchoolId;
    private String mSchoolName;
    private String mSchoolAddress;
    private ParentUsers mParentUsers;
    private String mRouteId;
    private String mRouteNumber;

    public String getRouteId() {
        return mRouteId;
    }

    public void setRouteId(String routeId) {
        mRouteId = routeId;
    }

    public String getRouteNumber() {
        return mRouteNumber;
    }

    public void setRouteNumber(String routeNumber) {
        mRouteNumber = routeNumber;
    }

    public String getSchoolId() {
        return mSchoolId;
    }

    public void setSchoolId(String schoolId) {
        mSchoolId = schoolId;
    }

    public Student(){} //Needed for firebase

    public String getStudentName() {
        return mStudentName;
    }

    public void setStudentName(String studentName) {
        mStudentName = studentName;
    }

    public String getStudentPicName() {
        return mStudentPicName;
    }

    public void setStudentPicName(String studentId) {
        mStudentPicName = studentId;
    }

    public String getStudentUUID() {
        return mStudentUUID;
    }

    public void setStudentUUID(String studentUUID) {
        mStudentUUID = studentUUID;
    }

    public String getSchoolName() {
        return mSchoolName;
    }

    public void setSchoolName(String schoolName) {
        mSchoolName = schoolName;
    }

    public String getSchoolAddress() {
        return mSchoolAddress;
    }

    public void setSchoolAddress(String schoolAddress) {
        mSchoolAddress = schoolAddress;
    }

    public ParentUsers getParentUsers() {
        return mParentUsers;
    }

    public void setParentUsers(ParentUsers parentUsers) {
        mParentUsers = parentUsers;
    }
}
