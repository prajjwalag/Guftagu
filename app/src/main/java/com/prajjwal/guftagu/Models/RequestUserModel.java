package com.prajjwal.guftagu.Models;

public class RequestUserModel {

    String request_type, request_uid, date;

    public RequestUserModel() {
    }

    public RequestUserModel(String request_type, String request_uid, String date) {
        this.request_type = request_type;
        this.request_uid = request_uid;
        this.date = date;
    }

    public String getRequest_type() {
        return request_type;
    }

    public void setRequest_type(String request_type) {
        this.request_type = request_type;
    }

    public String getRequest_uid() {
        return request_uid;
    }

    public void setRequest_uid(String request_uid) {
        this.request_uid = request_uid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
