package com.manridy.sdk.bean;


import java.util.Date;

public class Sport {
    private int id;//主键
    private Date stepDate;//时间
    private String stepDay;//天
    private int hisLength;//历史总条数
    private int hisCount;//历史包编号
    private int stepNum;//步数
    private int stepMileage;//里程
    private int stepCalorie;//卡路里
    private int stepTime;//运动时长
    private int stepType;//运动类型

    public Sport() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getStepDate() {
        return stepDate;
    }

    public void setStepDate(Date stepDate) {
        this.stepDate = stepDate;
    }

    public String getStepDay() {
        return stepDay;
    }

    public void setStepDay(String stepDay) {
        this.stepDay = stepDay;
    }

    public int getHisLength() {
        return hisLength;
    }

    public void setHisLength(int hisLength) {
        this.hisLength = hisLength;
    }

    public int getHisCount() {
        return hisCount;
    }

    public void setHisCount(int hisCount) {
        this.hisCount = hisCount;
    }

    public int getStepNum() {
        return stepNum;
    }

    public void setStepNum(int stepNum) {
        this.stepNum = stepNum;
    }

    public int getStepMileage() {
        return stepMileage;
    }

    public void setStepMileage(int stepMileage) {
        this.stepMileage = stepMileage;
    }

    public int getStepCalorie() {
        return stepCalorie;
    }

    public void setStepCalorie(int stepCalorie) {
        this.stepCalorie = stepCalorie;
    }

    public int getStepTime() {
        return stepTime;
    }

    public void setStepTime(int stepTime) {
        this.stepTime = stepTime;
    }

    public int getStepType() {
        return stepType;
    }

    public void setStepType(int stepType) {
        this.stepType = stepType;
    }

    @Override
    public String toString() {
        return "Sport{" +
                "id=" + id +
                ", stepDate=" + stepDate +
                ", stepDay='" + stepDay + '\'' +
                ", hisLength=" + hisLength +
                ", hisCount=" + hisCount +
                ", stepNum=" + stepNum +
                ", stepMileage=" + stepMileage +
                ", stepCalorie=" + stepCalorie +
                ", stepTime=" + stepTime +
                ", stepType=" + stepType +
                '}';
    }
}
