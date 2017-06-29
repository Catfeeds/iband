package com.manridy.iband.bean;

import org.litepal.crud.DataSupport;

/**
 * 用户模板
 * 应用于用户数据显示
 */
public class UserModel extends DataSupport {
    private int id;//数据索引
    private String userName;//用户名
    private int userSex;//性别
    private String userAge;//年龄
    private String userHeight;//身高
    private String userWeight;//体重
    private String userStep;//步长

    public UserModel() {
    }

    public UserModel(String userHeight, String userWeight) {
        this.userHeight = userHeight;
        this.userWeight = userWeight;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getUserSex() {
        return userSex;
    }

    public void setUserSex(int userSex) {
        this.userSex = userSex;
    }

    public String getUserAge() {
        return userAge;
    }

    public void setUserAge(String userAge) {
        this.userAge = userAge;
    }

    public String getUserHeight() {
        return userHeight;
    }

    public void setUserHeight(String userHeight) {
        this.userHeight = userHeight;
    }

    public String getUserWeight() {
        return userWeight;
    }

    public void setUserWeight(String userWeight) {
        this.userWeight = userWeight;
    }

    public String getUserStep() {
        return userStep;
    }

    public void setUserStep(String userStep) {
        this.userStep = userStep;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("UserModel{");
        sb.append("id=").append(id);
        sb.append(", userName='").append(userName).append('\'');
        sb.append(", userSex=").append(userSex);
        sb.append(", userAge='").append(userAge).append('\'');
        sb.append(", userHeight='").append(userHeight).append('\'');
        sb.append(", userWeight='").append(userWeight).append('\'');
        sb.append(", userStep='").append(userStep).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
