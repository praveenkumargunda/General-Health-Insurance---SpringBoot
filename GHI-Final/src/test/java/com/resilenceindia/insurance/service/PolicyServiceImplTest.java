package com.resilenceindia.insurance.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.resilenceindia.insurance.entity.Policy;
import com.resilenceindia.insurance.entity.Policy.Term;
import com.resilenceindia.insurance.exception.PolicyNotFoundException;
import com.resilenceindia.insurance.repository.PolicyRepository;
import com.resilenceindia.insurance.service.impl.PolicyServiceImpl;

class PolicyServiceImplTest {

    @Mock
    private PolicyRepository policyRepository;

    @InjectMocks
    private PolicyServiceImpl policyService;

    private Policy policy;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        policy = new Policy();
        policy.setId(1L);
        policy.setName("Health Insurance");
        policy.setCoverageAmount(BigDecimal.valueOf(500000));
        policy.setPremiumAmount(BigDecimal.valueOf(15000));
        policy.setTerm(Term.YEARLY);
        policy.setDescription("Covers medical expenses");
    }

    @Test
    void testCreatePolicy() {
        when(policyRepository.save(any(Policy.class))).thenReturn(policy);

        Policy created = policyService.create(policy);

        assertNotNull(created);
        assertEquals("Health Insurance", created.getName());
    }

    @Test
    void testUpdatePolicy() {
        when(policyRepository.findById(1L)).thenReturn(Optional.of(policy));
        when(policyRepository.save(any(Policy.class))).thenReturn(policy);

        Policy updated = policyService.update(1L, policy);

        assertEquals("Health Insurance", updated.getName());
    }

    @Test
    void testUpdatePolicy_NotFound() {
        when(policyRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(PolicyNotFoundException.class, () -> policyService.update(99L, policy));
    }

    @Test
    void testDeletePolicy() {
        when(policyRepository.existsById(1L)).thenReturn(true);
        doNothing().when(policyRepository).deleteById(1L);

        assertDoesNotThrow(() -> policyService.delete(1L));
        verify(policyRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeletePolicy_NotFound() {
        when(policyRepository.existsById(99L)).thenReturn(false);

        assertThrows(PolicyNotFoundException.class, () -> policyService.delete(99L));
    }

    @Test
    void testGetPolicy() {
        when(policyRepository.findById(1L)).thenReturn(Optional.of(policy));

        Policy found = policyService.get(1L);

        assertEquals("Health Insurance", found.getName());
    }

    @Test
    void testGetPolicy_NotFound() {
        when(policyRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(PolicyNotFoundException.class, () -> policyService.get(99L));
    }

    @Test
    void testGetAllPolicies() {
        when(policyRepository.findAll()).thenReturn(Arrays.asList(policy));

        List<Policy> policies = policyService.getAll();

        assertEquals(1, policies.size());
    }
}

