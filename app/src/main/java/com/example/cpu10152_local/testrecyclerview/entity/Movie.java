package com.example.cpu10152_local.testrecyclerview.entity;

/**
 * Created by cpu10152-local on 03/04/2018.
 */

public class Movie {

    private String title;
    private String description;
    private String url;

    public Movie (String title, String des, String url){
        this.title = title;
        this.description = des;
        this.url = url;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
