package com.example.contactapp;

public class Contact {
    public Integer id;
    public String name;
    public String phonenummber;
    public String email;
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
