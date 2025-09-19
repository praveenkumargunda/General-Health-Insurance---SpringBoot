package com.resilenceindia.insurance.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class ForgotPasswordRequest {
    
    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email")
    private String email;
    
    @NotBlank(message = "Favourite colour is required")
    private String favouriteColour;
    
    @NotBlank(message = "New password is required")
    private String newPassword;

    // Getters & Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFavouriteColour() { return favouriteColour; }
    public void setFavouriteColour(String favouriteColour) { this.favouriteColour = favouriteColour; }

    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
}
