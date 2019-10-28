package com.hades.adminpolyfit.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Hades on 23,October,2019
 **/
public class Meals {
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("image_url")
    private String imageUrl;
    @SerializedName("id_diets")
    @Expose
    private int dietId;

    public Meals() {
    }

    public Meals(int id, String title, String imageUrl, int dietId) {
        this.id = id;
        this.title = title;
        this.imageUrl = imageUrl;
        this.dietId = dietId;
    }

    public Meals(String title, String imageUrl, int dietId) {
        this.title = title;
        this.imageUrl = imageUrl;
        this.dietId = dietId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getDietId() {
        return dietId;
    }

    public void setDietId(int dietId) {
        this.dietId = dietId;
    }
}
