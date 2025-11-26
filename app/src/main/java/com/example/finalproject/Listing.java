package com.example.finalproject;

import java.util.List;

public class Listing {
    public String id;
    public String title;
    public Double price;
    public String address;
    public String city;
    public String province;
    public String postalCode;
    public String description;
    public Integer bedrooms;
    public Integer bathrooms;
    public Integer sqFt;
    public String propertyType;
    public String contactPhone;
    public String contactEmail;
    public Long timestamp;
    public Double lat;
    public Double lng;
    public Boolean favourite;
    public List<String> photos; 

    public Listing() {
        // Required empty constructor for Firebase
    }

    public Listing(String id, String title, Double price, String address, String city, String province, 
                   String postalCode, String description, Integer bedrooms, Integer bathrooms, 
                   Integer sqFt, String propertyType, String contactPhone, String contactEmail, 
                   Long timestamp, Double lat, Double lng, Boolean favourite, List<String> photos) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.address = address;
        this.city = city;
        this.province = province;
        this.postalCode = postalCode;
        this.description = description;
        this.bedrooms = bedrooms;
        this.bathrooms = bathrooms;
        this.sqFt = sqFt;
        this.propertyType = propertyType;
        this.contactPhone = contactPhone;
        this.contactEmail = contactEmail;
        this.timestamp = timestamp;
        this.lat = lat;
        this.lng = lng;
        this.favourite = favourite;
        this.photos = photos;
    }
}
