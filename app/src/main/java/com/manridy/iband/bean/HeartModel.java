package com.manridy.iband.bean;

import org.litepal.crud.DataSupport;

/**
 * 心率模板
 * 应用于心率数据显示
 * Created by jarLiao on 2016/10/25.
 */

public class HeartModel extends DataSupport {
    private int id;//主键
    private String heartDate;//时间
    private String heartDay;//天
    private int heartLength;//心率条数
    private int heartNum;//心率编号
    private int heartRate;//心率

    public HeartModel() {
    }

    public HeartModel(String heartDate, String heartDay, int heartLength, int heartNum, int heartRate) {
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
        final StringBuffer sb = new StringBuffer("HeartModel{");
        sb.append("id=").append(id);
        sb.append(", heartDate='").append(heartDate).append('\'');
        sb.append(", heartDay='").append(heartDay).append('\'');
        sb.append(", heartLength=").append(heartLength);
        sb.append(", heartNum=").append(heartNum);
        sb.append(", heartRate=").append(heartRate);
        sb.append('}');
        return sb.toString();
    }
}
