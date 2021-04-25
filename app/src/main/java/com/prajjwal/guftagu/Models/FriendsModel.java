package com.prajjwal.guftagu.Models;

public class FriendsModel {

    String name, status, image, friend_uid;

    public FriendsModel() {
    }

    public FriendsModel(String name, String status, String image, String friend_uid) {
        this.name = name;
        this.status = status;
        this.image = image;
        this.friend_uid = friend_uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getFriend_uid() {
        return friend_uid;
    }

    public void setFriend_uid(String friend_uid) {
        this.friend_uid = friend_uid;
    }
}
