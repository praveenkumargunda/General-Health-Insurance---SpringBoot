package com.resilenceindia.insurance.dto;

import java.math.BigDecimal;

public class RenewalPaymentRequest {

    private Long policyId;
    private BigDecimal amount;
    private String paymentMode; // CARD, UPI, NETBANKING
    private String option; // half-yearly / yearly
    private Long customerId;

    public RenewalPaymentRequest() {}

    public RenewalPaymentRequest(Long policyId, BigDecimal amount, String paymentMode, String option, Long customerId) {
        super();
        this.policyId = policyId;
        this.amount = amount;
        this.paymentMode = paymentMode;
        this.option = option;
        this.customerId = customerId;
    }

    public Long getpolicyId() {
        return policyId;
    }
    
    public void setpolicyId(Long policyId) {
        this.policyId = policyId;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public String getPaymentMode() {
        return paymentMode;
    }
    
    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }
    
    public String getOption() {
        return option;
    }
    
    public void setOption(String option) {
        this.option = option;
    }
    
    public Long getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
}