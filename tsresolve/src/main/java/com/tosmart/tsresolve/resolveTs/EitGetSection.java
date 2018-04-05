package com.tosmart.tsresolve.resolveTs;

import com.tosmart.tsresolve.bean.MessageAboutSection;
import com.tosmart.tsresolve.bean.MessageAboutTs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * Created by PC-001 on 2018/4/4.
 */

public class EitGetSection {
    private static final int EIT_PID_HIGH = 0x00;
    private static final int EIT_PID_LOW = 0x12;
    private static final int PACKET_LENGTH_LONG = 204;
    private static final int PACKET_LENGTH_SHORT = 188;
    private static final int ERROR_CODE = 16;
    private static final int HEAD_BYTE = 4;
    private static final int MOVE_RIGHT = 2;
    private static final int MOVE_LEFT = 16 * 16;
    private static final int POINTER_FIELD_FLAG = 0x40;
    private static final int POINTTE_LENGTH_FLAG = 0xFF;
    private static final int ADAPTATION_FIELD_FLAG = 0xFF;
    private static final int SERVICE_ID_FLAG = 0xFF;
    private static final int PAYLOAD_ONLY = 0x10;
    private static final int ADAPTATION_FIELD_ONLY = 0x20;
    private static final int BETWEEN = 0x30;
    private static final int HIGH_FLAG = 0x1F;
    private static final int LOW_FLAG = 0xFF;
    private static final int TABLE_ID_FLAG = 0xFF;
    private static final int SECTION_LENGTH_HIGH_FLAG = 0x0F;
    private static final int SECTION_LENGTH_LOW_FLAG = 0xFF;
    private static final int SECTION_NUMBER_FLAG = 0xFF;
    private static final int VERSION_NUMBER_FLAG = 0x3E;
    private static final int LAST_SECTION_NUMBER_FLAG = 0xFF;


