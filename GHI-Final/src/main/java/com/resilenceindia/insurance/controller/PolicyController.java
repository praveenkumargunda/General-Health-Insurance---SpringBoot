package com.resilenceindia.insurance.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.resilenceindia.insurance.dto.PolicyRequest;
import com.resilenceindia.insurance.entity.Policy;
import com.resilenceindia.insurance.service.PolicyService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/policies")
public class PolicyController {

    private final PolicyService policyService;

    public PolicyController(PolicyService policyService) {
        this.policyService = policyService;
    }

    @PostMapping
    public ResponseEntity<Policy> create(@Valid @RequestBody PolicyRequest request) {
        Policy policy = toEntity(request);
        return ResponseEntity.ok(policyService.create(policy));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Policy> update(@PathVariable Long id, @Valid @RequestBody PolicyRequest request) {
        Policy policy = toEntity(request);
        return ResponseEntity.ok(policyService.update(id, policy));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        policyService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<Policy>> getAll() {
        return ResponseEntity.ok(policyService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Policy> get(@PathVariable Long id) {
        return ResponseEntity.ok(policyService.get(id));
    }

    private Policy toEntity(PolicyRequest request) {
        Policy policy = new Policy();
        policy.setName(request.getName());
        policy.setCoverageAmount(request.getCoverageAmount());
        policy.setPremiumAmount(request.getPremiumAmount());
        policy.setTerm(request.getTerm());
        policy.setDescription(request.getDescription());
        return policy;
    }
}
