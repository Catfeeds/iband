package com.manridy.iband.bean;

import org.litepal.crud.DataSupport;


/**
 *
 */
public class ClockModel extends DataSupport{
    private int id;
    private int clockHour;
    private int clockMin;
    private boolean clockOnOFF;

    public ClockModel() {
    }

    public ClockModel(int clockHour, int clockMin) {
        this.clockHour = clockHour;
        this.clockMin = clockMin;
    }

    public ClockModel(int hour, int min, boolean onOff) {
        this.clockHour = hour;
        this.clockMin = min;
        this.clockOnOFF = onOff;
    }

    public ClockModel(String time,boolean onOff){
        String[] times = time.split(":");
        this.clockHour = Integer.valueOf(times[0]);
        this.clockMin = Integer.valueOf(times[1]);
        this.clockOnOFF = onOff;
    }

    public String getTime(){
        return addZeroToInt(clockHour)+":"+addZeroToInt(clockMin);
    }

    public void setTime(String time){
        String[] times = time.split(":");
        this.clockHour = Integer.valueOf(times[0]);
        this.clockMin = Integer.valueOf(times[1]);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClockHour() {
        return clockHour;
    }

    public void setClockHour(int clockHour) {
        this.clockHour = clockHour;
    }

    public int getClockMin() {
        return clockMin;
    }

    public void setClockMin(int clockMin) {
        this.clockMin = clockMin;
    }

    public boolean isClockOnOFF() {
        return clockOnOFF;
    }

    public void setClockOnOFF(boolean clockOnOFF) {
        this.clockOnOFF = clockOnOFF;
    }

    public boolean getOnOff(){
        return clockOnOFF;
    }

    private String addZeroToInt(int i){
        return i <= 9 ? "0" + i : "" + i;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ClockModel{");
        sb.append("id=").append(id);
        sb.append(", clockHour=").append(clockHour);
        sb.append(", clockMin=").append(clockMin);
        sb.append(", clockOnOFF=").append(clockOnOFF);
        sb.append('}');
        return sb.toString();
    }
}
