package com.manridy.iband.common;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Environment;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.ViewConfiguration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;

/**
 * App工具类
 * Created by jarLiao on 17/5/17.
 */

public class Utils {

    public static String[] getIconData(){
        String[] datas = new String[]{"本地相册","相机拍照","取消"};
        return datas;
    }

    public static String[] getSexData(){
        String[] datas = new String[]{"男","女"};
        return datas;
    }

    public static String[] getAgeData(){
        String[] datas = new String[50];
        for (int i = 0; i < datas.length; i++) {
            datas[i] = (10+i)+"";
        }
        return datas;
    }

    public static String[] getHeightData(int type){
        String[] datas = new String[70];
        if (type == 0) {
            for (int i = 0; i < datas.length; i++) {
                datas[i] = (130+i)+"";
            }
        }else {
            datas = new String[40];
            for (int i = 0; i < datas.length; i++) {
                datas[i] = (50+i)+"";
            }
        }
        return datas;
    }

    public static String[] getWeightData(int type){
        String[] datas = new String[100];
        if (type == 0) {
            for (int i = 0; i < datas.length; i++) {
                datas[i] = (30+i)+"";
            }
        }else {
            datas = new String[200];
            for (int i = 0; i < datas.length; i++) {
                datas[i] = (60+i)+"";
            }
        }

        return datas;
    }

    public static void setPicToView(Bitmap mBitmap,String name) {
        String sdStatus = Environment.getExternalStorageState();
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
            return;
        }
        FileOutputStream b = null;
        File file = new File(Environment.getExternalStorageDirectory()+"/iwaer");
        file.mkdirs();// 创建文件夹
        File[] files = file.listFiles();
        if (files.length >= 3) {
            for (int i = 0; i < files.length; i++) {
                files[i].delete();
            }
        }
        String fileName = file.getPath() + name;// 图片名字
        try {
            b = new FileOutputStream(fileName);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {// 关闭流
                b.flush();
                b.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean checkDeviceHasNavigationBar(Context context) {
        boolean hasNavigationBar = false;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {

        }
        return hasNavigationBar;

    }

    public static int getNavigationBarHeight(Context context) {
        boolean hasMenuKey = ViewConfiguration.get(context).hasPermanentMenuKey();
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
        if ( !hasBackKey) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        //获取NavigationBar的高度
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
        }
        else{
            return 0;
        }
    }
}
