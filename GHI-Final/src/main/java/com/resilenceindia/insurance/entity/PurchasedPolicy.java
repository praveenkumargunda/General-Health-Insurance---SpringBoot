package com.resilenceindia.insurance.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
	    name = "purchased_policies",
	    uniqueConstraints = @UniqueConstraint(columnNames = {"customer_id", "policy_id"})
	)
public class PurchasedPolicy {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "purchased_policies_seq")
    @SequenceGenerator(name = "purchased_policies_seq", sequenceName = "purchased_policies_seq", allocationSize = 1)
    private Long id;
    
    @Column(name = "customer_id", nullable = false)
    private Long customerId;
    
    @Column(name = "policy_id", nullable = false)
    private Long policyId;
    
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PolicyStatus status;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDate updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDate.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDate.now();
    }
    
	public PurchasedPolicy() {
		super();
	}
	
	public PurchasedPolicy(Long id, Long customerId, Long policyId, LocalDate startDate, LocalDate endDate,
			PolicyStatus status, LocalDateTime createdAt, LocalDate updatedAt) {
		super();
		this.id = id;
		this.customerId = customerId;
		this.policyId = policyId;
		this.startDate = startDate;
		this.endDate = endDate;
		this.status = status;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	public Long getPolicyId() {
		return policyId;
	}

	public void setPolicyId(Long policyId) {
		this.policyId = policyId;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	public PolicyStatus getStatus() {
		return status;
	}

	public void setStatus(PolicyStatus status) {
		this.status = status;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDate getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDate updatedAt) {
		this.updatedAt = updatedAt;
	}
    
    
}
