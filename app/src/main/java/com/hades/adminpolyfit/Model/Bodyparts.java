package com.hades.adminpolyfit.Model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Hades on 25,October,2019
 **/
public class Bodyparts {
    @SerializedName("id")
    private Integer idBodyPart;
    @SerializedName("title")
    private String title;
    @SerializedName("image_url")
    private String imageUrl;
    @SerializedName("createAt")
    private String createAt;
    @SerializedName("updateAt")
    private String updateAt;

    private boolean isChecked;

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public Bodyparts() {
    }

    public Bodyparts(Integer idBodyPart, String title, String imageUrl, String createAt, String updateAt) {
        this.idBodyPart = idBodyPart;
        this.title = title;
        this.imageUrl = imageUrl;
        this.createAt = createAt;
        this.updateAt = updateAt;
    }

    public Bodyparts(String title, String imageUrl, String createAt, String updateAt) {
        this.title = title;
        this.imageUrl = imageUrl;
        this.createAt = createAt;
        this.updateAt = updateAt;
    }

    public Integer getIdBodyPart() {
        return idBodyPart;
    }

    public void setIdBodyPart(Integer idBodyPart) {
        this.idBodyPart = idBodyPart;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public String getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(String updateAt) {
        this.updateAt = updateAt;
    }
}
