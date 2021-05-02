package com.prajjwal.guftagu.Models;

public class FriendsModel {

    String friend_uid, date;

    public FriendsModel() {
    }

    public FriendsModel(String friend_uid, String date) {
        this.friend_uid = friend_uid;
        this.date = date;
    }

    public String getFriend_uid() {
        return friend_uid;
    }

    public void setFriend_uid(String friend_uid) {
        this.friend_uid = friend_uid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
