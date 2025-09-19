package com.resilenceindia.insurance.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotNull;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentConfirmationRequest {
    @NotNull(message = "Transaction ID is required")
    private String transactionId;
    
    @NotNull(message = "Payment status is required")
    private String paymentStatus;
    
    @NotNull(message = "Payment mode is required")
    private String paymentMode; 
    
    // Explicit getters and setters
    public String getTransactionId() {
        return transactionId;
    }
    
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
    
    public String getPaymentStatus() {
        return paymentStatus;
    }
    
    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

	public String getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}
    
}
