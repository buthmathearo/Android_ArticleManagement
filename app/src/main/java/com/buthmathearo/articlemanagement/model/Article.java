package com.buthmathearo.articlemanagement.model;

/**
 * Created by Buth Mathearo on 11/30/2015.
 */
public class Article {
    private int id;
    private String title;
    private String description;
    private String publishDate;
    private String image;
    private boolean isEnabled;
    private int userId;
    private String baseUrl = "http://hrdams.herokuapp.com/";
    private String imageWithoutBaseUrl;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getShortTitle() {
        if (title.length() > 60) {
            return title.substring(0, 60) + "..." ;
        } else {
            return title;
        }
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public String getShortDescription() {
        if (description.length() > 100) {
            return description.substring(0, 100) + "...";
        } else return  description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public String getImage() {
        return image;
    }

    public String getImageWithoutBaseUrl() {
        return imageWithoutBaseUrl;
    }

    public void setImage(String image) {
        this.image = baseUrl + image;
        this.imageWithoutBaseUrl = image;
    }

    public void setImageWithoutBaseUrl(String image) {
        this.image = baseUrl + image;
        this.imageWithoutBaseUrl = image;

    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
