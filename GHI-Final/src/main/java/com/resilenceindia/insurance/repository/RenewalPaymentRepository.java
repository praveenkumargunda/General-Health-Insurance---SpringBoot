package com.resilenceindia.insurance.repository;


import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.resilenceindia.insurance.entity.Payment;

import java.util.Optional;

@Repository
public interface RenewalPaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findTopByPurchasedPolicyIdOrderByPaymentDateDesc(Long purchasedPolicyId);
    
    // Updated method using JPQL query since we can't navigate through relationship anymore
    @Query("SELECT p FROM Payment p JOIN PurchasedPolicy pp ON p.purchasedPolicyId = pp.id WHERE pp.customerId = :customerId ORDER BY p.paymentDate DESC")
    List<Payment> findByCustomerIdOrderByPaymentDateDesc(@Param("customerId") Long customerId);
    
    // Alternative method if you need payments by purchased policy IDs
    List<Payment> findByPurchasedPolicyIdInOrderByPaymentDateDesc(List<Long> purchasedPolicyIds);
}

