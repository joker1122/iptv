package com.tosmart.tsresolve.resolvetable;


import android.content.Context;
import android.util.Log;

import com.tosmart.tsresolve.bean.MessageAboutProgram;
import com.tosmart.tsresolve.bean.MessageAboutTs;
import com.tosmart.tsresolve.resolveTs.EitGetSection;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by PC-001 on 2018/3/28.
 */

public class Eit {
    private static final int TABLE_ID = 0x4E;
    private static final int DESCRIPTOR_TAG = 0x4D;
    private static final int DESCRIPTOR_FLAG = 0xFF;
    private static final int DESCRIPTOE_LENGTH_FLAG = 0xFF;
    private static final int LOOP_LENGTH_HIGH = 0x0F;
    private static final int LOOP_LENGTH_LOW = 0xFF;
    private static final char[] STANDARD = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private static final int PID_LENGTH = 4;
    private static final int HEX = 16;
    private static final int TIME_REDIX = 60;
    private static final int START_TIME = 3;
    private static final int DURATION = 3;
    private static final int LOOP_LENGTH = 2;
    private static final int CODE = 3;
    private static final int CODE_FLAG = 0xFF;
    private static final int EVENT_NAME_LENGTH_FLAG = 0xFF;
    private static final int START_TIME_OFFSET = 15;
    private static final int TIME_FLAG = 0xFF;

    public static void solve(MessageAboutTs messageAboutTs, List<MessageAboutProgram> messageAboutProgramList) {
        for (int i = 0; i < messageAboutProgramList.size(); i++) {
            char[] pidCh = messageAboutProgramList.get(i).getServiceId().toCharArray();
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
            ArrayList<byte[]> sectionList = EitGetSection.getSpeciallySection(high, low, TABLE_ID, messageAboutTs);
            getMessage(sectionList, messageAboutProgramList.get(i));
        }
    }

