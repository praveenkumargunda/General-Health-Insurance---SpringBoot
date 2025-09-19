package com.resilenceindia.insurance.dto;

import java.math.BigDecimal;

import com.resilenceindia.insurance.entity.Policy.Term;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class PolicyRequest {

	@NotBlank
	@Size(max = 100)
	private String name;

	@NotNull
	@DecimalMin("0.01")
	@Digits(integer = 15, fraction = 2)
	private BigDecimal coverageAmount;

	@NotNull
	@DecimalMin("0.01")
	@Digits(integer = 15, fraction = 2)
	private BigDecimal premiumAmount;

	@NotNull
	private Term term;

	@Size(max = 500)
	private String description;

	// Getters and Setters
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
}
