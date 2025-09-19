package com.resilenceindia.insurance.service;

import java.util.List;

import com.resilenceindia.insurance.entity.Policy;

public interface PolicyService {
	Policy create(Policy policy);

	Policy update(Long id, Policy policy);

	void delete(Long id);

	Policy get(Long id);

	List<Policy> getAll();
}
