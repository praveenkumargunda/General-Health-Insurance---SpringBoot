package com.resilenceindia.insurance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.resilenceindia.insurance.entity.Payment;
import com.resilenceindia.insurance.entity.PaymentStatus;

import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    Optional<Payment> findByPurchasedPolicyId(Long purchasedPolicyId);
    
    Optional<Payment> findByTransactionId(String transactionId);
    
    List<Payment> findByPaymentStatus(PaymentStatus paymentStatus);
    
    @Modifying
    @Transactional
    void deleteByPurchasedPolicyIdIn(List<Long> policyIds);
    
    @Query("SELECT p FROM Payment p WHERE p.purchasedPolicyId = :purchasedPolicyId ORDER BY p.paymentDate DESC")
    List<Payment> findPaymentHistoryByPolicyId(@Param("purchasedPolicyId") Long purchasedPolicyId);
    
    @Query("SELECT p FROM Payment p WHERE p.paymentDate BETWEEN :startDate AND :endDate AND p.paymentStatus = 'COMPLETED'")
    List<Payment> findCompletedPaymentsBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT p FROM Payment p WHERE p.paymentStatus = 'PENDING' AND p.id IN (SELECT pp.id FROM PurchasedPolicy pp WHERE pp.id = p.purchasedPolicyId)")
    List<Payment> findPendingPaymentsWithPolicyDetails();
    
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.paymentStatus = 'COMPLETED' AND p.paymentDate BETWEEN :startDate AND :endDate")
    Double getTotalRevenueBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
