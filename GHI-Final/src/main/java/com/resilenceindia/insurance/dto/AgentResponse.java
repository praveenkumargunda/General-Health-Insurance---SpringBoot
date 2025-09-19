package com.resilenceindia.insurance.dto;

import java.time.LocalDateTime;
import com.resilenceindia.insurance.entity.Agent;

public class AgentResponse {
    
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String dateOfBirth;
    private String address;
    private String city;
    private String state;
    private String pinCode;
    private String documentType;
    private String documentNumber;
    private Integer experienceYears;
    private Boolean isActive;
    private Boolean isApproved;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public AgentResponse() {}
    
    public AgentResponse(Agent agent) {
        this.id = agent.getId();
        this.firstName = agent.getFirstName();
        this.lastName = agent.getLastName();
        this.email = agent.getEmail();
        this.phoneNumber = agent.getPhoneNumber();
        this.dateOfBirth = agent.getDateOfBirth();
        this.address = agent.getAddress();
        this.city = agent.getCity();
        this.state = agent.getState();
        this.pinCode = agent.getPinCode();
        this.documentType = agent.getDocumentType();
        this.documentNumber = agent.getDocumentNumber();
        this.experienceYears = agent.getExperienceYears();
        this.isActive = agent.getIsActive();
        this.isApproved = agent.getIsApproved();
        this.createdAt = agent.getCreatedAt();
        this.updatedAt = agent.getUpdatedAt();
    }
    
    // Getters and Setters
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
    
    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    
    public String getPinCode() { return pinCode; }
    public void setPinCode(String pinCode) { this.pinCode = pinCode; }
    
    public String getDocumentType() { return documentType; }
    public void setDocumentType(String documentType) { this.documentType = documentType; }
    
    public String getDocumentNumber() { return documentNumber; }
    public void setDocumentNumber(String documentNumber) { this.documentNumber = documentNumber; }
    
    public Integer getExperienceYears() { return experienceYears; }
    public void setExperienceYears(Integer experienceYears) { this.experienceYears = experienceYears; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public Boolean getIsApproved() { return isApproved; }
    public void setIsApproved(Boolean isApproved) { this.isApproved = isApproved; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public String getFullName() {
        return firstName + " " + lastName;
    }
}