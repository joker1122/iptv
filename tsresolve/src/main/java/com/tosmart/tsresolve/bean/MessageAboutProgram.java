package com.tosmart.tsresolve.bean;

import java.util.ArrayList;

/**
 * Created by PC-001 on 2018/3/27.
 */

public class MessageAboutProgram {
    private ArrayList<String> mVideoPid;
    private ArrayList<String> mAudioPid;
    private ArrayList<String> mPrivate;
    private String mProviderName;
    private String mName;
    private String mProgramName;
    private String mStartTime;
    private String mEndTime;
    private String mNextProgramName;
    private String mServiceId;

    public String getEndTime() {
        return mEndTime;
    }

    public void setEndTime(String endTime) {
        mEndTime = endTime;
    }

    public String getNextProgramName() {
        return mNextProgramName;
    }

    public void setNextProgramName(String nextProgramName) {
        mNextProgramName = nextProgramName;
    }

    public String getProgramName() {
        return mProgramName;
    }

    public void setProgramName(String programName) {
        mProgramName = programName;
    }

    public String getStartTime() {
        return mStartTime;
    }

    public void setStartTime(String startTime) {
        mStartTime = startTime;
    }

    public String getProviderName() {
        return mProviderName;
    }

    public void setProviderName(String providerName) {
        mProviderName = providerName;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public ArrayList<String> getPrivate() {
        return mPrivate;
    }

    public void setPrivate(ArrayList<String> aPrivate) {
        mPrivate = aPrivate;
    }

    public String getServiceId() {
        return mServiceId;
    }

    public void setServiceId(String serviceId) {
        mServiceId = serviceId;
    }

    public ArrayList<String> getVideoPid() {
        return mVideoPid;
    }

    public void setVideoPid(ArrayList<String> videoPid) {
        mVideoPid = videoPid;
    }

    public ArrayList<String> getAudioPid() {
        return mAudioPid;
    }

    public void setAudioPid(ArrayList<String> audioPid) {
        mAudioPid = audioPid;
    }
}
