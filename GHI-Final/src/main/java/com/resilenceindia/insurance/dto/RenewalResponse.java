package com.resilenceindia.insurance.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.resilenceindia.insurance.entity.PaymentStatus;
import com.resilenceindia.insurance.entity.PolicyStatus;

public class RenewalResponse {   
    private Long paymentId;
    private BigDecimal amount;
    private PaymentStatus paymentStatus;
    private String paymentMode;
    private LocalDateTime paymentDate;
    private LocalDate nextPremiumDate;
    
   // private Long purchasedPolicyId; 
    private Long policyId;     
    private Long customerId;   
    private PolicyStatus policyStatus;

    private String message; // this one we added to display message for policies that are already renewed or in active

    public RenewalResponse() {
        
    }

    public RenewalResponse(Long paymentId, BigDecimal amount, PaymentStatus paymentStatus, String paymentMode,
                           LocalDateTime paymentDate, LocalDate nextPremiumDate,
                           Long policyId, Long customerId,
                           PolicyStatus policyStatus, String message) {
        this.paymentId = paymentId;
        this.amount = amount;
        this.paymentStatus = paymentStatus;
        this.paymentMode = paymentMode;
        this.paymentDate = paymentDate;
        this.nextPremiumDate = nextPremiumDate;
        this.policyId = policyId;
        this.customerId = customerId;
        this.policyStatus = policyStatus;
        this.message = message;
    }

    
    public Long getPaymentId() { return paymentId; }
    public void setPaymentId(Long paymentId) { this.paymentId = paymentId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getPaymentMode() { return paymentMode; }
    public void setPaymentMode(String paymentMode) { this.paymentMode = paymentMode; }

    public LocalDateTime getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDateTime paymentDate) { this.paymentDate = paymentDate; }

    public LocalDate getNextPremiumDate() { return nextPremiumDate; }
    public void setNextPremiumDate(LocalDate nextPremiumDate) { this.nextPremiumDate = nextPremiumDate; }

	public Long getPolicyId() { return policyId; }
    public void setPolicyId(Long policyId) { this.policyId = policyId; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public PolicyStatus getPolicyStatus() { return policyStatus; }
    public void setPolicyStatus(PolicyStatus policyStatus) { this.policyStatus = policyStatus; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
