package com.manridy.applib.utils;


import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 时间工具类
 *  Created by jarLiao on 2016/8/5.
 */
public class TimeUtil {

    /**
     * 毫秒转化时分秒毫秒
     */
    public static String formatTime(Long ms) {
        Integer ss = 1000;
        Integer mi = ss * 60;
        Integer hh = mi * 60;
        Integer dd = hh * 24;

        Long day = ms / dd;
        Long hour = (ms - day * dd) / hh;
        Long minute = (ms - day * dd - hour * hh) / mi;
        Long second = (ms - day * dd - hour * hh - minute * mi) / ss;
        Long milliSecond = ms - day * dd - hour * hh - minute * mi - second * ss;

        StringBuffer sb = new StringBuffer();
        if(day > 0) {
            sb.append(day+"天");
        }
        if(hour > 0) {
            sb.append(hour+"小时");
        }
        if(minute > 0) {
            sb.append(minute+"分钟");
        }
        if(second > 0) {
            sb.append(second+"秒");
        }
        if(milliSecond > 0) {
            sb.append(milliSecond+"毫秒");
        }
        return sb.toString();
    }

    /**
     * 得到现在时间
     * @param t
     * @return
     */
    public static String longToTime(long t){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date =dateFormat.format(new Date(t));
        return date;
    }

    /**
     * yyyy-MM-dd HH:mm:ss
     *
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static String getNowYMDHMSTime() {


        SimpleDateFormat mDateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        String date = mDateFormat.format(new Date());
        return date;
    }

    /**
     * MM-dd HH:mm:ss
     *
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static String getNowMDHMSTime() {

        SimpleDateFormat mDateFormat = new SimpleDateFormat(
                "MM-dd HH:mm:ss");
        String date = mDateFormat.format(new Date());
        return date;
    }

    /**
     * MM-dd
     *
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static String getNowYMD() {

        SimpleDateFormat mDateFormat = new SimpleDateFormat(
                "yyyy-MM-dd");
        String date = mDateFormat.format(new Date());
        return date;
    }

    /**
     * yyyy-MM-dd
     *
     * @param date
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static String getYMD(Date date) {

        SimpleDateFormat mDateFormat = new SimpleDateFormat(
                "yyyy-MM-dd");
        String dateS = mDateFormat.format(date);
        return dateS;
    }

    public static int[] getNowYMDHMStoInt(){
        int[] time = new int[7];
        Calendar cal = Calendar.getInstance();//使用默认时区和语言环境获得一个日历。
        time[0] =cal.get(Calendar.YEAR);
        time[1] =cal.get(Calendar.MONTH)+1;
        time[2] =cal.get(Calendar.DAY_OF_MONTH);
        time[3] =cal.get(Calendar.HOUR_OF_DAY);
        time[4] =cal.get(Calendar.MINUTE);
        time[5] =cal.get(Calendar.SECOND);
        time[6] =cal.get(Calendar.DAY_OF_WEEK)-1;
        return time;
    }

    public static int[] getYMDtoInt(String s){
        int[] time = new int[3];
        String[] strings = s.split("-");
        time[0] = Integer.parseInt(strings[0]);
        time[1] = Integer.parseInt(strings[1])-1;
        time[2] = Integer.parseInt(strings[2]);
        return time;
    }

    public static int[] getYMDHMtoInt(String s){
        int[] time = new int[3];
        String[] strings = s.split(" ");
        String[] strings1= strings[0].split("-");
        time[0] = Integer.parseInt(strings1[0]);
        time[1] = Integer.parseInt(strings1[1])-1;
        time[2] = Integer.parseInt(strings1[2]);
        return time;
    }

    /**
     * 比较选择日期是否大于当前日期
     * @param date 选择日期
     * @return true 大于，false 不大于
     */
    public static boolean compareNowYMD(Date date){
        Calendar calendar = Calendar.getInstance();
        Date curDate = calendar.getTime();
        boolean is;
        if ( date.getTime() > curDate.getTime()){
            is = true;
        }else{
            is = false;
        }
        return is;
    }

