package com.manridy.iband.bean;

import org.litepal.crud.DataSupport;

/**
 * 应用模板
 * 应用于应用提醒
 * Created by jarLiao on 2016/10/25.
 */

public class AppModel extends DataSupport {
    private int id;//主键
    private int appId;
    private String appName;
    private boolean isOnOff;

    public AppModel() {
    }

    public AppModel(int appId, String appName, boolean isOnOff) {
        this.appId = appId;
        this.appName = appName;
        this.isOnOff = isOnOff;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAppId() {
        return appId;
    }

    public void setAppId(int appId) {
        this.appId = appId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public boolean isOnOff() {
        return isOnOff;
    }

    public void setOnOff(boolean onOff) {
        isOnOff = onOff;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("AppModel{");
        sb.append("id=").append(id);
        sb.append(", appName='").append(appName).append('\'');
        sb.append(", isOnOff=").append(isOnOff);
        sb.append('}');
        return sb.toString();
    }
}
