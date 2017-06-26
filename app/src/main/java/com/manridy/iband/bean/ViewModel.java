package com.manridy.iband.bean;

import org.litepal.crud.DataSupport;

public class ViewModel extends DataSupport {
    private int id;
    private int viewId;
    private String viewName;
    private int viewIcon;
    private boolean isSelect;
    private boolean isEnable = true;

    public ViewModel() {
    }

    public ViewModel(int viewId, String viewName, int viewIcon, boolean isSelect) {
        this.viewId = viewId;
        this.viewName = viewName;
        this.viewIcon = viewIcon;
        this.isSelect = isSelect;
    }

    public ViewModel(int viewId, String viewName, int viewIcon, boolean isSelect, boolean isEnable) {
        this.viewId = viewId;
        this.viewName = viewName;
        this.viewIcon = viewIcon;
        this.isSelect = isSelect;
        this.isEnable = isEnable;
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
        final StringBuffer sb = new StringBuffer("ViewModel{");
        sb.append("id=").append(id);
        sb.append(", viewId=").append(viewId);
        sb.append(", viewName='").append(viewName).append('\'');
        sb.append(", viewIcon=").append(viewIcon);
        sb.append(", isSelect=").append(isSelect);
        sb.append(", isEnable=").append(isEnable);
        sb.append('}');
        return sb.toString();
    }
}