    private static void getMessage(ArrayList<byte[]> arrayList, MessageAboutProgram messageAboutProgram) {
        if (arrayList.size() != 0) {
            byte[] startTimeBuffer = new byte[START_TIME];
            byte[] durationBuffer = new byte[DURATION];
            byte[] loopLengthBuffer = new byte[LOOP_LENGTH];
            byte[] code = new byte[CODE];
            System.arraycopy(arrayList.get(0), START_TIME_OFFSET, startTimeBuffer, 0, START_TIME);
            System.arraycopy(arrayList.get(0), START_TIME_OFFSET + START_TIME, durationBuffer, 0, DURATION);
            getTime(startTimeBuffer, durationBuffer, messageAboutProgram);
            System.arraycopy(arrayList.get(0), START_TIME_OFFSET + START_TIME + DURATION, loopLengthBuffer, 0, LOOP_LENGTH);
            int loopLength = ((loopLengthBuffer[0] & LOOP_LENGTH_HIGH) << 8) + loopLengthBuffer[1] & LOOP_LENGTH_LOW;
            for (int i = START_TIME_OFFSET + 8; i < START_TIME_OFFSET + 8 + loopLength; i++) {
                int descriptorLength = arrayList.get(0)[i + 1] & DESCRIPTOE_LENGTH_FLAG;
                if ((arrayList.get(0)[i] & DESCRIPTOR_FLAG) == DESCRIPTOR_TAG) {
                    int nameLength = arrayList.get(0)[i + 5] & EVENT_NAME_LENGTH_FLAG;
                    byte[] name = new byte[nameLength];
                    System.arraycopy(arrayList.get(0), i + 2, code, 0, CODE);
                    System.arraycopy(arrayList.get(0), i + 6, name, 0, nameLength);
                    String character = getCharacter(code);
                    try {
                        String programName = new String(name, character);
                        messageAboutProgram.setProgramName(programName);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    break;
                } else {
                    i = i + descriptorLength + 2;
                }
            }
        }
        if (arrayList.size() == 2) {
            byte[] loopLengthBuffer = new byte[LOOP_LENGTH];
            byte[] code = new byte[CODE];
            System.arraycopy(arrayList.get(1), START_TIME_OFFSET + START_TIME + DURATION, loopLengthBuffer, 0, LOOP_LENGTH);
            int loopLength = ((loopLengthBuffer[1] & LOOP_LENGTH_HIGH) << 8) + loopLengthBuffer[1] & LOOP_LENGTH_LOW;
            for (int i = START_TIME_OFFSET + 8; i < START_TIME_OFFSET + 8 + loopLength; i++) {
                int descriptorLength = arrayList.get(1)[i + 1] & DESCRIPTOE_LENGTH_FLAG;
                if ((arrayList.get(0)[i] & DESCRIPTOR_FLAG) == DESCRIPTOR_TAG) {
                    int nameLength = arrayList.get(1)[i + 5] & EVENT_NAME_LENGTH_FLAG;
                    byte[] nextName = new byte[nameLength];
                    System.arraycopy(arrayList.get(1), i + 2, code, 0, CODE);
                    System.arraycopy(arrayList.get(1), i + 6, nextName, 0, nameLength);
                    String character = getCharacter(code);
                    try {
                        String programName = new String(nextName, character);
                        messageAboutProgram.setNextProgramName(programName);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    break;
                } else {
                    i = i + descriptorLength + 2;
                }
            }
        }
        Log.d("filter", "hhh");
    }

    private static void getTime(byte[] startTime, byte[] duration, MessageAboutProgram messageAboutProgram) {
        int startH, startM, startS, endH, endM, endS;
        int sUp = 0;
        int mUp = 0;
        startH = byteToInt(startTime[0]);
        startM = byteToInt(startTime[1]);
        startS = byteToInt(startTime[2]);
        endS = startS + byteToInt(duration[2]);
        sUp = endS / TIME_REDIX;
        endS = endS % TIME_REDIX;
        endM = startM + byteToInt(duration[1]) + sUp;
        mUp = endM / TIME_REDIX;
        endM = endM % TIME_REDIX;
        endH = startH + mUp + byteToInt(duration[0]);
        String start = String.format("%d:%d:%d", startH, startM, startS);
        String end = String.format("%d:%d:%d", endH, endM, endS);
        messageAboutProgram.setEndTime(end);
        messageAboutProgram.setStartTime(start);
    }

    private static String getCharacter(byte[] bytes) {
        String character;
        switch (bytes[0]) {
            case 0x01:
                character = "ISO-8859-5";
                break;
            case 0x02:
                character = "ISO-8859-6";
                break;
            case 0x03:
                character = "ISO-8859-7";
                break;
            case 0x04:
                character = "ISO-8859-8";
                break;
            case 0x05:
                character = "ISO-8859-9";
                break;
            case 0x06:
                character = "ISO-8859-10";
                break;
            case 0x07:
                character = "ISO-8859-11";
                break;
            case 0x08:
                character = "ISO-8859-12";
                break;
            case 0x09:
                character = "ISO-8859-13";
                break;
            case 0x0A:
                character = "ISO-8859-14";
                break;
            case 0x0B:
                character = "ISO-8859-15";
                break;
            case 0x10:
                switch (bytes[2]) {
                    case 0x01:
                        character = "ISO-8859-1";
                        break;
                    case 0x02:
                        character = "ISO-8859-2";
                        break;
                    case 0x03:
                        character = "ISO-8859-3";
                        break;
                    case 0x04:
                        character = "ISO-8859-4";
                        break;
                    case 0x05:
                        character = "ISO-8859-5";
                        break;
                    case 0x06:
                        character = "ISO-8859-6";
                        break;
                    case 0x07:
                        character = "ISO-8859-7";
                        break;
                    case 0x08:
                        character = "ISO-8859-8";
                        break;
                    case 0x09:
                        character = "ISO-8859-9";
                        break;
                    case 0x0A:
                        character = "ISO-8859-10";
                        break;
                    case 0x0B:
                        character = "ISO-8859-11";
                        break;
                    case 0x0C:
                        character = "ISO-8859-12";
                        break;
                    case 0x0D:
                        character = "ISO-8859-13";
                        break;
                    case 0x0E:
                        character = "ISO-8859-14";
                        break;
                    case 0x0F:
                        character = "ISO-8859-15";
                        break;
                    default:
                        character = null;
                        break;
                }
                break;
            case 0x11:
            case 0x14:
            case 0x15:
                character = "ISO-10646";
                break;
            case 0x12:
                character = "KSX1001-2004";
                break;
            case 0x13:
                character = "GB-2312-1980";
                break;
            default:
                character = null;
                break;
        }
        return character;
    }

    private static int byteToInt(byte b) {
        int time = b & TIME_FLAG;
        int h = time / HEX;
        int l = time % HEX;
        return h * 10 + l;
    }
}
