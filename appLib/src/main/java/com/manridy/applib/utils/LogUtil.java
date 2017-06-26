package com.manridy.applib.utils;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 日志工具类
 * 控制日志显示
 * Created by jarLiao on 2016/9/18.
 */
public class LogUtil {

    private static boolean LOGV = true;
    private static boolean LOGD = true;
    private static boolean LOGI = true;
    private static boolean LOGW = true;
    private static boolean LOGE = true;

    public static void v(String tag, String mess) {
        if (LOGV) { Log.v(tag, mess); }
    }
    public static void d(String tag, String mess) {
        if (LOGD) { Log.d(tag, mess); }
    }
    public static void i(String tag, String mess) {
        if (LOGI) { Log.i(tag, mess); }
    }
    public static void w(String tag, String mess) {
        if (LOGW) { Log.w(tag, mess); }
    }
    public static void e(String tag, String mess) {
        if (LOGE) { Log.e(tag, mess); }
//        print2File(tag, mess);
    }
    public static final String LOG_FILE_PATH = "/iwaerLog.txt";
    private synchronized static void print2File(final String tag, final String msg) {
        Date now = new Date();
        String date = new SimpleDateFormat("MM-dd", Locale.getDefault()).format(now);

//        final String fullPath = FileUtil.getSdCardPath() +"manridy-"+ date + ".txt";
//        if (!createOrExistsFile(fullPath)) {
//            Log.e(tag, "log to " + fullPath + " failed!");
//            return;
//        }
        final String lineFeed = "\f\n";
        String time = new SimpleDateFormat("MM-dd HH:mm:ss.SSS ", Locale.getDefault()).format(now);
        StringBuilder sb = new StringBuilder();
        sb.append(lineFeed);
        sb.append(time)
                .append(tag)
                .append(": ")
                .append(msg);
        final String dateLogContent = sb.toString();
        new Thread(new Runnable() {
            @Override
            public void run() {
                BufferedWriter bw = null;
                try {
                    bw = new BufferedWriter(new FileWriter(FileUtil.getSdCardPath() + LOG_FILE_PATH, true));
                    bw.write(dateLogContent);
//                    Log.d(tag, "log to " + fullPath + " success!");
                } catch (IOException e) {
                    e.printStackTrace();
//                    Log.e(tag, "log to " + fullPath + " failed!");
                } finally {
                    try {
                        if (bw != null) {
                            bw.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private static boolean createOrExistsFile(String filePath) {
        return createOrExistsFile(isSpace(filePath) ? null : new File(filePath));
    }

    private static boolean createOrExistsFile(File file) {
        if (file == null) return false;
        if (file.exists()) return file.isFile();
        if (!createOrExistsDir(file.getParentFile())) return false;
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean createOrExistsDir(File file) {
        return file != null && (file.exists() ? file.isDirectory() : file.mkdirs());
    }

    private static boolean isSpace(String s) {
        if (s == null) return true;
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
