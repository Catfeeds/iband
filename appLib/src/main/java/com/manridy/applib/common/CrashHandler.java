package com.manridy.applib.common;

import android.content.Context;
import android.os.Build;
import android.os.Looper;

import com.manridy.applib.R;
import com.manridy.applib.utils.FileUtil;
import com.manridy.applib.utils.TimeUtil;
import com.manridy.applib.utils.ToastUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static android.R.attr.versionCode;
import static android.R.attr.versionName;

/**
 * 异常捕获处理类
 * Created by Administrator on 2016/10/17.
 */

public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private static final String TAG = "CrashHandler";
    /** 错误日志文件名称 */
    static final String LOG_NAME = "/manridy_log.txt";
    private Thread.UncaughtExceptionHandler mDefaultHandler;// 系统默认的UncaughtException处理类
    private static CrashHandler INSTANCE = new CrashHandler();// CrashHandler实例
    private Context mContext;// 程序的Context对象

    /**
     * 保证只有一个CrashHandler实例
     */
    private CrashHandler() {
    }

    /**
     * 获取CrashHandler实例 ,单例模式
     */
    public static CrashHandler getInstance() {
        return INSTANCE;
    }

    /**
     * 初始化
     *
     * @param context
     */
    public void init(Context context) {
        mContext = context;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();// 获取系统默认的UncaughtException处理器
        Thread.setDefaultUncaughtExceptionHandler(this);// 设置该CrashHandler为程序的默认处理器
    }

    /**
     * 当UncaughtException发生时会转入该重写的方法来处理
     */
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            // 如果自定义的没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            // 退出程序
            try {
                Thread.sleep(3000);// 如果处理了，让程序继续运行3秒再退出，保证文件保存并上传到服务器
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex 异常信息
     * @return true 如果处理了该异常信息;否则返回false.
     */
    public boolean handleException(Throwable ex) {
        if (ex == null)
            return true;
        new Thread() {
            public void run() {
                Looper.prepare();
                ToastUtil.showToast(mContext,R.string.error);
                Looper.loop();
            }
        }.start();
        saveCrashInfoToFile(ex);
        return true;
    }

    /**
     * 保存错误信息到文件中
     * @param ex 异常
     */
    private void saveCrashInfoToFile(Throwable ex){
        final StackTraceElement[] stack = ex.getStackTrace();
        final String message = ex.getMessage();
        /* 准备错误日志文件 */
        File logFile = new File(FileUtil.getSdCardPath() + LOG_NAME);
        if (!logFile.getParentFile().exists()) {
            logFile.getParentFile().mkdirs();
        }
        /* 写入错误日志 */
        FileWriter fw = null;
        final String lineFeed = "\f\n";
        try {
            fw = new FileWriter(logFile,true);
            fw.write(getCrashHead());
            fw.write(TimeUtil.getNowYMDHMSTime()+lineFeed+lineFeed);
            fw.write(message + lineFeed);
            for (int i = 0; i < stack.length; i++) {
                fw.write(stack[i].toString() + lineFeed);
            }
            fw.write(lineFeed);
            fw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (null != fw) {
                    fw.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取崩溃头
     *
     * @return 崩溃头
     */
    private String getCrashHead() {
        return "\n************* Crash Log Head ****************" +
                "\nDevice Manufacturer: " + Build.MANUFACTURER +// 设备厂商
                "\nDevice Model       : " + Build.MODEL +// 设备型号
                "\nAndroid Version    : " + Build.VERSION.RELEASE +// 系统版本
                "\nAndroid SDK        : " + Build.VERSION.SDK_INT +// SDK版本
                "\nApp VersionName    : " + versionName +
                "\nApp VersionCode    : " + versionCode +
                "\n************* Crash Log Head ****************\n\n";
    }
}
