package com.example.wardrobe.Model;

public class Gallery {
    private String imageUrl;
    public Gallery() {

    }
    public Gallery(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
