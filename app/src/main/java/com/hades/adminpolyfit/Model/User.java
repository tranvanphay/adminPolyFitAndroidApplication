package com.hades.adminpolyfit.Model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by Hades on 29,October,2019
 **/
public class User {
    @SerializedName("id")
    private int id;
    @SerializedName("phoneNumber")
    private String phoneNumber;
    @SerializedName("username")
    private String userName;
    /*@SerializedName("isVerified")
    private int isVerified;*/
    @SerializedName("password")
    private String password;
    @SerializedName("weight")
    private float weight;
    @SerializedName("height")
    private float height;
    @SerializedName("bmi")
    private float bmi;
    @SerializedName("gender")
    private int gender;
    @SerializedName("display_name")
    private String displayName;
    @SerializedName("createdAt")
    private Date createdAt;
    @SerializedName("updatedAt")
    private Date updatedAt;
    @SerializedName("firebase_token")
    private String token;
/*    @SerializedName("isOnline")
    private int isOnline;*/

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User(int id, String phoneNumber, String userName, int isVerified, String password, float weight, float height, float bmi, int gender, String displayName, Date createdAt, Date updatedAt, boolean isOnline) {
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.userName = userName;
        /*this.isVerified = isVerified;*/
        this.password = password;
        this.weight = weight;
        this.height = height;
        this.bmi = bmi;
        this.gender = gender;
        this.displayName = displayName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

  /*  public int getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(int isVerified) {
        this.isVerified = isVerified;
    }*/

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getBmi() {
        return bmi;
    }

    public void setBmi(float bmi) {
        this.bmi = bmi;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

   /* public int getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(int isOnline) {
        this.isOnline = isOnline;
    }*/

    public User(int id, String phoneNumber, String userName, int isVerified, String password, float weight, float height, float bmi, int gender, String displayName, Date createdAt, Date updatedAt, int isOnline) {

        this.id = id;
        this.phoneNumber = phoneNumber;
        this.userName = userName;
        /*this.isVerified = isVerified;*/
        this.password = password;
        this.weight = weight;
        this.height = height;
        this.bmi = bmi;
        this.gender = gender;
        this.displayName = displayName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        /*this.isOnline = isOnline;*/
    }
}
