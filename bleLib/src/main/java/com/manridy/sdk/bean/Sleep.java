package com.manridy.sdk.bean;




public class Sleep {
    private int id;//主键
    private String sleepDay;//天
    private int sleepLength;//总条数
    private int sleepNum;//编号
    private String sleepStartTime;//开始时间
    private String sleepEndTime;//结束时间
    private int sleepDataType;//数据类型
    private int sleepDeep;//深睡
    private int sleepLight;//浅睡

    public Sleep() {
    }

    public Sleep(String sleepDay, int sleepLength, int sleepNum, String sleepStartTime, String sleepEndTime, int sleepDataType, int sleepDeep, int sleepLight) {
        this.sleepDay = sleepDay;
        this.sleepLength = sleepLength;
        this.sleepNum = sleepNum;
        this.sleepStartTime = sleepStartTime;
        this.sleepEndTime = sleepEndTime;
        this.sleepDataType = sleepDataType;
        this.sleepDeep = sleepDeep;
        this.sleepLight = sleepLight;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSleepDay() {
        return sleepDay;
    }

    public void setSleepDay(String sleepDay) {
        this.sleepDay = sleepDay;
    }

    public int getSleepLength() {
        return sleepLength;
    }

    public void setSleepLength(int sleepLength) {
        this.sleepLength = sleepLength;
    }

    public int getSleepNum() {
        return sleepNum;
    }

    public void setSleepNum(int sleepNum) {
        this.sleepNum = sleepNum;
    }

    public String getSleepStartTime() {
        return sleepStartTime;
    }

    public void setSleepStartTime(String sleepStartTime) {
        this.sleepStartTime = sleepStartTime;
    }

    public String getSleepEndTime() {
        return sleepEndTime;
    }

    public void setSleepEndTime(String sleepEndTime) {
        this.sleepEndTime = sleepEndTime;
    }

    public int getSleepDataType() {
        return sleepDataType;
    }

    public void setSleepDataType(int sleepDataType) {
        this.sleepDataType = sleepDataType;
    }

    public int getSleepDeep() {
        return sleepDeep;
    }

    public void setSleepDeep(int sleepDeep) {
        this.sleepDeep = sleepDeep;
    }

    public int getSleepLight() {
        return sleepLight;
    }

    public void setSleepLight(int sleepLight) {
        this.sleepLight = sleepLight;
    }

    @Override
    public String toString() {
        return "Sleep{" +
                "id=" + id +
                ", sleepDay='" + sleepDay + '\'' +
                ", sleepLength=" + sleepLength +
                ", sleepNum=" + sleepNum +
                ", sleepStartTime='" + sleepStartTime + '\'' +
                ", sleepEndTime='" + sleepEndTime + '\'' +
                ", sleepDataType=" + sleepDataType +
                ", sleepDeep=" + sleepDeep +
                ", sleepLight=" + sleepLight +
                '}';
    }
}
