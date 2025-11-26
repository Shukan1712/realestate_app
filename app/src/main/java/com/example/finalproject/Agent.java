package com.example.finalproject;

/**
 * Model class representing a Real Estate Agent.
 * Corresponds to the "agents" path in Firebase.
 */
public class Agent {
    public String id, name, bio, imageUrl;
    public Double rating;
    public Boolean active;

    // Empty constructor required for Firebase
    public Agent() {}

    public Agent(String id, String name, Double rating, String imageUrl, String bio, Boolean active) {
        this.id = id;
        this.name = name;
        this.rating = rating;
        this.imageUrl = imageUrl;
        this.bio = bio;
        this.active = active;
    }
}
