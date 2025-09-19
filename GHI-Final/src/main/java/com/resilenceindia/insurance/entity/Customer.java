package com.resilenceindia.insurance.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "customers")
public class Customer {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    @Column(name = "first_name", nullable = false)
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    @Column(name = "last_name", nullable = false)
    private String lastName;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Please provide a valid 10-digit Indian mobile number")
    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;
    
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Column(name = "password", nullable = false)
    private String password;
    
    @NotBlank(message = "Date of birth is required")
    @Column(name = "date_of_birth", nullable = false)
    private String dateOfBirth;
    
    @NotBlank(message = "Gender is required")
    @Column(name = "gender", nullable = false)
    private String gender;
    
    @NotBlank(message = "Favourite colour is required")
    @Column(name = "favourite_colour", nullable = false, length = 50)
    private String favouriteColour;

    
    @NotBlank(message = "Address is required")
    @Column(name = "address", nullable = false, length = 500)
    private String address;
    
    @NotBlank(message = "City is required")
    @Column(name = "city", nullable = false)
    private String city;
    
    @NotBlank(message = "State is required")
    @Column(name = "state", nullable = false)
    private String state;
    
    @NotBlank(message = "PIN code is required")
    @Pattern(regexp = "^[1-9][0-9]{5}$", message = "Please provide a valid 6-digit PIN code")
    @Column(name = "pin_code", nullable = false)
    private String pinCode;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @ManyToOne
    @JoinColumn(name = "agent_id")
    @JsonBackReference
    private Agent agent;

    public Agent getAgent() { return agent; }
    public void setAgent(Agent agent) { this.agent = agent; }
    
    // Constructors
    public Customer() {}
    
    public Customer(String firstName, String lastName, String email, String phoneNumber, 
                   String password, String dateOfBirth, String gender, String address, 
                   String city, String state, String pinCode, String favouriteColour) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.address = address;
        this.city = city;
        this.state = state;
        this.pinCode = pinCode;
        this.favouriteColour = favouriteColour;
    }
    
    // Getters and Setters
    public String getFavouriteColour() {
        return favouriteColour;
    }

    public void setFavouriteColour(String favouriteColour) {
        this.favouriteColour = favouriteColour;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    
    public String getPinCode() { return pinCode; }
    public void setPinCode(String pinCode) { this.pinCode = pinCode; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public String getFullName() {
        return firstName + " " + lastName;
    }
}