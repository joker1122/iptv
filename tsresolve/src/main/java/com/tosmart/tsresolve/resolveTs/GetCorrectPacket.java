package com.tosmart.tsresolve.resolveTs;

import android.util.Log;

import com.tosmart.tsresolve.bean.MessageAboutTs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by PC-001 on 2018/3/19.
 */

public class GetCorrectPacket {
    private static final int PACKET_LENGTH_LONG = 204;
    private static final int PACKET_LENGTH_SHORT = 188;
    private static final int HIGH_FLAG = 0x1F;
    private static final int LOW_FLAG = 0xFF;

    public static void getSpeciallyPacket(MessageAboutTs messageAboutTs, FileOutputStream fileOutputStream, int high, int low) {
        int number = 0;
        File tsFile = new File(messageAboutTs.getPath());
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(tsFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        byte[] buffer = new byte[messageAboutTs.getStartIndex()];
        switch (messageAboutTs.getPacketLength()) {
            case PACKET_LENGTH_SHORT:
                byte[] bufferShort = new byte[PACKET_LENGTH_SHORT];
                try {
                    fileInputStream.read(buffer);
                    while (fileInputStream.read(bufferShort) != -1) {
                        if (checkPidRightOrNot(bufferShort, high, low)) {
                            fileOutputStream.write(bufferShort);
                            number++;
                        }
                    }
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case PACKET_LENGTH_LONG:
                byte[] bufferLong = new byte[PACKET_LENGTH_LONG];
                try {
                    fileInputStream.read(buffer);
                    while (fileInputStream.read(bufferLong) != -1) {
                        if (checkPidRightOrNot(bufferLong, high, low)) {
                            fileOutputStream.write(bufferLong);
                            number++;
                        }
                    }
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
        try {
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean checkPidRightOrNot(byte[] buffer, int h, int l) {
        byte[] indexBuffer = new byte[2];
        System.arraycopy(buffer, 1, indexBuffer, 0, 2);
        int high = indexBuffer[0] & HIGH_FLAG;
        int low = indexBuffer[1] & LOW_FLAG;
        if (high == h && low == l) {
            return true;
        } else {
            return false;
        }
    }

}
