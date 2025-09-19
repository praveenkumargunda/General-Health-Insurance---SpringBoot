//package com.resilenceindia.insurance.repository;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//import java.time.LocalDate;
//import java.util.List;
//import java.util.Optional;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
//
//import com.resilenceindia.insurance.entity.PolicyStatus;
//import com.resilenceindia.insurance.entity.PurchasedPolicy;
//
//@DataJpaTest
//class PurchasedPolicyRepositoryTest {
//
//    @Autowired
//    private TestEntityManager entityManager;
//
//    @Autowired
//    private PurchasedPolicyRepository repository;
//
//    private PurchasedPolicy testPolicy1;
//    private PurchasedPolicy testPolicy2;
//    private PurchasedPolicy testPolicy3;
//
//    @BeforeEach
//    void setUp() {
//        testPolicy1 = new PurchasedPolicy();
//        testPolicy1.setCustomerId(1L);
//        testPolicy1.setPolicyId(1L);
//        testPolicy1.setStartDate(LocalDate.now());
//        testPolicy1.setEndDate(LocalDate.now().plusYears(1));
//        testPolicy1.setStatus(PolicyStatus.ACTIVE);
//
//        testPolicy2 = new PurchasedPolicy();
//        testPolicy2.setCustomerId(1L);
//        testPolicy2.setPolicyId(2L);
//        testPolicy2.setStartDate(LocalDate.now().minusYears(1));
//        testPolicy2.setEndDate(LocalDate.now().minusDays(1));
//        testPolicy2.setStatus(PolicyStatus.EXPIRED);
//
//        testPolicy3 = new PurchasedPolicy();
//        testPolicy3.setCustomerId(2L);
//        testPolicy3.setPolicyId(1L);
//        testPolicy3.setStartDate(LocalDate.now());
//        testPolicy3.setEndDate(LocalDate.now().plusDays(15));
//        testPolicy3.setStatus(PolicyStatus.ACTIVE);
//
//        entityManager.persistAndFlush(testPolicy1);
//        entityManager.persistAndFlush(testPolicy2);
//        entityManager.persistAndFlush(testPolicy3);
//    }
//
//    @Test
//    void testFindByCustomerId() {
//        List<PurchasedPolicy> policies = repository.findByCustomerId(1L);
//        assertEquals(2, policies.size());
//    }
//
//    @Test
//    void testFindByCustomerIdAndPolicyId() {
//        Optional<PurchasedPolicy> policy = repository.findByCustomerIdAndPolicyId(1L, 1L);
//        assertTrue(policy.isPresent());
//        assertEquals(testPolicy1.getId(), policy.get().getId());
//    }
//
//    @Test
//    void testFindByStatus() {
//        List<PurchasedPolicy> activePolicies = repository.findByStatus(PolicyStatus.ACTIVE);
//        assertEquals(2, activePolicies.size());
//
//        List<PurchasedPolicy> expiredPolicies = repository.findByStatus(PolicyStatus.EXPIRED);
//        assertEquals(1, expiredPolicies.size());
//    }
//
//    @Test
//    void testCountActivePoliciesByCustomer() {
//        Long count = repository.countActivePoliciesByCustomer(1L);
//        assertEquals(1L, count);
//    }
//
//    @Test
//    void testCountExpiredPoliciesByCustomer() {
//        Long count = repository.countExpiredPoliciesByCustomer(1L);
//        assertEquals(1L, count);
//    }
//
//    @Test
//    void testCountPoliciesExpiringSoon() {
//        LocalDate today = LocalDate.now();
//        LocalDate futureDate = today.plusDays(30);
//        
//        Long count = repository.countPoliciesExpiringSoon(2L, today, futureDate);
//        assertEquals(1L, count); // testPolicy3 expires in 15 days
//    }
//
//    @Test
//    void testCountActivePoliciesByCustomerAndPolicy() {
//        Long count = repository.countActivePoliciesByCustomerAndPolicy(1L, 1L);
//        assertEquals(1L, count);
//    }
//
//    @Test
//    void testFindExpiredPolicies() {
//        LocalDate currentDate = LocalDate.now();
//        List<PurchasedPolicy> expiredPolicies = repository.findExpiredPolicies(currentDate);
//        
//        // This should find testPolicy2 which has endDate before today but status EXPIRED
//        // Note: The query looks for ACTIVE policies with endDate < currentDate
//        // Since testPolicy2 is EXPIRED, it won't be returned
//        assertEquals(0, expiredPolicies.size());
//    }
//
//    @Test
//    void testFindPoliciesExpiringBetween() {
//        LocalDate startDate = LocalDate.now();
//        LocalDate endDate = startDate.plusDays(20);
//        
//        List<PurchasedPolicy> policies = repository.findPoliciesExpiringBetween(startDate, endDate);
//        assertEquals(1, policies.size()); // testPolicy3
//    }
//
//    @Test
//    void testFindActiveProfilesByCustomerId() {
//        Optional<PurchasedPolicy> policy = repository.findActiveProfilesByCustomerId(1L);
//        assertTrue(policy.isPresent());
//        assertEquals(PolicyStatus.ACTIVE, policy.get().getStatus());
//    }
//}