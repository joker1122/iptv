package com.tosmart.tsresolve.resolveTs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.tosmart.tsresolve.bean.MessageAboutTs;

/**
 * Created by PC-001 on 2018/3/19.
 */

public class ResolveTs {
    private static final int PACKET_LENGTH_LONG = 204;
    private static final int PACKET_LENGTH_SHORT = 188;
    private static final int PACKET_START = 0x47;
    private static final int FLAG = 0xFF;
    private static final int MAX_TIMES = 10;
    private static final int INIT_STARTINDEX = -1;
    private static final int INIT_PACKET_LENGTH = 0;

    public static MessageAboutTs resolveFile(String itemPath) {

        MessageAboutTs mMessageAboutTs = new MessageAboutTs();
        int startIndex = INIT_STARTINDEX;
        int packetLength = INIT_PACKET_LENGTH;
        boolean is204 = false;
        File tsFile = new File(itemPath);
        int index;
        byte[] bufferShortLength = new byte[PACKET_LENGTH_SHORT];
        byte[] bufferLongLength = new byte[PACKET_LENGTH_LONG];
        /**
         * judgement 204
         */
        try {
            startIndex = INIT_STARTINDEX;
            packetLength = INIT_PACKET_LENGTH;
            FileInputStream tsInputStream = new FileInputStream(tsFile);
            try {
                while ((index = tsInputStream.read()) != -1) {
                    startIndex++;
                    if (index == PACKET_START) {
                        int times = 0;
                        int flag = 1;
                        for (; flag <= MAX_TIMES; flag++) {
                            tsInputStream.read(bufferLongLength, 0, bufferLongLength.length);
                            if (((bufferLongLength[PACKET_LENGTH_LONG - 1]) & FLAG) != PACKET_START && flag == 1) {
                                tsInputStream.close();
                                tsInputStream = new FileInputStream(tsFile);
                                byte[] buffer = new byte[startIndex];
                                tsInputStream.read(buffer);
                                break;
                            }
                            times = times + 1;
                        }
                        if (flag == 1) {
                            break;
                        }
                        if (times == MAX_TIMES) {
                            packetLength = PACKET_LENGTH_LONG;
                            is204 = true;
                        }
                        break;
                    }
                }
                tsInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            /**
             * judgement 188
             */
            if (!is204) {
                startIndex = INIT_STARTINDEX;
                packetLength = INIT_PACKET_LENGTH;
                FileInputStream tsInputStream1 = new FileInputStream(tsFile);
                try {
                    while ((index = tsInputStream1.read()) != -1) {
                        startIndex++;
                        if (index == PACKET_START) {
                            int times = 0;
                            int flag = 1;
                            for (; flag <= MAX_TIMES; flag++) {
                                tsInputStream1.read(bufferShortLength, 0, bufferShortLength.length);
                                if (((bufferShortLength[PACKET_LENGTH_SHORT - 1]) & FLAG) != PACKET_START && flag == 1) {
                                    tsInputStream.close();
                                    tsInputStream = new FileInputStream(tsFile);
                                    byte[] buffer = new byte[startIndex];
                                    tsInputStream.read(buffer);
                                    break;
                                }
                                times = times + 1;
                            }
                            if (flag == 1) {
                                break;
                            }
                            if (times == MAX_TIMES) {
                                packetLength = PACKET_LENGTH_SHORT;
                            } else {
                                startIndex = INIT_STARTINDEX;
                            }
                            break;
                        }
                    }
                    tsInputStream1.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mMessageAboutTs.setPath(itemPath);
        mMessageAboutTs.setPacketLength(packetLength);
        mMessageAboutTs.setStartIndex(startIndex);
        return mMessageAboutTs;
    }
}
