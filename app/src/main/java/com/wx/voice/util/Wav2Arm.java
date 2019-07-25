package com.wx.voice.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import io.kvh.media.amr.AmrEncoder;


/**
 * Created by Administrator on 2018/1/23.
 */

public class Wav2Arm {
    final private static byte[] header = new byte[]{0x23, 0x21, 0x41, 0x4D, 0x52, 0x0A};

    private static class SingletonHolder {
        private static Wav2Arm instance = new Wav2Arm();
    }


    private Wav2Arm() {

    }

    public static Wav2Arm getInstance() {
        return SingletonHolder.instance;
    }

    public interface OnStateListener {
        void onStart();
        void onError(String errMsg);
        void onOk(String outPath);
    }

    public void wav2amrNoCallBack(final String inpath, final String outpath) throws IOException {
        startAmr(inpath, outpath);
    }

    public void wav2amr(final String inpath, final String outpath, OnStateListener listener) {
        convertAMR(inpath, outpath,listener);
    }

    /**
     * 将wav或raw文件转换成amr
     *
     * @param inpath  源文件
     * @param outpath 目标文件
     */
    private void convertAMR(String inpath, String outpath, OnStateListener listener) {
        try {
            listener.onStart();
            startAmr(inpath, outpath);
            listener.onOk(outpath);
        } catch (Exception e) {
            listener.onError(e.getMessage());
        }
    }

    private void startAmr(String inpath, String outpath) throws IOException {
        AmrEncoder.init(0);
        File inFile = new File(inpath);
        List<short[]> armsList = new ArrayList<short[]>();
        FileInputStream inputStream = new FileInputStream(inFile);
        FileOutputStream outStream = new FileOutputStream(outpath);
        //写入Amr头文件
        outStream.write(header);

        int byteSize = 320;
        byte[] buff = new byte[byteSize];
        int rc = 0;
        while ((rc = inputStream.read(buff, 0, byteSize)) > 0) {
            short[] shortTemp = new short[160];
            //将byte[]转换成short[]
            ByteBuffer.wrap(buff).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shortTemp);
            //将short[]转换成byte[]
//              ByteBuffer.wrap(buff).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().put(shortTemp);

            byte[] encodedData = new byte[shortTemp.length * 2];
            int len = AmrEncoder.encode(AmrEncoder.Mode.MR122.ordinal(),shortTemp, encodedData);
            if (len > 0) {
                byte[] tempBuf = new byte[len];
                System.arraycopy(encodedData, 0, tempBuf, 0, len);
                outStream.write(tempBuf, 0, len);
            }
        }
        AmrEncoder.exit();
        outStream.close();
        inputStream.close();
    }

}
