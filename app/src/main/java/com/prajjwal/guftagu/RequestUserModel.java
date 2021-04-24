package com.prajjwal.guftagu;

public class RequestUserModel {

    String request_type, name, status, image, uid;

    public RequestUserModel() {
    }

    public RequestUserModel(String request_type, String name, String status, String image, String uid) {
        this.request_type = request_type;
        this.name = name;
        this.status = status;
        this.image = image;
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getRequest_type() {
        return request_type;
    }

    public void setRequest_type(String request_type) {
        this.request_type = request_type;
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
}
