package com.hades.adminpolyfit.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Hades on 15,October,2019
 **/

public class Exercise implements Serializable {
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("introduction")
    @Expose
    private String introduction;
    @SerializedName("content")
    @Expose
    private String content;
    @SerializedName("tips")
    @Expose
    private String tips;
    @SerializedName("sets")
    @Expose
    private Integer sets;
    @SerializedName("reps")
    @Expose
    private Integer reps;
    @SerializedName("video_url")
    @Expose
    private String video_url;
    @SerializedName("image_url")
    @Expose
    private String image_url;
    @SerializedName("id_level")
    @Expose
    private Integer id_level;
    @SerializedName("bodypartsArr")
    @Expose
    private Integer id_bodyparts;
    @SerializedName("rest")
    @Expose
    private Integer rest;


    public Exercise() {
    }

    //Insert


    public Exercise(int id, String title, String introduction, String content, String tips, Integer sets, Integer reps, String video_url, String image_url, Integer id_level, Integer id_bodyparts, Integer rest) {
        this.id = id;
        this.title = title;
        this.introduction = introduction;
        this.content = content;
        this.tips = tips;
        this.sets = sets;
        this.reps = reps;
        this.video_url = video_url;
        this.image_url = image_url;
        this.id_level = id_level;
        this.id_bodyparts = id_bodyparts;
        this.rest = rest;
    }

    public Exercise(String title, String introduction, String content, String tips, Integer sets, Integer reps, String video_url, String image_url, Integer id_level, Integer id_bodyparts, Integer rest) {
        this.title = title;
        this.introduction = introduction;
        this.content = content;
        this.tips = tips;
        this.sets = sets;
        this.reps = reps;
        this.video_url = video_url;
        this.image_url = image_url;
        this.id_level = id_level;
        this.id_bodyparts = id_bodyparts;
        this.rest = rest;
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

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public Integer getSets() {
        return sets;
    }

    public void setSets(Integer sets) {
        this.sets = sets;
    }

    public Integer getReps() {
        return reps;
    }

    public void setReps(Integer reps) {
        this.reps = reps;
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public Integer getId_level() {
        return id_level;
    }

    public void setId_level(Integer id_level) {
        this.id_level = id_level;
    }

    public Integer getId_bodyparts() {
        return id_bodyparts;
    }

    public void setId_bodyparts(Integer id_bodyparts) {
        this.id_bodyparts = id_bodyparts;
    }

    public Integer getRest() {
        return rest;
    }

    public void setRest(Integer rest) {
        this.rest = rest;
    }
}

