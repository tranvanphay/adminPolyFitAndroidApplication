package com.hades.adminpolyfit.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Hades on 21,October,2019
 **/
public class Dish implements Serializable {
    @SerializedName("id")
    private int id;
    @SerializedName("title")
    private String title;
    @SerializedName("image_url")
    private String imageUrl;
    @SerializedName("protein")
    private double protein;
    @SerializedName("fat")
    private double fat;
    @SerializedName("carb")
    private double carb;
    @SerializedName("calories")
    private double calories;
    @SerializedName("id_meals")
    private Integer idMeals;
    @SerializedName("description")
    private String description;
    @SerializedName("ingredientsArr")
    private Integer id_ingredients;

    public Integer getId_ingredients() {
        return id_ingredients;
    }

    public void setId_ingredients(Integer id_ingredients) {
        this.id_ingredients = id_ingredients;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Dish() {
    }

    public Dish(int id, String title, String imageUrl, double protein, double fat, double carb, double calories, Integer idMeals/*, Integer id_ingredients*/) {
        this.id = id;
        this.title = title;
        this.imageUrl = imageUrl;
        this.protein = protein;
        this.fat = fat;
        this.carb = carb;
        this.calories = calories;
        this.idMeals = idMeals;
        /*this.id_ingredients = id_ingredients;*/
    }

    public Dish(String title, String imageUrl, double protein, double fat, double carb, double calories, Integer idMeals/*, Integer id_ingredients*/) {
        this.title = title;
        this.imageUrl = imageUrl;
        this.protein = protein;
        this.fat = fat;
        this.carb = carb;
        this.calories = calories;
        this.idMeals = idMeals;
        /*this.id_ingredients = id_ingredients;*/
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

    public double getProtein() {
        return protein;
    }

    public void setProtein(double protein) {
        this.protein = protein;
    }

    public double getFat() {
        return fat;
    }

    public void setFat(double fat) {
        this.fat = fat;
    }

    public double getCarb() {
        return carb;
    }

    public void setCarb(double carb) {
        this.carb = carb;
    }

    public double getCalories() {
        return calories;
    }

    public void setCalories(double calories) {
        this.calories = calories;
    }

    public Integer getIdMeals() {
        return idMeals;
    }

    public void setIdMeals(Integer idMeals) {
        this.idMeals = idMeals;
    }

   /* public Integer getId_ingredients() {
        return id_ingredients;
    }

    public void setId_ingredients(Integer id_ingredients) {
        this.id_ingredients = id_ingredients;
    }*/
}
