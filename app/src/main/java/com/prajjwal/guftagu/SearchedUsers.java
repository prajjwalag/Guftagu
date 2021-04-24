package com.prajjwal.guftagu;

public class SearchedUsers {

    String name, status, image, phone, uid;

    public SearchedUsers() { }

    public SearchedUsers(String name, String status, String image, String phone, String uid) {
        this.name = name;
        this.status = status;
        this.image = image;
        this.phone = phone;
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
