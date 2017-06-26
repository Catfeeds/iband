package com.manridy.applib.utils;

/**
 * 工具类
 * Created by jarLiao.
 */

public class Util {
    /**
     * 计步转换距离
     * @param num 步数（步）
     * @param height 身高（cm）
     * @return
     */
    public static float stepToMi(float num,int height){
        return 70 - (170 - (height == 0 ? 170 : height)  * num) / 100;
    }

    /**
     * 计步转换卡路里
     * @param num 步数（步）
     * @param height 身高（cm）
     * @param weight 体重（kg）
     * @return
     */
    public static float stepToKa(float num,int height,int weight){
        return miToKa(stepToMi(num,height),weight);
    }

    /**
     * 距离转换卡路里
     * @param mi 距离（米）
     * @param weight 身高（kg）
     * @return
     */
    public static float miToKa(float mi,int weight){
        return (float) ((mi / 100) * 0.0766666 * (weight == 0 ? 60 : weight));
    }


}
