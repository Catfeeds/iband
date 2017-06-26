package com.manridy.sdk.bean;

/**
 * 闹钟
 * Created by jarLiao on 17/2/14.
 */

public class Clock {
    private int id;
    private int clockHour;
    private int clockMin;
    private boolean clockOnOFF;

    public Clock(int clockHour, int clockMin) {
        this.clockHour = clockHour;
        this.clockMin = clockMin;
    }

    public Clock(String time,boolean onOff){
        String[] times = time.split(":");
        this.clockHour = Integer.valueOf(times[0]);
        this.clockMin = Integer.valueOf(times[1]);
        this.clockOnOFF = onOff;
    }

    public Clock(int hour, int min, boolean onOff) {
        this.clockHour = hour;
        this.clockMin = min;
        this.clockOnOFF = onOff;
    }

    public String getTime(){
       return addZeroToInt(clockHour)+":"+addZeroToInt(clockMin);
    }

    public boolean getOnOff(){
        return clockOnOFF;
    }

    private String addZeroToInt(int i){
       return i <= 9 ? "0" + i : "" + i;
    }
}
