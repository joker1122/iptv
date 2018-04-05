package com.tosmart.tsresolve.resolveTs;

import android.os.Environment;
import android.util.Log;

import com.tosmart.tsresolve.bean.MessageAboutProgram;
import com.tosmart.tsresolve.bean.MessageAboutTs;
import com.tosmart.tsresolve.bean.Programav;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by PC-001 on 2018/3/29.
 */

public class GetPESPacket {

    private static final int ERROR_CODE = 16;
    private static final int HEAD_BYTE = 4;
    private static final int MOVE_LEFT = 16 * 16;
    private static final int FORMER_BYTES = 9;
    private static final int PACKET_FORMER_BYTES = 6;
    private static final int POINTER_FIELD_FLAG = 0x40;
    private static final int PES_PACKET_LENGTH_HIGH = 0xFF;
    private static final int PES_PACKET_LENGTH_LOW = 0xFF;
    private static final int ADAPTATION_FIELD_FLAG = 0xFF;
    private static final int PAYLOAD_ONLY = 0x10;
    private static final int ADAPTATION_FIELD_ONLY = 0x20;
    private static final int BETWEEN = 0x30;
    private static final int HEAD_LENGTH = 0xFF;
    private static final char[] STANDARD = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private static final int HEX = 16;
    private static final int PID_LENGTH = 4;
    private static final int PACKET_LENGTH_LONG = 204;
    private static final int PACKET_LENGTH_SHORT = 188;
    private static final int HIGH_FLAG = 0x1F;
    private static final int LOW_FLAG = 0xFF;
    private static final String FILE_PATH = Environment.getExternalStorageDirectory().getPath() + "/ts/out/";

    public static void resolveAll(List<MessageAboutProgram> list, MessageAboutTs messageAboutTs) {
        Programav programav = new Programav();
        resolvePES(list.get(5), messageAboutTs, programav);
        Log.d("filter", "END");
    }

    private static void resolvePES(MessageAboutProgram messageAboutProgram, MessageAboutTs messageAboutTs, Programav programav) {
        List<String> videoPid = messageAboutProgram.getVideoPid();
        List<String> audioPid = messageAboutProgram.getAudioPid();
        ArrayList<List<byte[]>> video = new ArrayList<List<byte[]>>();
        ArrayList<List<byte[]>> audio = new ArrayList<List<byte[]>>();
        for (int i = 0; i < videoPid.size(); i++) {
            String pid = videoPid.get(i);
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
            File file;
            FileOutputStream fileOutputStream = null;
            do {
                String path = FILE_PATH + RandomString.random() + "___" + messageAboutProgram.getServiceId() + ".ts";
                file = new File(path);
            } while (file.exists());
            try {
                file.createNewFile();
                fileOutputStream = new FileOutputStream(file);

            } catch (IOException e) {
                e.printStackTrace();
            }
            int high = pidBuffer[0] * HEX + pidBuffer[1];
            int low = pidBuffer[2] * HEX + pidBuffer[3];
            getPESPacket(high, low, messageAboutTs, fileOutputStream);
        }
        for (int i = 0; i < audioPid.size(); i++) {
            String pid = audioPid.get(i);
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
            File file;
            FileOutputStream fileOutputStream = null;
            do {
                String path = FILE_PATH + RandomString.random() + "___" + messageAboutProgram.getServiceId() + ".ts";
                file = new File(path);
            } while (file.exists());
            try {
                file.createNewFile();
                fileOutputStream = new FileOutputStream(file);

            } catch (IOException e) {
                e.printStackTrace();
            }
            int high = pidBuffer[0] * HEX + pidBuffer[1];
            int low = pidBuffer[2] * HEX + pidBuffer[3];
//            List<byte[]> bytesList = getPESPacket(high, low, messageAboutTs, fileOutputStream);
//            audio.add(bytesList);
        }
        programav.setAudioList(audio);
        programav.setVideoList(video);
    }

