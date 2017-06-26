package com.manridy.iband.bean;

import org.litepal.crud.DataSupport;

/**
 * 睡眠模板
 */
public class SleepModel extends DataSupport {

    private int id;//主键
    private String sleepDay;//天
    private int sleepLength;//总条数
    private int sleepNum;//编号
    private String sleepStartTime;//开始时间
    private String sleepEndTime;//结束时间
    private int sleepDataType;//数据类型
    private int sleepDeep;//深睡
    private int sleepLight;//浅睡



    public SleepModel() {
    }

    public SleepModel(int sleepDeep, int sleepLight) {
        this.sleepDeep = sleepDeep;
        this.sleepLight = sleepLight;
    }

    public SleepModel(String sleepDay, int sleepLength, int sleepNum, String sleepStartTime, String sleepEndTime, int sleepDataType, int sleepDeep, int sleepLight) {
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
        final StringBuffer sb = new StringBuffer("SleepModel{");
        sb.append("id=").append(id);
        sb.append(", sleepDay='").append(sleepDay).append('\'');
        sb.append(", sleepLength=").append(sleepLength);
        sb.append(", sleepNum=").append(sleepNum);
        sb.append(", sleepStartTime='").append(sleepStartTime).append('\'');
        sb.append(", sleepEndTime='").append(sleepEndTime).append('\'');
        sb.append(", sleepDataType=").append(sleepDataType);
        sb.append(", sleepDeep=").append(sleepDeep);
        sb.append(", sleepLight=").append(sleepLight);
        sb.append('}');
        return sb.toString();
    }
}
