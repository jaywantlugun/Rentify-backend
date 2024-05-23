package com.jaywant.rentify.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Column(name = "rent", nullable = false)
    private Integer rent;

    @NotNull
    @Column(name = "area", nullable = false)
    private String area;

    @NotNull
    @Column(name = "bedrooms", nullable = false)
    private Integer bedrooms;

    @NotNull
    @Column(name = "bathrooms", nullable = false)
    private Integer bathrooms;

    @NotNull
    @Column(name = "nearby_spots")
    private String nearbySpots;

    @NotNull
    @Column(name = "image_url")
    private String imageUrl;

    
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @ElementCollection
    @CollectionTable(name = "property_applications", joinColumns = @JoinColumn(name = "property_id"))
    @Column(name = "user_id")
    private Set<Integer> appliedUsers = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "property_likes", joinColumns = @JoinColumn(name = "property_id"))
    @Column(name = "user_id")
    private Set<Integer> likedUsers = new HashSet<>();

    public Property() {
    }

    public Property(Integer id, Integer rent, String area, Integer bedrooms, Integer bathrooms, String nearbySpots, String imageUrl, User owner) {
        super();
        this.id = id;
        this.rent = rent;
        this.area = area;
        this.bedrooms = bedrooms;
        this.bathrooms = bathrooms;
        this.nearbySpots = nearbySpots;
        this.imageUrl = imageUrl;
        this.owner = owner;
    }

    // getters and setters for all fields, including imageUrl, appliedUsers, and likedUsers

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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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