    private static void getPESPacket(int high, int low, MessageAboutTs messageAboutTs, FileOutputStream fileOutputStream) {
        File tsFile = new File(messageAboutTs.getPath());
        boolean isUnknow = false;
        byte[] bytes = null;
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
                    ArrayList<byte[]> pesList = new ArrayList<byte[]>();
                    while (fileInputStream.read(bufferShort) != -1) {
                        if (checkPidRightOrNot(bufferShort, high, low)) {
                            if (checkPayload(bufferShort)) {
                                int adaptation = checkAdaptationField(bufferShort);
                                int pesStart = -1;
                                switch (adaptation) {
                                    case PAYLOAD_ONLY:
                                        pesStart = 4;
                                        break;
                                    case BETWEEN:
                                        int adaptationFieldLength = bufferShort[4] & ADAPTATION_FIELD_FLAG;
                                        pesStart = HEAD_BYTE + adaptationFieldLength + 1;
                                        break;
                                    default:
                                        break;
                                }
                                int headLength = bufferShort[pesStart + 8] & HEAD_LENGTH;
                                int pesPacketLengthHigh = bufferShort[pesStart + 4] & PES_PACKET_LENGTH_HIGH;
                                int pesPacketLengthLow = bufferShort[pesStart + 5] & PES_PACKET_LENGTH_LOW;
                                int pesLengthCount = -1;
                                int pesPacketLength = pesPacketLengthHigh * MOVE_LEFT + pesPacketLengthLow;
                                if (pesPacketLength == 0) {
                                    isUnknow = true;
                                }
                                if (!isUnknow) {
                                    fileOutputStream.write(bufferShort, pesStart + FORMER_BYTES + headLength, pesPacketLength < (PACKET_LENGTH_SHORT - pesStart - PACKET_FORMER_BYTES) ? pesPacketLength : PACKET_LENGTH_SHORT - pesStart - headLength - FORMER_BYTES);
                                    pesLengthCount = pesLengthCount + 1 + PACKET_LENGTH_SHORT - pesStart - PACKET_FORMER_BYTES;
                                } else {
                                    fileOutputStream.write(bufferShort, pesStart + FORMER_BYTES + headLength, PACKET_LENGTH_SHORT - pesStart - headLength - FORMER_BYTES);
                                }
                                while (pesLengthCount < pesPacketLength && fileInputStream.read(bufferShort) != -1) {
                                    if (checkPidRightOrNot(bufferShort, high, low)) {
                                        switch (checkAdaptationField(bufferShort)) {
                                            case PAYLOAD_ONLY:
                                                if (!isUnknow) {
                                                    fileOutputStream.write(bufferShort, HEAD_BYTE, pesPacketLength < (pesLengthCount + PACKET_LENGTH_SHORT - HEAD_BYTE) ? pesPacketLength - pesLengthCount : PACKET_LENGTH_SHORT - HEAD_BYTE);
                                                    pesLengthCount = pesLengthCount + PACKET_LENGTH_SHORT - HEAD_BYTE;
                                                } else {
                                                    fileOutputStream.write(bufferShort, HEAD_BYTE, PACKET_LENGTH_SHORT - HEAD_BYTE);
                                                }
                                                break;
                                            case BETWEEN:
                                                int adaptationFieldLength = bufferShort[4] & ADAPTATION_FIELD_FLAG;
                                                if (!isUnknow) {
                                                    fileOutputStream.write(bufferShort, HEAD_BYTE + 1 + adaptationFieldLength, pesPacketLength < (pesLengthCount + PACKET_LENGTH_SHORT - HEAD_BYTE - adaptationFieldLength - 1) ? pesPacketLength - pesLengthCount : PACKET_LENGTH_SHORT - HEAD_BYTE - adaptationFieldLength - 1);
                                                    pesLengthCount = pesLengthCount + PACKET_LENGTH_SHORT - HEAD_BYTE - adaptationFieldLength - 1;
                                                } else {
                                                    fileOutputStream.write(bufferShort, HEAD_BYTE + 1 + adaptationFieldLength, PACKET_LENGTH_SHORT - HEAD_BYTE - adaptationFieldLength - 1);
                                                }
                                                break;
                                            case ADAPTATION_FIELD_ONLY:
                                                break;
                                            default:
                                                break;
                                        }
                                    }
                                }
                                break;
                            }
                        }
                    }
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case PACKET_LENGTH_LONG:
                byte[] bufferLong = new byte[PACKET_LENGTH_LONG];
                try {
                    fileInputStream.read(buffer);
                    ArrayList<byte[]> pesList = new ArrayList<byte[]>();
                    while (fileInputStream.read(bufferLong) != -1) {
                        if (checkPidRightOrNot(bufferLong, high, low)) {
                            if (checkPayload(bufferLong)) {
                                int adaptation = checkAdaptationField(bufferLong);
                                int pesStart = -1;
                                switch (adaptation) {
                                    case PAYLOAD_ONLY:
                                        pesStart = HEAD_BYTE;
                                        break;
                                    case BETWEEN:
                                        int adaptationFieldLength = bufferLong[4] & ADAPTATION_FIELD_FLAG;
                                        pesStart = HEAD_BYTE + adaptationFieldLength + 1;
                                        break;
                                    default:
                                        break;
                                }
                                int headLength = bufferLong[pesStart + 8] & HEAD_LENGTH;
                                int pesPacketLengthHigh = bufferLong[pesStart + 4] & PES_PACKET_LENGTH_HIGH;
                                int pesPacketLengthLow = bufferLong[pesStart + 5] & PES_PACKET_LENGTH_LOW;
                                int pesLengthCount = -1;
                                int pesPacketLength = pesPacketLengthHigh * MOVE_LEFT + pesPacketLengthLow;
                                if (pesPacketLength == 0) {
                                    isUnknow = true;
                                }
                                if (!isUnknow) {
                                    fileOutputStream.write(bufferLong, pesStart + FORMER_BYTES + headLength, pesPacketLength < (PACKET_LENGTH_LONG - pesStart - PACKET_FORMER_BYTES) ? pesPacketLength : PACKET_LENGTH_LONG - pesStart - headLength - FORMER_BYTES - ERROR_CODE);
                                    pesLengthCount = pesLengthCount + 1 + PACKET_LENGTH_LONG - pesStart - FORMER_BYTES - ERROR_CODE;
                                } else {
                                    fileOutputStream.write(bufferLong, pesStart + FORMER_BYTES + headLength, PACKET_LENGTH_LONG - pesStart - headLength - FORMER_BYTES - ERROR_CODE);
                                }
                                while (pesLengthCount < pesPacketLength && fileInputStream.read(bufferLong) != -1) {
                                    if (checkPidRightOrNot(bufferLong, high, low)) {
                                        switch (checkAdaptationField(bufferLong)) {
                                            case PAYLOAD_ONLY:
                                                if (!isUnknow) {
                                                    fileOutputStream.write(bufferLong, HEAD_BYTE, pesPacketLength < (pesLengthCount + PACKET_LENGTH_LONG - HEAD_BYTE - ERROR_CODE) ? pesPacketLength - pesLengthCount : PACKET_LENGTH_LONG - HEAD_BYTE - ERROR_CODE);
                                                    pesLengthCount = pesLengthCount + PACKET_LENGTH_LONG - HEAD_BYTE - ERROR_CODE;
                                                } else {
                                                    fileOutputStream.write(bufferLong, HEAD_BYTE, PACKET_LENGTH_LONG - HEAD_BYTE - ERROR_CODE);
                                                }
                                                break;
                                            case BETWEEN:
                                                int adaptationFieldLength = bufferLong[4] & ADAPTATION_FIELD_FLAG;
                                                if (!isUnknow) {
                                                    fileOutputStream.write(bufferLong, HEAD_BYTE + adaptationFieldLength + 1, pesPacketLength < (pesLengthCount + PACKET_LENGTH_LONG - HEAD_BYTE - ERROR_CODE - adaptationFieldLength - 1) ? pesPacketLength - pesLengthCount : PACKET_LENGTH_LONG - HEAD_BYTE - ERROR_CODE - adaptationFieldLength - 1);
                                                    pesLengthCount = pesLengthCount + PACKET_LENGTH_LONG - HEAD_BYTE - ERROR_CODE - adaptationFieldLength - 1;
                                                } else {
                                                    fileOutputStream.write(bufferLong, HEAD_BYTE + adaptationFieldLength + 1, PACKET_LENGTH_LONG - HEAD_BYTE - ERROR_CODE - adaptationFieldLength - 1);
                                                }
                                                break;
                                            case ADAPTATION_FIELD_ONLY:
                                                break;
                                            default:
                                                break;
                                        }
                                    }
                                }
                                break;
                            }
                        }
                    }
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
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

    private static int checkAdaptationField(byte[] buffer) {
        return buffer[3] & BETWEEN;
    }

    private static boolean checkPayload(byte[] buffer) {
        if ((buffer[1] & POINTER_FIELD_FLAG) == 0) {
            return false;
        }
        return true;
    }
}
