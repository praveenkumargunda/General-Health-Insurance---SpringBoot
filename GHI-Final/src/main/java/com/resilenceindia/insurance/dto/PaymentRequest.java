package com.resilenceindia.insurance.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
public class PaymentRequest {
    @NotNull(message = "Purchased Policy ID is required")
    private Long purchasedPolicyId;
    
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;
    
    @NotNull(message = "Payment mode is required")
    private String paymentMode;
}
