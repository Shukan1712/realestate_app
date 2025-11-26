package com.example.finalproject;

public class UserProfile {
    public String uid;
    public String name;
    public String email;
    public String phone;

    public UserProfile() {
        // Default constructor required for calls to DataSnapshot.getValue(UserProfile.class)
    }

    public UserProfile(String uid, String name, String email, String phone) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }
}
