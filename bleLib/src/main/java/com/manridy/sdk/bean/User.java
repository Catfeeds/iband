package com.manridy.sdk.bean;

/**
 * Created by jarLiao on 17/2/15.
 */
public class User {
    private int id;//数据索引
    private String userName;//用户名
    private int userSex;//性别
    private String userAge;//年龄
    private String userHeight;//身高
    private String userWeight;//体重
    private String userStep;//步长

    public User() {
    }

    public User(String userHeight, String userWeight) {
        this.userHeight = userHeight;
        this.userWeight = userWeight;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", userSex=" + userSex +
                ", userAge='" + userAge + '\'' +
                ", userHeight='" + userHeight + '\'' +
                ", userWeight='" + userWeight + '\'' +
                ", userStep='" + userStep + '\'' +
                '}';
    }
}
