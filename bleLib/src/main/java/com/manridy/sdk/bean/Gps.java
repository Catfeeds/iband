package com.manridy.sdk.bean;

/**
 * Created by jarLiao on 17/2/16.
 */

public class Gps {
    private String mapName;//名称
    private String gpsDate;//时间
    private String day ;//时间
    private float mapLat;//纬度
    private float mapLong;//经度
    private int mapLatType;//数据包总数
    private int mapLongType;//数据包总数
    private int mapLength;//数据包总数
    private int mapNum;//数据包编号
    private int mapState;//数据包状态  0 起点，1 过程点，2 终点

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public String getGpsDate() {
        return gpsDate;
    }

    public void setGpsDate(String gpsDate) {
        this.gpsDate = gpsDate;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public float getMapLat() {
        return mapLat;
    }

    public void setMapLat(float mapLat) {
        this.mapLat = mapLat;
    }

    public float getMapLong() {
        return mapLong;
    }

    public void setMapLong(float mapLong) {
        this.mapLong = mapLong;
    }

    public int getMapLength() {
        return mapLength;
    }

    public void setMapLength(int mapLength) {
        this.mapLength = mapLength;
    }

    public int getMapNum() {
        return mapNum;
    }

    public void setMapNum(int mapNum) {
        this.mapNum = mapNum;
    }

    public int getMapState() {
        return mapState;
    }

    public void setMapState(int mapState) {
        this.mapState = mapState;
    }

    public int getMapLatType() {
        return mapLatType;
    }

    public void setMapLatType(int mapLatType) {
        this.mapLatType = mapLatType;
    }

    public int getMapLongType() {
        return mapLongType;
    }

    public void setMapLongType(int mapLongType) {
        this.mapLongType = mapLongType;
    }

    @Override
    public String toString() {
        return "Gps{" +
                "mapName='" + mapName + '\'' +
                ", gpsDate='" + gpsDate + '\'' +
                ", day='" + day + '\'' +
                ", mapLat=" + mapLat +
                ", mapLong=" + mapLong +
                ", mapLength=" + mapLength +
                ", mapNum=" + mapNum +
                ", mapState=" + mapState +
                '}';
    }
}
