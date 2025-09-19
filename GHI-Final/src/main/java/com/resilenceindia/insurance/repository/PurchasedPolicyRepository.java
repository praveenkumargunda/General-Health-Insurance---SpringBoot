package com.resilenceindia.insurance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.resilenceindia.insurance.entity.PolicyStatus;
import com.resilenceindia.insurance.entity.PurchasedPolicy;

import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PurchasedPolicyRepository extends JpaRepository<PurchasedPolicy, Long> {
    
    List<PurchasedPolicy> findByCustomerId(Long customerId);
    
    Optional<PurchasedPolicy> findByCustomerIdAndPolicyId(Long customerId, Long policyId);
    
    List<PurchasedPolicy> findByStatus(PolicyStatus status);
    
    @Modifying
    @Transactional
    void deleteByIdIn(List<Long> ids);
    
    @Modifying
    @Transactional
    List<PurchasedPolicy> findByStatusAndCreatedAtBefore(PolicyStatus status, LocalDateTime cutoffTime);

    
    @Query("SELECT pp FROM PurchasedPolicy pp WHERE pp.customerId = :customerId AND pp.status = 'ACTIVE'")
    Optional<PurchasedPolicy> findActiveProfilesByCustomerId(@Param("customerId") Long customerId);
    
    // Active policy count
    @Query("SELECT COUNT(pp) FROM PurchasedPolicy pp WHERE pp.customerId = :customerId AND pp.status = 'ACTIVE'")
    Long countActivePoliciesByCustomer(@Param("customerId") Long customerId);

    // Expired policy count
    @Query("SELECT COUNT(pp) FROM PurchasedPolicy pp WHERE pp.customerId = :customerId AND pp.status = 'EXPIRED'")
    Long countExpiredPoliciesByCustomer(@Param("customerId") Long customerId);

    // Policies expiring soon (e.g., in 30 days)
    @Query("SELECT COUNT(pp) FROM PurchasedPolicy pp WHERE pp.customerId = :customerId AND pp.status = 'ACTIVE' AND pp.endDate BETWEEN :today AND :futureDate")
    Long countPoliciesExpiringSoon(@Param("customerId") Long customerId,
                                   @Param("today") LocalDate today,
                                   @Param("futureDate") LocalDate futureDate);
    
    @Query("SELECT COUNT(pp) FROM PurchasedPolicy pp WHERE pp.customerId = :customerId AND pp.policyId = :policyId AND pp.status = 'ACTIVE'")
    Long countActivePoliciesByCustomerAndPolicy(@Param("customerId") Long customerId, @Param("policyId") Long policyId);
    
    @Query("SELECT pp FROM PurchasedPolicy pp WHERE pp.endDate < :currentDate AND pp.status = 'ACTIVE'")
    List<PurchasedPolicy> findExpiredPolicies(@Param("currentDate") LocalDate currentDate);
    
    @Query("SELECT pp FROM PurchasedPolicy pp WHERE pp.endDate BETWEEN :startDate AND :endDate AND pp.status = 'ACTIVE'")
    List<PurchasedPolicy> findPoliciesExpiringBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
