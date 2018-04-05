package com.tosmart.tsresolve.bean;

/**
 * Created by PC-001 on 2018/3/19.
 */

public class MessageAboutTs {
    private String mPath;
    private int mPacketLength;
    private int mStartIndex;

    public String getPath() {
        return mPath;
    }

    public void setPath(String path) {
        if (path.length() == 0) {
            path = "";
        } else {
            this.mPath = path;
        }

    }

    public int getPacketLength() {
        return mPacketLength;
    }

    public void setPacketLength(int packetLength) {
        this.mPacketLength = packetLength;
    }

    public int getStartIndex() {
        return mStartIndex;
    }

    public void setStartIndex(int startIndex) {
        this.mStartIndex = startIndex;
    }
}