    public static ArrayList<byte[]> getSpeciallySection(int serviceIdHigh, int serviceIdLow, int tableid, MessageAboutTs messageAboutTs) {
        int versionNumber = -1;
        boolean isFirst = true;
        boolean isEnd = false;
        int sectionCount = 0;
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
                    MessageAboutSection messageAboutSection;
                    ArrayList<byte[]> sectionList = new ArrayList<byte[]>();
                    while (!isEnd && fileInputStream.read(bufferShort) != -1) {
                        if (checkPidRightOrNot(bufferShort, EIT_PID_HIGH, EIT_PID_LOW)) {
                            if (checkPayload(bufferShort)) {
                                int packetLengthCount = 0;
                                int adaptation = checkAdaptationField(bufferShort);
                                int sectionStart = -1;
                                switch (adaptation) {
                                    case PAYLOAD_ONLY:
                                        int pointerFieldLength = bufferShort[4] & POINTTE_LENGTH_FLAG;
                                        sectionStart = HEAD_BYTE + pointerFieldLength + 1;
                                        break;
                                    case BETWEEN:
                                        int adaptationFieldLength = bufferShort[4] & ADAPTATION_FIELD_FLAG;
                                        int adaptionPointerFieldLength = bufferShort[4 + adaptationFieldLength + 1] & POINTTE_LENGTH_FLAG;
                                        sectionStart = HEAD_BYTE + adaptationFieldLength + 1 + adaptionPointerFieldLength + 1;
                                        break;
                                    default:
                                        break;
                                }
                                messageAboutSection = getGetMessageAboutSection(bufferShort, sectionStart);
                                if (messageAboutSection.getTableId() == tableid && checkServiceId(bufferShort, sectionStart + 3, serviceIdHigh, serviceIdLow)) {
                                    ByteBuffer section = ByteBuffer.allocate(messageAboutSection.getSectionLength());
                                    sectionCount = sectionCount + 1;
                                    if (isFirst) {
                                        versionNumber = messageAboutSection.getVersionNumber();
                                        isFirst = false;
                                    }
                                    if (messageAboutSection.getLastSectionNumber() + 1 == sectionCount) {
                                        isEnd = true;
                                    }
                                    if (messageAboutSection.getSectionLength() <= PACKET_LENGTH_SHORT - sectionStart - 3) {
                                        if (messageAboutSection.getVersionNumber() == versionNumber) {
                                            section.put(bufferShort, sectionStart + 3, messageAboutSection.getSectionLength());
                                        }
                                    } else {
                                        packetLengthCount = packetLengthCount + PACKET_LENGTH_SHORT - sectionStart - 3;
                                        section.put(bufferShort, sectionStart + 3, PACKET_LENGTH_SHORT - sectionStart - 3);
                                        while (packetLengthCount < messageAboutSection.getSectionLength() && fileInputStream.read(bufferShort) != -1) {
                                            if (checkPidRightOrNot(bufferShort, EIT_PID_HIGH, EIT_PID_LOW)) {
                                                int a = checkAdaptationField(bufferShort);
                                                switch (a) {
                                                    case PAYLOAD_ONLY:
                                                        packetLengthCount = packetLengthCount + PACKET_LENGTH_SHORT - HEAD_BYTE;
                                                        if (messageAboutSection.getVersionNumber() == versionNumber) {
                                                            section.put(bufferShort, HEAD_BYTE, (packetLengthCount < messageAboutSection.getSectionLength()) ? PACKET_LENGTH_SHORT - HEAD_BYTE : messageAboutSection.getSectionLength() - section.position());
                                                        }
                                                        break;
                                                    case ADAPTATION_FIELD_ONLY:
                                                        break;
                                                    case BETWEEN:
                                                        int length = bufferShort[4] & ADAPTATION_FIELD_FLAG;
                                                        packetLengthCount = packetLengthCount + PACKET_LENGTH_SHORT - length - HEAD_BYTE - 1;
                                                        if (messageAboutSection.getVersionNumber() == versionNumber) {
                                                            section.put(bufferShort, HEAD_BYTE + length + 1, (packetLengthCount < messageAboutSection.getSectionLength()) ? PACKET_LENGTH_SHORT - HEAD_BYTE - length - 1 : messageAboutSection.getSectionLength() - section.position());
                                                        }
                                                        break;
                                                    default:
                                                        break;
                                                }
                                            }
                                        }
                                    }
                                    sectionList.add(section.array());
                                } else {
                                    if (messageAboutSection.getSectionLength() > PACKET_LENGTH_SHORT - sectionStart) {
                                        packetLengthCount = packetLengthCount + PACKET_LENGTH_SHORT - sectionStart;
                                        while (packetLengthCount < messageAboutSection.getSectionLength() && fileInputStream.read(bufferShort) != -1) {
                                            int a = checkAdaptationField(bufferShort);
                                            switch (a) {
                                                case PAYLOAD_ONLY:
                                                    packetLengthCount = packetLengthCount + PACKET_LENGTH_SHORT - HEAD_BYTE;
                                                    break;
                                                case ADAPTATION_FIELD_ONLY:
                                                    break;
                                                case BETWEEN:
                                                    int length = bufferShort[4] & ADAPTATION_FIELD_FLAG;
                                                    packetLengthCount = packetLengthCount + PACKET_LENGTH_SHORT - length - HEAD_BYTE - 1;
                                                    break;
                                                default:
                                                    break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    fileInputStream.close();
                    return sectionList;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case PACKET_LENGTH_LONG:
                byte[] bufferLong = new byte[PACKET_LENGTH_LONG];
                try {
                    fileInputStream.read(buffer);
                    MessageAboutSection messageAboutSection;
                    ArrayList<byte[]> sectionList = new ArrayList<byte[]>();
                    while (!isEnd && fileInputStream.read(bufferLong) != -1) {
                        if (checkPidRightOrNot(bufferLong, EIT_PID_HIGH, EIT_PID_LOW)) {
                            if (checkPayload(bufferLong)) {
                                int packetLengthCount = 0;
                                int adaptation = checkAdaptationField(bufferLong);
                                int sectionStart = -1;
                                switch (adaptation) {
                                    case PAYLOAD_ONLY:
                                        int pointerFieldLength = bufferLong[4] & POINTTE_LENGTH_FLAG;
                                        sectionStart = HEAD_BYTE + pointerFieldLength + 1;
                                        break;
                                    case BETWEEN:
                                        int adaptationFieldLength = bufferLong[4] & ADAPTATION_FIELD_FLAG;
                                        int adaptionPointerFieldLength = bufferLong[4 + adaptationFieldLength + 1] & POINTTE_LENGTH_FLAG;
                                        sectionStart = HEAD_BYTE + adaptationFieldLength + 1 + adaptionPointerFieldLength + 1;
                                        break;
                                    default:
                                        break;
                                }
                                messageAboutSection = getGetMessageAboutSection(bufferLong, sectionStart);
                                if (messageAboutSection.getTableId() == tableid && checkServiceId(bufferLong, sectionStart + 3, serviceIdHigh, serviceIdLow)) {
                                    ByteBuffer section = ByteBuffer.allocate(messageAboutSection.getSectionLength());
                                    sectionCount = sectionCount + 1;
                                    if (isFirst) {
                                        versionNumber = messageAboutSection.getVersionNumber();
                                        isFirst = false;
                                    }
                                    if (messageAboutSection.getLastSectionNumber() + 1 == sectionCount) {
                                        isEnd = true;
                                    }
                                    if (messageAboutSection.getSectionLength() <= PACKET_LENGTH_LONG - sectionStart - 3 - ERROR_CODE) {
                                        if (messageAboutSection.getVersionNumber() == versionNumber) {
                                            section.put(bufferLong, sectionStart + 3, messageAboutSection.getSectionLength());
                                        }
                                    } else {
                                        packetLengthCount = packetLengthCount + PACKET_LENGTH_LONG - sectionStart - 3 - ERROR_CODE;
                                        section.put(bufferLong, sectionStart + 3, PACKET_LENGTH_LONG - sectionStart - 3 - ERROR_CODE);
                                        while (packetLengthCount < messageAboutSection.getSectionLength() && fileInputStream.read(bufferLong) != -1) {
                                            if (checkPidRightOrNot(bufferLong, EIT_PID_HIGH, EIT_PID_LOW)) {
                                                int a = checkAdaptationField(bufferLong);
                                                switch (a) {
                                                    case PAYLOAD_ONLY:
                                                        packetLengthCount = packetLengthCount + PACKET_LENGTH_LONG - HEAD_BYTE - ERROR_CODE;
                                                        if (messageAboutSection.getVersionNumber() == versionNumber) {
                                                            section.put(bufferLong, HEAD_BYTE, (packetLengthCount < messageAboutSection.getSectionLength()) ? PACKET_LENGTH_LONG - HEAD_BYTE - ERROR_CODE : messageAboutSection.getSectionLength() - section.position());
                                                        }
                                                        break;
                                                    case ADAPTATION_FIELD_ONLY:
                                                        break;
                                                    case BETWEEN:
                                                        int length = bufferLong[4] & ADAPTATION_FIELD_FLAG;
                                                        packetLengthCount = packetLengthCount + PACKET_LENGTH_LONG - length - HEAD_BYTE - 1 - ERROR_CODE;
                                                        if (messageAboutSection.getVersionNumber() == versionNumber) {
                                                            section.put(bufferLong, HEAD_BYTE + length + 1, (packetLengthCount < messageAboutSection.getSectionLength()) ? PACKET_LENGTH_LONG - HEAD_BYTE - length - 1 - ERROR_CODE : messageAboutSection.getSectionLength() - section.position());
                                                        }
                                                        break;
                                                    default:
                                                        break;
                                                }
                                            }
                                        }
                                    }
                                    sectionList.add(section.array());
                                } else {
                                    if (messageAboutSection.getSectionLength() > PACKET_LENGTH_LONG - sectionStart) {
                                        packetLengthCount = packetLengthCount + PACKET_LENGTH_LONG - sectionStart;
                                        while (packetLengthCount < messageAboutSection.getSectionLength() && fileInputStream.read(bufferLong) != -1) {
                                            int a = checkAdaptationField(bufferLong);
                                            switch (a) {
                                                case PAYLOAD_ONLY:
                                                    packetLengthCount = packetLengthCount + PACKET_LENGTH_LONG - HEAD_BYTE - ERROR_CODE;
                                                    break;
                                                case ADAPTATION_FIELD_ONLY:
                                                    break;
                                                case BETWEEN:
                                                    int length = bufferLong[4] & ADAPTATION_FIELD_FLAG;
                                                    packetLengthCount = packetLengthCount + PACKET_LENGTH_LONG - length - HEAD_BYTE - 1 - ERROR_CODE;
                                                    break;
                                                default:
                                                    break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    fileInputStream.close();
                    return sectionList;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
        return null;
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

    private static boolean checkPayload(byte[] buffer) {
        if ((buffer[1] & POINTER_FIELD_FLAG) == 0) {
            return false;
        }
        return true;
    }

    private static int checkAdaptationField(byte[] buffer) {
        return buffer[3] & BETWEEN;
    }

    private static boolean checkServiceId(byte[] bytes, int index, int high, int low) {
        int h = bytes[index] & SERVICE_ID_FLAG;
        int l = bytes[index + 1] & SERVICE_ID_FLAG;
        if (h == high && l == low) {
            return true;
        }
        return false;
    }

    private static MessageAboutSection getGetMessageAboutSection(byte[] buffer, int index) {
        MessageAboutSection messageAboutSection = new MessageAboutSection();
        messageAboutSection.setTableId(buffer[index] & TABLE_ID_FLAG);
        messageAboutSection.setLastSectionNumber(buffer[index + 7] & LAST_SECTION_NUMBER_FLAG);
        messageAboutSection.setVersionNumber((buffer[index + 5] & VERSION_NUMBER_FLAG) / MOVE_RIGHT);
        messageAboutSection.setSectionNumber(buffer[index + 6] & SECTION_NUMBER_FLAG);
        int h = buffer[index + 1] & SECTION_LENGTH_HIGH_FLAG;
        int l = buffer[index + 2] & SECTION_LENGTH_LOW_FLAG;
        int sectionLength = h * MOVE_LEFT + l;
        messageAboutSection.setSectionLength(sectionLength);
        return messageAboutSection;
    }
}
