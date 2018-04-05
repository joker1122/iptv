package com.tosmart.tsresolve.resolvetable;


import com.tosmart.tsresolve.bean.MessageAboutProgram;
import com.tosmart.tsresolve.bean.MessageAboutTs;
import com.tosmart.tsresolve.resolveTs.GetCorrectSection;

import java.util.ArrayList;

/**
 * Created by PC-001 on 2018/3/22.
 */

public class Pmt {
    private static final int DESCRIPTORS_LENGTH_HIGH = 0x0F;
    private static final int DESCRIPTORS_LENGTH_LOW = 0xFF;
    private static final int ES_INFO_LENGTH_HIGH = 0x0F;
    private static final int ES_INFO_LENGTH_LOW = 0xFF;
    private static final int SERVICES_ID_HIGH = 0xFF;
    private static final int SERVICES_ID_LOW = 0xFF;
    private static final int ELEMENT_ID_HIGH = 0x1F;
    private static final int ELEMENT_ID_LOW = 0xFF;
    private static final int STREAM_TYPE_CHECK = 0xFF;
    private static final int MPEG_ONE_VIDEO = 0x01;
    private static final int MPEG_TWO_VIDEO = 0x02;
    private static final int AVC = 0x1B;
    private static final int MPEG_ONE_AUDIO = 0x03;
    private static final int MPEG_TWO_AUDIO = 0x04;
    private static final int MPEG_TWO_ACC = 0x0F;
    private static final int MPEG_FOUR_ACC = 0x11;
    private static final int AC_THREE = 0x81;
    private static final int DTS = 0x82;
    private static final int PEIVATE_DATA = 0x06;
    private static final int PEIVATE_SECTION = 0x05;
    private static final int MOVE_LEFT = 16 * 16;
    private static final int COMPONENTS_HEAD = 5;
    private static final int HEAD = 9;
    private static final int CRC = 4;
    private static final char[] STANDARD = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private static final int PID_LENGTH = 4;
    private static final int HEX = 16;
    private static final int TABLE_ID = 0x02;

    public static ArrayList<MessageAboutProgram> resolveAll(ArrayList<String> pmtPid, MessageAboutTs messageAboutTs) {
        ArrayList<MessageAboutProgram> messageAboutProgramList = new ArrayList<MessageAboutProgram>();
        if (!pmtPid.isEmpty()) {
            for (int i = 0; i < pmtPid.size(); i++) {
                MessageAboutProgram messageAboutProgram = new MessageAboutProgram();
                String pid = pmtPid.get(i);
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
                ArrayList<byte[]> sectionList = GetCorrectSection.getSpeciallySection(high, low, TABLE_ID, messageAboutTs, null);
                getMessage(messageAboutProgram, sectionList.get(0));
                messageAboutProgramList.add(messageAboutProgram);
            }
        }
        return messageAboutProgramList;
    }

    private static void getMessage(MessageAboutProgram messageAboutProgram, byte[] bytes) {
        int high = bytes[0] & SERVICES_ID_HIGH;
        int low = bytes[1] & SERVICES_ID_LOW;
        int descriptorLength = getDescriptorsLength(bytes);
        ArrayList<String> audioList = new ArrayList<String>();
        ArrayList<String> videoList = new ArrayList<String>();
        ArrayList<String> privateList = new ArrayList<String>();
        for (int i = descriptorLength + HEAD; i < bytes.length - CRC; ) {
            int esInfoLength = getEsInfoLength(bytes[i + 3], bytes[i + 4]);
            String elementPid = getId(bytes[i + 1] & ELEMENT_ID_HIGH, bytes[i + 2] & ELEMENT_ID_LOW);
            switch (bytes[i] & STREAM_TYPE_CHECK) {
                case MPEG_ONE_VIDEO:
                case MPEG_TWO_VIDEO:
                case AVC:
                    videoList.add(elementPid);
                    break;
                case MPEG_ONE_AUDIO:
                case MPEG_TWO_AUDIO:
                case MPEG_TWO_ACC:
                case MPEG_FOUR_ACC:
                case AC_THREE:
                case DTS:
                    audioList.add(elementPid);
                    break;
                case PEIVATE_DATA:
                case PEIVATE_SECTION:
                    privateList.add(elementPid);
                    break;
                default:
                    break;
            }
            i = i + COMPONENTS_HEAD + esInfoLength;
        }
        messageAboutProgram.setServiceId(getId(high, low));
        messageAboutProgram.setAudioPid(audioList);
        messageAboutProgram.setVideoPid(videoList);
        messageAboutProgram.setPrivate(privateList);
    }

    private static String getId(int high, int low) {
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

    private static int getDescriptorsLength(byte[] bytes) {
        int high = bytes[7] & DESCRIPTORS_LENGTH_HIGH;
        int low = bytes[8] & DESCRIPTORS_LENGTH_LOW;
        int length = high * MOVE_LEFT + low;
        return length;
    }

    private static int getEsInfoLength(byte high, byte low) {
        int h = high & ES_INFO_LENGTH_HIGH;
        int l = low & ES_INFO_LENGTH_LOW;
        int length = h * MOVE_LEFT + l;
        return length;
    }
}
