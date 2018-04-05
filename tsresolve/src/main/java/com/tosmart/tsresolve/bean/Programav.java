package com.tosmart.tsresolve.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PC-001 on 2018/3/30.
 */

public class Programav {
    private ArrayList<List<byte[]>> mAudioList;
    private ArrayList<List<byte[]>> mVideoList;

    public ArrayList<List<byte[]>> getAudioList() {
        return mAudioList;
    }

    public void setAudioList(ArrayList<List<byte[]>> audioList) {
        mAudioList = audioList;
    }

    public ArrayList<List<byte[]>> getVideoList() {
        return mVideoList;
    }

    public void setVideoList(ArrayList<List<byte[]>> videoList) {
        mVideoList = videoList;
    }
}
