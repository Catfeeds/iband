package com.manridy.iband.bean;

import org.litepal.crud.DataSupport;

/**
 *
 */

public class SedentaryModel extends DataSupport {
    private int id;//主键
    private boolean sedentaryOnOff;
    private boolean sedentaryNap;
    private String startTime;
    private String endTime;

    public SedentaryModel() {
    }

    public SedentaryModel(boolean sedentaryOnOff, boolean sedentaryNap, String startTime, String endTime) {
        this.sedentaryOnOff = sedentaryOnOff;
        this.sedentaryNap = sedentaryNap;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isSedentaryOnOff() {
        return sedentaryOnOff;
    }

    public void setSedentaryOnOff(boolean sedentaryOnOff) {
        this.sedentaryOnOff = sedentaryOnOff;
    }

    public boolean isSedentaryNap() {
        return sedentaryNap;
    }

    public void setSedentaryNap(boolean sedentaryNap) {
        this.sedentaryNap = sedentaryNap;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("SedentaryModel{");
        sb.append("id=").append(id);
        sb.append(", sedentaryOnOff=").append(sedentaryOnOff);
        sb.append(", sedentaryNap=").append(sedentaryNap);
        sb.append(", startTime='").append(startTime).append('\'');
        sb.append(", endTime='").append(endTime).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
