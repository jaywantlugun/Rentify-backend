package com.jaywant.rentify.Dto;

import com.jaywant.rentify.models.User;

import java.util.Set;

public class PropertyDTO {

    private Integer id;
    private Integer rent;
    private String area;
    private Integer bedrooms;
    private Integer bathrooms;
    private String nearbySpots;
    private String propertyImage;
    private User owner;
    private Set<Integer> appliedUsers;
    private Set<Integer> likedUsers;

    public PropertyDTO() {
    }

    public PropertyDTO(Integer id, Integer rent, String area, Integer bedrooms, Integer bathrooms, String nearbySpots, String propertyImage, User owner, Set<Integer> appliedUsers, Set<Integer> likedUsers) {
        this.id = id;
        this.rent = rent;
        this.area = area;
        this.bedrooms = bedrooms;
        this.bathrooms = bathrooms;
        this.nearbySpots = nearbySpots;
        this.propertyImage = propertyImage;
        this.owner = owner;
        this.appliedUsers = appliedUsers;
        this.likedUsers = likedUsers;
    }

    // Getters and Setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRent() {
        return rent;
    }

    public void setRent(Integer rent) {
        this.rent = rent;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public Integer getBedrooms() {
        return bedrooms;
    }

    public void setBedrooms(Integer bedrooms) {
        this.bedrooms = bedrooms;
    }

    public Integer getBathrooms() {
        return bathrooms;
    }

    public void setBathrooms(Integer bathrooms) {
        this.bathrooms = bathrooms;
    }

    public String getNearbySpots() {
        return nearbySpots;
    }

    public void setNearbySpots(String nearbySpots) {
        this.nearbySpots = nearbySpots;
    }

    public String getPropertyImage() {
        return propertyImage;
    }

    public void setPropertyImage(String propertyImage) {
        this.propertyImage = propertyImage;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Set<Integer> getAppliedUsers() {
        return appliedUsers;
    }

    public void setAppliedUsers(Set<Integer> appliedUsers) {
        this.appliedUsers = appliedUsers;
    }

    public Set<Integer> getLikedUsers() {
        return likedUsers;
    }

    public void setLikedUsers(Set<Integer> likedUsers) {
        this.likedUsers = likedUsers;
    }
}

