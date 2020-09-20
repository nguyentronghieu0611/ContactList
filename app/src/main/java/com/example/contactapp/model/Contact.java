package com.example.contactapp.model;

import com.google.gson.annotations.SerializedName;

public class Contact {
    @SerializedName("id")
    public Integer id;
    @SerializedName("name")
    public String name;
    @SerializedName("phonenummber")
    public String phonenummber;
    @SerializedName("email")
    public String email;
    @SerializedName("image")
    public byte[] image;

    public Contact(String name, String phonenummber, String email, byte[] image) {
        this.name = name;
        this.phonenummber = phonenummber;
        this.email = email;
        this.image = image;
    }

    public Contact(Integer id,String name, String phonenummber, String email, byte[] image) {
        this.id = id;
        this.name = name;
        this.phonenummber = phonenummber;
        this.email = email;
        this.image = image;
    }
}
