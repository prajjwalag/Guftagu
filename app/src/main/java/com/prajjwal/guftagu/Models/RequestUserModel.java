package com.prajjwal.guftagu.Models;

public class RequestUserModel {

    String request_type, name, status, image, request_uid;

    public RequestUserModel() {
    }

    public RequestUserModel(String request_type, String name, String status, String image, String request_uid) {
        this.request_type = request_type;
        this.name = name;
        this.status = status;
        this.image = image;
        this.request_uid = request_uid;
    }

    public String getRequest_uid() {
        return request_uid;
    }

    public void setRequest_uid(String request_uid) {
        this.request_uid = request_uid;
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
