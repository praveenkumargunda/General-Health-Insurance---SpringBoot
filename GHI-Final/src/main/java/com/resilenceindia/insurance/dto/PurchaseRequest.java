package com.resilenceindia.insurance.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseRequest {
    @NotNull(message = "Policy ID is required")
    private Long policyId;
    
    @NotNull(message = "Customer ID is required")
    private Long customerId;
    
    private String email;
    
    private String firstName;       // Customer first name
    private String policyName;      // Policy name
    private BigDecimal premiumAmount;
    
    public Long getPolicyId() {
        return policyId;
    }
    
    public void setPolicyId(Long policyId) {
        this.policyId = policyId;
    }
    
    public Long getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getPolicyName() {
		return policyName;
	}

	public void setPolicyName(String policyName) {
		this.policyName = policyName;
	}

	public BigDecimal getPremiumAmount() {
		return premiumAmount;
	}

	public void setPremiumAmount(BigDecimal premiumAmount) {
		this.premiumAmount = premiumAmount;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
    
}