package com.manridy.iband.bean;

/**
 * 每日数据模板
 * 应用于历史数据查询填充返回
 * Created by jarLiao on 2016/10/25.
 */
public class DayBean {
    String day;//日期
    int daySum;//一天总和
    int dayMax;//一天最大
    int dayMin;//一天最小
    int dayCount;//一天数据个数
    int dayAverage;//一天平均值

    public DayBean() {
    }

    public DayBean(String day) {
        this.day = day;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public int getDaySum() {
        return daySum;
    }

    public void setDaySum(int daySum) {
        this.daySum = daySum;
    }

    public int getDayMax() {
        return dayMax;
    }

    public void setDayMax(int dayMax) {
        this.dayMax = dayMax;
    }

    public int getDayMin() {
        return dayMin;
    }

    public void setDayMin(int dayMin) {
        this.dayMin = dayMin;
    }

    public int getDayCount() {
        return dayCount;
    }

    public void setDayCount(int dayCount) {
        this.dayCount = dayCount;
    }

    public int getDayAverage() {
        if (dayCount==0) {
            return 0;
        }else {
            dayAverage = daySum/dayCount;
        }
        return dayAverage;
    }

    public void setDayAverage(int dayAverage) {
        this.dayAverage = dayAverage;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("DayBean{");
        sb.append("day='").append(day).append('\'');
        sb.append(", daySum=").append(daySum);
        sb.append(", dayMax=").append(dayMax);
        sb.append(", dayMin=").append(dayMin);
        sb.append(", dayCount=").append(dayCount);
        sb.append(", dayAverage=").append(dayAverage);
        sb.append('}');
        return sb.toString();
    }
}
