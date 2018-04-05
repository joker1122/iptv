package com.tosmart.tsresolve.resolvetable;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by PC-001 on 2018/3/22.
 */

public class Pat {
    private static final int SPACE_BYTE = 4;
    private static final int PROGRAM_NUMBER_FLAG = 0xFF;
    private static final int GET_HIGH = 2;
    private static final int GET_LOW = 3;
    private static final int GET_PMT_PID_HIGH = 0x1F;
    private static final int GET_PMT_PID_LOW = 0xFF;
    private static final int CRC_32 = 4;

    public static ArrayList<String> resolvePat(ArrayList<byte[]> sectionList) {
        ArrayList<String> pmtPID = new ArrayList<String>();
        for (int i = 0; i < sectionList.size(); i++) {
            for (int j = 5; j < sectionList.get(i).length - CRC_32; ) {
                int a = sectionList.get(i)[j] & PROGRAM_NUMBER_FLAG;
                if (a != 0) {
                    int high = sectionList.get(i)[j + GET_HIGH] & GET_PMT_PID_HIGH;
                    int low = sectionList.get(i)[j + GET_LOW] & GET_PMT_PID_LOW;
                    String programMapPid = getPid(high, low);
                    pmtPID.add(programMapPid);
                }
                j = j + SPACE_BYTE;
            }
        }
        return pmtPID;
    }

    private static String getPid(int high, int low) {
        StringBuffer pid = new StringBuffer();
        if (Integer.toHexString(high).length() != 2) {
            pid.append("0");
            pid.append(Integer.toHexString(high));
        } else {
            pid.append(Integer.toHexString(high));
        }
        if (Integer.toHexString(low).length() != 2) {
            pid.append("0");
            pid.append(Integer.toHexString(low));
        } else {
            pid.append(Integer.toHexString(low));
        }
        return new String(pid);
    }
}
