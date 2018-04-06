package com.tosmart.tsresolve.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by PC-001 on 2018/3/27.
 */

public class MessageAboutProgram implements Parcelable {
    private ArrayList<String> mVideoPid = new ArrayList<>();
    private ArrayList<String> mAudioPid = new ArrayList<>();
    private ArrayList<String> mPrivate = new ArrayList<>();
    private String mProviderName;
    private String mName;
    private String mProgramName;
    private String mStartTime;
    private String mEndTime;
    private String mNextProgramName;
    private String mServiceId;

    public MessageAboutProgram() {

    }

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
        if (nextProgramName.length() == 0) {
            nextProgramName = "NO EPG";
        } else {
            mNextProgramName = nextProgramName;
        }
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(mVideoPid);
        dest.writeStringList(mAudioPid);
        dest.writeStringList(mPrivate);
        dest.writeString(mProviderName);
        dest.writeString(mName);
        dest.writeString(mProgramName);
        dest.writeString(mStartTime);
        dest.writeString(mEndTime);
        dest.writeString(mNextProgramName);
        dest.writeString(mServiceId);

    }

    public static final Parcelable.Creator<MessageAboutProgram> CREATOR = new Creator<MessageAboutProgram>() {
        @Override
        public MessageAboutProgram createFromParcel(Parcel source) {
            MessageAboutProgram messageAboutProgram = new MessageAboutProgram(source);
            return messageAboutProgram;
        }

        @Override
        public MessageAboutProgram[] newArray(int size) {
            return new MessageAboutProgram[0];
        }
    };

    private MessageAboutProgram(Parcel in) {
        mVideoPid = in.createStringArrayList();
        mAudioPid = in.createStringArrayList();
        mPrivate = in.createStringArrayList();
        mProviderName = in.readString();
        mName = in.readString();
        mProgramName = in.readString();
        mStartTime = in.readString();
        mEndTime = in.readString();
        mNextProgramName = in.readString();
        mServiceId = in.readString();
    }
}
