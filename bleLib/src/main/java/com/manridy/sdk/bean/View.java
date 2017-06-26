package com.manridy.sdk.bean;

/**
 * Created by jarLiao on 17/5/22.
 */

public class View {
    private int id;
    private int viewId;
    private String viewName;
    private int viewIcon;
    private boolean isSelect;
    private boolean isEnable;

    public View() {
    }

    public View(int viewId, boolean isSelect) {
        this.viewId = viewId;
        this.isSelect = isSelect;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getViewId() {
        return viewId;
    }

    public void setViewId(int viewId) {
        this.viewId = viewId;
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public int getViewIcon() {
        return viewIcon;
    }

    public void setViewIcon(int viewIcon) {
        this.viewIcon = viewIcon;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public boolean isEnable() {
        return isEnable;
    }

    public void setEnable(boolean enable) {
        isEnable = enable;
    }

    @Override
    public String toString() {
        return "View{" +
                "id=" + id +
                ", viewId=" + viewId +
                ", viewName='" + viewName + '\'' +
                ", viewIcon=" + viewIcon +
                ", isSelect=" + isSelect +
                ", isEnable=" + isEnable +
                '}';
    }
}
