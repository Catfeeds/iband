package com.manridy.sdk.bean;


public class Heart {
    private int id;//主键
    private String heartDate;//时间
    private String heartDay;//天
    private int heartLength;//心率条数
    private int heartNum;//心率编号
    private int heartRate;//心率

    public Heart() {
    }

    public Heart(String heartDate, String heartDay, int heartLength, int heartNum, int heartRate) {
        this.heartDate = heartDate;
        this.heartDay = heartDay;
        this.heartLength = heartLength;
        this.heartNum = heartNum;
        this.heartRate = heartRate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHeartDate() {
        return heartDate;
    }

    public void setHeartDate(String heartDate) {
        this.heartDate = heartDate;
    }

    public String getHeartDay() {
        return heartDay;
    }

    public void setHeartDay(String heartDay) {
        this.heartDay = heartDay;
    }

    public int getHeartLength() {
        return heartLength;
    }

    public void setHeartLength(int heartLength) {
        this.heartLength = heartLength;
    }

    public int getHeartNum() {
        return heartNum;
    }

    public void setHeartNum(int heartNum) {
        this.heartNum = heartNum;
    }

    public int getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(int heartRate) {
        this.heartRate = heartRate;
    }

    @Override
    public String toString() {
        return "Heart{" +
                "id=" + id +
                ", heartDate='" + heartDate + '\'' +
                ", heartDay='" + heartDay + '\'' +
                ", heartLength=" + heartLength +
                ", heartNum=" + heartNum +
                ", heartRate=" + heartRate +
                '}';
    }
}
