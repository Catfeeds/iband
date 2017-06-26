package com.manridy.sdk.bean;


/**
 * 血压
 * Created by jarLiao on 2016/10/25.
 */

public class BloodPressure {
    private int id;//主键
    private String bpDate;//时间
    private String bpDay;//天
    private int bpLength;//血压条数
    private int bpNum;//血压编号
    private int bpHp;//高压
    private int bpLp;//低压
    private int bpHr;//心率

    public BloodPressure() {
    }


    public BloodPressure(String bpDate, String bpDay, int bpHp, int bpLp, int bpHr) {
        this.bpDate = bpDate;
        this.bpDay = bpDay;
        this.bpHp = bpHp;
        this.bpLp = bpLp;
        this.bpHr = bpHr;
    }

    public BloodPressure(String bpDate, String bpDay, int bpLength, int bpNum, int bpHp, int bpLp, int bpHr) {
        this.bpDate = bpDate;
        this.bpDay = bpDay;
        this.bpLength = bpLength;
        this.bpNum = bpNum;
        this.bpHp = bpHp;
        this.bpLp = bpLp;
        this.bpHr = bpHr;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBpDate() {
        return bpDate;
    }

    public void setBpDate(String bpDate) {
        this.bpDate = bpDate;
    }

    public String getBpDay() {
        return bpDay;
    }

    public void setBpDay(String bpDay) {
        this.bpDay = bpDay;
    }

    public int getBpLength() {
        return bpLength;
    }

    public void setBpLength(int bpLength) {
        this.bpLength = bpLength;
    }

    public int getBpNum() {
        return bpNum;
    }

    public void setBpNum(int bpNum) {
        this.bpNum = bpNum;
    }

    public int getBpHp() {
        return bpHp;
    }

    public void setBpHp(int bpHp) {
        this.bpHp = bpHp;
    }

    public int getBpLp() {
        return bpLp;
    }

    public void setBpLp(int bpLp) {
        this.bpLp = bpLp;
    }

    public int getBpHr() {
        return bpHr;
    }

    public void setBpHr(int bpHr) {
        this.bpHr = bpHr;
    }

    @Override
    public String toString() {
        return "BloodPressure{" +
                ", bpDate='" + bpDate + '\'' +
                ", bpDay='" + bpDay + '\'' +
                ", bpLength=" + bpLength +
                ", bpNum=" + bpNum +
                ", bpHp=" + bpHp +
                ", bpLp=" + bpLp +
                ", bpHr=" + bpHr +
                '}';
    }
}
