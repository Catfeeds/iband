package com.manridy.sdk.bean;

/**
 * Created by jarLiao on 17/2/18.
 */
public class Sedentary {
    private int id;
    private boolean sedentaryOnOff;
    private boolean sedentaryNap;
    private String startTime;
    private String endTime;

    public Sedentary() {
    }

    public Sedentary(boolean sedentaryOnOff, boolean sedentaryNap, String startTime, String endTime) {
        this.sedentaryOnOff = sedentaryOnOff;
        this.sedentaryNap = sedentaryNap;
        this.startTime = startTime;
        this.endTime = endTime;
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
        return "Sedentary{" +
                "id=" + id +
                ", sedentaryOnOff=" + sedentaryOnOff +
                ", sedentaryNap=" + sedentaryNap +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                '}';
    }
}
