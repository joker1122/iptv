package com.tosmart.tsresolve.resolvetable;


import com.tosmart.tsresolve.bean.MessageAboutProgram;
import com.tosmart.tsresolve.bean.MessageAboutTs;
import com.tosmart.tsresolve.resolveTs.GetCorrectSection;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PC-001 on 2018/3/22.
 */

public class Sdt {
    private static final int ACTUAL_TABLE_ID = 0x42;
    private static final int OTHER_TABLE_ID = 0x46;
    private static final int LOOP_LENGTH_HIGH = 0x0F;
    private static final int LOOP_LENGTH_LOW = 0xFF;
    private static final int SERVICE_PROVIDER_LENGTH = 0xFF;
    private static final int SERVICE_NAME_LENGTH = 0xFF;
    private static final int SERVICE_ID_START_INDEX = 8;
    private static final int SERVICE_ID_HIGH = 0xFF;
    private static final int SERVICE_ID_LOW = 0xFF;
    private static final int PID_LENGTH = 4;
    private static final char[] STANDARD = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private static final int HEX = 16;
    private static final int MOVE_LEFT = 16 * 16;
    private static final int CRC_BYTE = 4;
    private static final int LOOP_HEAD = 5;

    public static void resolve(ArrayList<MessageAboutProgram> list, MessageAboutTs messageAboutTs) {
        String pid = "0011";
        char[] pidCh = pid.toCharArray();
        int[] pidBuffer = new int[PID_LENGTH];
        for (int n = 0; n < pidBuffer.length; n++) {
            for (int m = 0; m < STANDARD.length; m++) {
                if (pidCh[n] == STANDARD[m]) {
                    pidBuffer[n] = m;
                    break;
                }
            }
        }
        int high = pidBuffer[0] * HEX + pidBuffer[1];
        int low = pidBuffer[2] * HEX + pidBuffer[3];
        ArrayList<byte[]> sectionList = GetCorrectSection.getSpeciallySection(high, low, ACTUAL_TABLE_ID, messageAboutTs, null);
        for (int i = 0; i < sectionList.size(); i++) {
            resolveOne(sectionList.get(i), list);
        }
    }

    private static void resolveOne(byte[] bytes, List<MessageAboutProgram> list) {
        for (int i = SERVICE_ID_START_INDEX; i < bytes.length - CRC_BYTE; ) {
            int serviceIdHigh = bytes[i] & SERVICE_ID_HIGH;
            int serviceIdLow = bytes[i + 1] & SERVICE_ID_LOW;
            int serviceId = serviceIdHigh * MOVE_LEFT + serviceIdLow;
            int high = bytes[i + 3] & LOOP_LENGTH_HIGH;
            int low = bytes[i + 4] & LOOP_LENGTH_LOW;
            int loopLength = high * MOVE_LEFT + low;
            MessageAboutProgram messageAboutProgram = checkServiceId(serviceId, list);
            if (messageAboutProgram != null) {
                int j = 0;
                while (j < loopLength) {
                    int serviceProviderLength = bytes[i + 8] & SERVICE_PROVIDER_LENGTH;
                    int serviceNameLength = bytes[i + 9 + serviceProviderLength] & SERVICE_NAME_LENGTH;
                    byte[] providerName = new byte[serviceProviderLength];
                    System.arraycopy(bytes, i + 9, providerName, 0, serviceProviderLength);
                    byte[] name = new byte[serviceNameLength];
                    System.arraycopy(bytes, i + 10 + serviceProviderLength, name, 0, serviceNameLength);
                    messageAboutProgram.setProviderName(new String(providerName));
                    messageAboutProgram.setName(new String(name));
                    j = j + 5 + serviceProviderLength + serviceNameLength;
                }
            }
            i = i + loopLength + LOOP_HEAD;
        }
    }

    private static MessageAboutProgram checkServiceId(int serviceId, List<MessageAboutProgram> list) {
        for (int i = 0; i < list.size(); i++) {
            MessageAboutProgram messageAboutProgram = list.get(i);
            String pid = messageAboutProgram.getServiceId();
            char[] pidCh = pid.toCharArray();
            int[] pidBuffer = new int[PID_LENGTH];
            for (int n = 0; n < pidBuffer.length; n++) {
                for (int m = 0; m < STANDARD.length; m++) {
                    if (pidCh[n] == STANDARD[m]) {
                        pidBuffer[n] = m;
                        break;
                    }
                }
            }
            int high = pidBuffer[0] * HEX + pidBuffer[1];
            int low = pidBuffer[2] * HEX + pidBuffer[3];
            if (high * MOVE_LEFT + low == serviceId) {
                return messageAboutProgram;
            }
        }
        return null;
    }
}
