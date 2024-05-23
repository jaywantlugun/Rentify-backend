package com.jaywant.rentify.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class LoginRequest {
    
    @Email(message = "Enter a valid email")
    @NotBlank(message = "Email is required")
    private String email;
    
    @NotNull(message = "Password is required")
    @NotBlank(message = "Password cannot be blank")
    private String password;
    
    public LoginRequest() {
    }

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
}