    /**
     * 比较选择周期是否大于当前日期
     * @param date 选择日期
     * @return true 大于，false 不大于
     */
    public static boolean compareNowWeek(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH,-7);
        Date curDate = calendar.getTime();
        boolean is;
        if ( date.getTime() >= curDate.getTime()){
            is = true;
        }else{
            is = false;
        }
        return is;
    }

    public static List<String> getCurrenWeek(Calendar cal){
        List<String> weeks = new ArrayList<>();
        Calendar calendar =cal;
        Calendar calendar1 = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("MM/dd");
        for (int i = 1 ;i < 8 ; i++){
            calendar.add(Calendar.DAY_OF_MONTH, +i);
            if (format.format(calendar.getTime()).equals(format.format(calendar1.getTime()))){
                weeks.add("今天");
            }else{
                weeks.add(format.format(calendar.getTime()));
            }
            calendar.add(Calendar.DAY_OF_MONTH, -i);
        }
        return weeks;
    }

    public static String getDayFormat(Date day) {
        SimpleDateFormat Dayformat = new SimpleDateFormat("MM/dd");
        String d = Dayformat.format(day);
        return d;
    }


    public static String minToHM(int min){
        String s;
        int m;
        int h = min/60;
        if (h != 0){
            m = min%h;
        }else{
            m = min;
        }
        s=h+"h"+m+"min";
        return s;
    }
    public static String minToHM2(int min){
        String s;
        int m;
        int h = min/60;
        if (h != 0){
            m = min%h;
        }else{
            m = min;
        }
        s=zero(h)+":"+zero(m);
        return s;
    }

    public static String zero(int i){
        String s;
        if (i <10){
            s = "0" + i;
        }else {
            s = "" + i;
        }
        return s;
    }

    /**
     * 得到某天一周的日期
     * @param mdate 某天
     * @return 一周的时间
     */
    public static List<Date> dateToWeek(Date mdate) {
        int b = mdate.getDay();
        if (b == 0) {
            b = 7;
        }
        List<Date> list = new ArrayList<Date>();
        Long fTime = mdate.getTime() - b * 24 * 3600000;
        for (int a = 1; a <= 7; a++) {
            Date fdate = new Date();
            fdate.setTime(fTime + (a * 24 * 3600000));
            list.add(a-1, fdate);
        }
        return list;
    }

    /**
     * 得到当月
     * @return
     */
    public static int getNowMonth(){
        return  Calendar.getInstance().get(Calendar.MONTH)+1;
    }

    public static List<String> getMonthToDay(int month){
        List<String> list = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        calendar.set(year,month-1,1);
        int max = calendar.getActualMaximum(Calendar.DATE);
        for (int i = 0; i < max; i++) {
            list.add(getYMD(calendar.getTime()));
            calendar.add(Calendar.DAY_OF_MONTH,1);
        }
        return list;
    }

    public static List<String> getMonthToDay(Calendar calendar){
        List<String> list = new ArrayList<>();
        calendar.set(Calendar.DAY_OF_MONTH,1);
        int max = calendar.getActualMaximum(Calendar.DATE);
        for (int i = 0; i < max; i++) {
            list.add(getYMD(calendar.getTime()));
            calendar.add(Calendar.DAY_OF_MONTH,1);
        }
        calendar.add(Calendar.MONTH,-1);
        return list;
    }

    public static String[] getMonthList(){
        int index = 12;
        String[] Months = new String[index];
        for (int i = 0; i < index; i++) {
            Months[i] = String.valueOf(i+1);
        }
        return Months;
    }

    public static String getHour(int time) {
        String str ;
        if (time<60) {
            str = time+"分钟";
        }else {
            str =  String .format("%.1f", ((double)time/60))+"小时";
        }
        return str;
    }

    public static double getHourDouble(int time){
        String str =  String .format("%.1f", ((double)time/60));
        return Double.valueOf(str);
    }
}
