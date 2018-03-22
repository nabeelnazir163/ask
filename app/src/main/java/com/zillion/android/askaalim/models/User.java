package com.zillion.android.askaalim.models;


import java.io.Serializable;

public class User implements Serializable {

    private String address;
    private String image;
    private String name;
    private String uid;
    private String email;
    private String fiqah;
    private String userType;

    public User() {
    }

    public User(String address, String image, String name, String uid, String email, String fiqah, String usertype) {
        this.address = address;
        this.image = image;
        this.name = name;
        this.uid = uid;
        this.email = email;
        this.fiqah = fiqah;
        this.userType = usertype;
    }

    public String getFiqah() {
        return fiqah;
    }

    public void setFiqah(String fiqah) {
        this.fiqah = fiqah;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}