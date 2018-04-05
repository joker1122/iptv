package com.tosmart.tsresolve.bean;

/**
 * Created by PC-001 on 2018/3/21.
 */

public class MessageAboutSection {
    private int mSectionNumber;
    private int mSectionLength;
    private int mVersionNumber;
    private int mLastSectionNumber;
    private int mTableId;

    public int getSectionNumber() {
        return mSectionNumber;
    }

    public void setSectionNumber(int sectionNumber) {
        mSectionNumber = sectionNumber;
    }

    public int getTableId() {
        return mTableId;
    }

    public void setTableId(int tableId) {
        mTableId = tableId;
    }

    public int getSectionLength() {
        return mSectionLength;
    }

    public void setSectionLength(int sectionLength) {
        mSectionLength = sectionLength;
    }

    public int getVersionNumber() {
        return mVersionNumber;
    }

    public void setVersionNumber(int versionNumber) {
        mVersionNumber = versionNumber;
    }

    public int getLastSectionNumber() {
        return mLastSectionNumber;
    }

    public void setLastSectionNumber(int lastSectionNumber) {
        mLastSectionNumber = lastSectionNumber;
    }
}
