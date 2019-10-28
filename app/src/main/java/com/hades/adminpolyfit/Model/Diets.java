package com.hades.adminpolyfit.Model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Hades on 27,October,2019
 **/
public class Diets {
    @SerializedName("id")
    private Integer id;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("image_url")
    private String imageUrl;

    @SerializedName("id_level")
    private Integer idLevel;

    public Diets(Integer id, String title, String description, String imageUrl, Integer idLevel) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.idLevel = idLevel;
    }

    public Diets(String title, String description, String imageUrl, Integer idLevel) {
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.idLevel = idLevel;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getIdLevel() {
        return idLevel;
    }

    public void setIdLevel(Integer idLevel) {
        this.idLevel = idLevel;
    }
}
