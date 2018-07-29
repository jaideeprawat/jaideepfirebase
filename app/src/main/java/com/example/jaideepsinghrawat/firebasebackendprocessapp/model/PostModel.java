package com.example.jaideepsinghrawat.firebasebackendprocessapp.model;

import java.util.Date;

public class PostModel {
    public String description;
    public String userId;
    public String imageUri;
    public String thumburi;
    public Date timestamp;

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getThumburi() {
        return thumburi;
    }

    public void setThumburi(String thumburi) {
        this.thumburi = thumburi;
    }

    public PostModel(){

    }
    public PostModel(String description, String userId, String imageUri, String thumburi, Date timestamp ){
        this.description=description;
        this.userId=userId;
        this.imageUri=imageUri;
        this.thumburi=thumburi;
        this.timestamp=timestamp;

    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }





    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(
            Date timestamp) {
        this.timestamp = timestamp;
    }



}
