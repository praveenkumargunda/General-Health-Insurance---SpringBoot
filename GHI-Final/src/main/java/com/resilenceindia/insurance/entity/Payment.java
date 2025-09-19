package com.resilenceindia.insurance.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

//import com.insurance.entity.PurchasedPolicy;

@Entity
@Table(name = "payments")
public class Payment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "payments_seq")
    @SequenceGenerator(name = "payments_seq", sequenceName = "payments_seq", allocationSize = 1)
    private Long id;
    
    @Column(name = "purchased_policy_id", nullable = false)
    private Long purchasedPolicyId;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;
    
    @Column(name = "payment_date")
    private LocalDateTime paymentDate;
    
    @Column(name = "next_premium_date")
    private LocalDate nextPremiumDate;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 20)
    private PaymentStatus paymentStatus;
    
    @Column(name = "payment_mode", length = 50)
    private String paymentMode;
    
    @Column(name = "transaction_id", unique = true, length = 100)
    private String transactionId;

	public Payment() {
		super();
	}

	public Payment(Long id, Long purchasedPolicyId, BigDecimal amount, LocalDateTime paymentDate,
			LocalDate nextPremiumDate, PaymentStatus paymentStatus, String paymentMode, String transactionId) {
		super();
		this.id = id;
		this.purchasedPolicyId = purchasedPolicyId;
		this.amount = amount;
		this.paymentDate = paymentDate;
		this.nextPremiumDate = nextPremiumDate;
		this.paymentStatus = paymentStatus;
		this.paymentMode = paymentMode;
		this.transactionId = transactionId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPurchasedPolicyId() {
		return purchasedPolicyId;
	}

	public void setPurchasedPolicyId(Long purchasedPolicyId) {
		this.purchasedPolicyId = purchasedPolicyId;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public LocalDateTime getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(LocalDateTime paymentDate) {
		this.paymentDate = paymentDate;
	}

	public LocalDate getNextPremiumDate() {
		return nextPremiumDate;
	}

	public void setNextPremiumDate(LocalDate nextPremiumDate) {
		this.nextPremiumDate = nextPremiumDate;
	}

	public PaymentStatus getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(PaymentStatus paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public String getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
}
