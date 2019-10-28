package com.hades.adminpolyfit.Model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Hades on 26,October,2019
 **/
public class Ingredients {
    @SerializedName("id")
    private Integer idIngredients;
    @SerializedName("title")
    private String title;
   /* @SerializedName("price")
    private float price;
    @SerializedName("unit")*/
    /*private String unit;*/
    @SerializedName("image_url")
    private String imageUrl;

    public Ingredients() {
    }

    public Ingredients(Integer idIngredients, String title, /*float price,*/ /*String unit,*/ String imageUrl) {
        this.idIngredients = idIngredients;
        this.title = title;
        /*this.price = price;*/
        /*this.unit = unit;*/
        this.imageUrl = imageUrl;
    }

    public Ingredients(String title, /*float price,*/ /*String unit,*/ String imageUrl) {
        this.title = title;
        /*this.price = price;*/
        /*this.unit = unit;*/
        this.imageUrl = imageUrl;
    }

    public Integer getIdIngredients() {
        return idIngredients;
    }

    public void setIdIngredients(Integer idIngredients) {
        this.idIngredients = idIngredients;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

/*    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }*/

  /*  public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }*/

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
