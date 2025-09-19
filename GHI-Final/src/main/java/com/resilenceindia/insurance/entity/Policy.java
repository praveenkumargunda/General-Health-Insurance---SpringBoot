package com.resilenceindia.insurance.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "policies")
public class Policy {

	public enum Term {
		HALF_YEARLY, YEARLY
	}

	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "policies_seq")
    @SequenceGenerator(name = "policies_seq", sequenceName = "policies_seq", allocationSize = 1)
	@Column(name = "policy_id")
	private Long id;

	@NotBlank
	@Size(max = 100)
	@Column(name = "name", nullable = false, length = 100)
	private String name;

	@NotNull
	@DecimalMin(value = "0.01")
	@Digits(integer = 15, fraction = 2)
	@Column(name = "coverage_amount", nullable = false, precision = 15, scale = 2)
	private BigDecimal coverageAmount;

	@NotNull
	@DecimalMin(value = "0.01")
	@Digits(integer = 15, fraction = 2)
	@Column(name = "premium_amount", nullable = false, precision = 15, scale = 2)
	private BigDecimal premiumAmount;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "term", nullable = false, length = 20)
	private Term term;

	@Size(max = 500)
	@Column(name = "description", length = 500)
	private String description;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@PrePersist
	public void onCreate() {
		createdAt = LocalDateTime.now();
	}

	@PreUpdate
	public void onUpdate() {
		updatedAt = LocalDateTime.now();
	}

	// Getters and Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getCoverageAmount() {
		return coverageAmount;
	}

	public void setCoverageAmount(BigDecimal coverageAmount) {
		this.coverageAmount = coverageAmount;
	}

	public BigDecimal getPremiumAmount() {
		return premiumAmount;
	}

	public void setPremiumAmount(BigDecimal premiumAmount) {
		this.premiumAmount = premiumAmount;
	}

	public Term getTerm() {
		return term;
	}

	public void setTerm(Term term) {
		this.term = term;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}
}
