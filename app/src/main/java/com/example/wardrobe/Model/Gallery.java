package com.example.wardrobe.Model;

public class Gallery {
    private String imageUrl;
    private String userId;

    public Gallery(String imageUrl, String userId) {
        this.imageUrl = imageUrl;
        this.userId = userId;
    }

    public Gallery() {

    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
