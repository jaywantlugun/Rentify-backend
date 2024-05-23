package com.jaywant.rentify.response;

import com.jaywant.rentify.models.Property;

public class PropertyResponse {
    private String message;
    private Property property;

    public PropertyResponse() {
    }

    public PropertyResponse(String message) {
        this.message = message;
    }

    public PropertyResponse(String message, Property property) {
        this.message = message;
        this.property = property;
    }

    // getters and setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }
}

