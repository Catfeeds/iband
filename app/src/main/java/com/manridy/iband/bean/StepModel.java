package com.manridy.iband.bean;

import org.litepal.crud.DataSupport;

import java.util.Date;

/**
 * 计步模板
 * 应用于计步数据显示
 * Created by jarLiao on 2016/10/25.
 */
 public class StepModel extends DataSupport {
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

    public StepModel() {
    }

    public StepModel(String stepDay, int hisLength, int hisCount, int stepNum) {
        this.stepDay = stepDay;
        this.hisLength = hisLength;
        this.hisCount = hisCount;
        this.stepNum = stepNum;
    }

    public StepModel(Date stepDate, String stepDay, int stepNum, int stepMileage, int stepCalorie) {
        this.stepDate = stepDate;
        this.stepDay = stepDay;
        this.stepNum = stepNum;
        this.stepMileage = stepMileage;
        this.stepCalorie = stepCalorie;
    }

    public StepModel(Date stepDate, int stepTime, int stepType, int stepNum, int stepMileage, int stepCalorie) {
        this.stepDate = stepDate;
        this.stepTime = stepTime;
        this.stepType = stepType;
        this.stepNum = stepNum;
        this.stepMileage = stepMileage;
        this.stepCalorie = stepCalorie;
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
        final StringBuffer sb = new StringBuffer("StepModel{");
        sb.append("id=").append(id);
        sb.append(", stepDate=").append(stepDate);
        sb.append(", stepDay='").append(stepDay).append('\'');
        sb.append(", hisLength=").append(hisLength);
        sb.append(", hisCount=").append(hisCount);
        sb.append(", stepNum=").append(stepNum);
        sb.append(", stepMileage=").append(stepMileage);
        sb.append(", stepCalorie=").append(stepCalorie);
        sb.append(", stepTime=").append(stepTime);
        sb.append(", stepType=").append(stepType);
        sb.append('}');
        return sb.toString();
    }
}
