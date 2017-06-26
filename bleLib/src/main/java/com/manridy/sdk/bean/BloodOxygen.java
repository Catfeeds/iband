package com.manridy.sdk.bean;


/**
 * 血氧
 * Created by jarLiao on 2016/10/25.
 */

public class BloodOxygen{
    private int id;//主键
    private String boDate;//时间
    private String boDay;//天
    private int boLength;//血氧条数
    private int boNum;//血氧编号
    private String boRate;//血氧

    public BloodOxygen() {
    }

    public BloodOxygen(String boDay, String boDate, String boRate) {
        this.boDay = boDay;
        this.boDate = boDate;
        this.boRate = boRate;
    }

    public BloodOxygen(String boDate, String boDay, int boLength, int boNum, String boRate) {
        this.boDate = boDate;
        this.boDay = boDay;
        this.boLength = boLength;
        this.boNum = boNum;
        this.boRate = boRate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getboDate() {
        return boDate;
    }

    public void setboDate(String boDate) {
        this.boDate = boDate;
    }

    public String getboDay() {
        return boDay;
    }

    public void setboDay(String boDay) {
        this.boDay = boDay;
    }

    public int getboLength() {
        return boLength;
    }

    public void setboLength(int boLength) {
        this.boLength = boLength;
    }

    public int getboNum() {
        return boNum;
    }

    public void setboNum(int boNum) {
        this.boNum = boNum;
    }

    public String getboRate() {
        return boRate;
    }

    public void setboRate(String boRate) {
        this.boRate = boRate;
    }

    @Override
    public String toString() {
        return "boModel{" +
                "boDate=" + boDate +
                ", boDay='" + boDay + '\'' +
                ", boLength=" + boLength +
                ", boNum=" + boNum +
                ", boRate=" + boRate +
                '}';
    }
}
