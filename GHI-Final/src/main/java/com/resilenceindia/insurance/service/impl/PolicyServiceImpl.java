package com.resilenceindia.insurance.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.resilenceindia.insurance.entity.Policy;
import com.resilenceindia.insurance.exception.PolicyNotFoundException;
import com.resilenceindia.insurance.repository.PolicyRepository;
import com.resilenceindia.insurance.service.PolicyService;


@Service
@Transactional
public class PolicyServiceImpl implements PolicyService {

	private final PolicyRepository policyRepository;

	public PolicyServiceImpl(PolicyRepository policyRepository) {
		this.policyRepository = policyRepository;
	}

	@Override
	public Policy create(Policy policy) {
		policy.setId(null);
		return policyRepository.save(policy);
	}

	@Override
	public Policy update(Long id, Policy updated) {
		Policy existing = policyRepository.findById(id).orElseThrow(() -> new PolicyNotFoundException(id));
		existing.setName(updated.getName());
		existing.setCoverageAmount(updated.getCoverageAmount());
		existing.setPremiumAmount(updated.getPremiumAmount());
		existing.setTerm(updated.getTerm());
		existing.setDescription(updated.getDescription());
		return policyRepository.save(existing);
	}

	@Override
	public void delete(Long id) {
		if (!policyRepository.existsById(id)) {
			throw new PolicyNotFoundException(id);
		}
		policyRepository.deleteById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public Policy get(Long id) {
		return policyRepository.findById(id).orElseThrow(() -> new PolicyNotFoundException(id));
	}

	@Override
	@Transactional(readOnly = true)
	public List<Policy> getAll() {
		return policyRepository.findAll();
	}
}
