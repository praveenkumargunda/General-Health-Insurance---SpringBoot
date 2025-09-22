package com.resilenceindia.insurance.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.resilenceindia.insurance.dto.PaymentConfirmationRequest;
import com.resilenceindia.insurance.dto.PurchaseRequest;
import com.resilenceindia.insurance.dto.PurchaseResponse;
import com.resilenceindia.insurance.entity.Customer;
import com.resilenceindia.insurance.entity.Payment;
import com.resilenceindia.insurance.entity.PaymentStatus;
import com.resilenceindia.insurance.entity.Policy;
import com.resilenceindia.insurance.entity.PolicyStatus;
import com.resilenceindia.insurance.entity.PurchasedPolicy;
import com.resilenceindia.insurance.exception.PaymentException;
import com.resilenceindia.insurance.exception.PurchaseException;
import com.resilenceindia.insurance.repository.CustomerRepository;
import com.resilenceindia.insurance.repository.PaymentRepository;
import com.resilenceindia.insurance.repository.PolicyRepository;
import com.resilenceindia.insurance.repository.PurchasedPolicyRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class PurchaseService {
    
    @Autowired
    private PurchasedPolicyRepository purchasedPolicyRepository;
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private PolicyRepository policyRepository;
    
    @Autowired
    private CustomerRepository customerRepository;
    
    private static final Logger log = LoggerFactory.getLogger(PurchaseService.class);
    
    public PurchaseResponse initiatePurchase(PurchaseRequest request) {
        // Validate customer exists
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new PurchaseException("Customer not found"));

        // Validate policy exists
        Policy policy = policyRepository.findById(request.getPolicyId())
                .orElseThrow(() -> new PurchaseException("Policy not found"));

        // Check if customer already has this policy
        Optional<PurchasedPolicy> existingPolicyOpt =
                purchasedPolicyRepository.findByCustomerIdAndPolicyId(customer.getId(), request.getPolicyId());

        if (existingPolicyOpt.isPresent()) {
            PurchasedPolicy existingPolicy = existingPolicyOpt.get();

            // Case 1: Already Active -> reject
            if (existingPolicy.getStatus() == PolicyStatus.ACTIVE) {
                throw new PurchaseException("Customer already has an active policy of this type");
            }

            // Case 2: Already Expired -> reject
            if (existingPolicy.getStatus() == PolicyStatus.EXPIRED) {
                throw new PurchaseException("Customer policy expired. Customer needs to renew it");
            }

            // Case 3: Pending Payment -> reuse transaction
            if (existingPolicy.getStatus() == PolicyStatus.PENDING_PAYMENT) {
                Payment existingPayment = paymentRepository.findByPurchasedPolicyId(existingPolicy.getId())
                        .orElseThrow(() -> new PaymentException("Payment record not found for pending policy"));

                PurchaseResponse response = new PurchaseResponse();
                response.setTransactionId(existingPayment.getTransactionId());
                response.setFirstName(customer.getFirstName());
                response.setPolicyName(policy.getName());
                response.setPremiumAmount(policy.getPremiumAmount());

                return response;
            }
        }

        // Case 4: No policy bought → create new record
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = calculateEndDate(startDate, policy.getTerm());

        PurchasedPolicy purchasedPolicy = new PurchasedPolicy();
        purchasedPolicy.setCustomerId(request.getCustomerId());
        purchasedPolicy.setPolicyId(request.getPolicyId());
        purchasedPolicy.setStartDate(startDate);
        purchasedPolicy.setEndDate(endDate);
        purchasedPolicy.setStatus(PolicyStatus.PENDING_PAYMENT);

        PurchasedPolicy saved = purchasedPolicyRepository.save(purchasedPolicy);

        // Create payment record
        Payment payment = new Payment();
        payment.setPurchasedPolicyId(saved.getId());
        payment.setAmount(policy.getPremiumAmount());
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setTransactionId(generateTransactionId());

        Payment savedPayment = paymentRepository.save(payment);

        // Return response
        PurchaseResponse response = new PurchaseResponse();
        response.setTransactionId(savedPayment.getTransactionId());
        response.setFirstName(customer.getFirstName());
        response.setPolicyName(policy.getName());
        response.setPremiumAmount(policy.getPremiumAmount());

        return response;
    }

    
    public String confirmPurchase(PaymentConfirmationRequest request) {
        // Find payment by transaction ID
        Payment payment = paymentRepository.findByTransactionId(request.getTransactionId())
                .orElseThrow(() -> new PaymentException("Payment time limit exceeded"));

        Optional<PurchasedPolicy> policyOpt = purchasedPolicyRepository.findById(payment.getPurchasedPolicyId());

        // Update payment status
        if ("COMPLETED".equals(request.getPaymentStatus())) {
            payment.setPaymentStatus(PaymentStatus.COMPLETED);
            payment.setPaymentDate(LocalDateTime.now());
            policyOpt.ifPresent(p -> payment.setNextPremiumDate(p.getEndDate()));
            payment.setPaymentMode(request.getPaymentMode());
            
            //Update purchased policy details
            if (policyOpt.isPresent()) {
                PurchasedPolicy purchasedPolicy = policyOpt.get();
                purchasedPolicy.setStatus(PolicyStatus.ACTIVE);
                purchasedPolicyRepository.save(purchasedPolicy);
            }
            paymentRepository.save(payment);
            return "Payment confirmation processed successfully";
        } else {
            payment.setPaymentStatus(PaymentStatus.FAILED);
            payment.setPaymentDate(LocalDateTime.now());
            payment.setPaymentMode(request.getPaymentMode());
            paymentRepository.save(payment);

            // Delete purchased policy and payment together
            policyOpt.ifPresent(purchasedPolicy -> purchasedPolicyRepository.delete(purchasedPolicy));
            paymentRepository.delete(payment);

            return "Payment failed. Policy removed. Please initiate a new purchase.";
        }
    }
    
    private LocalDate calculateEndDate(LocalDate startDate, Policy.Term term) {
	    	return switch (term) {
	        case HALF_YEARLY -> startDate.plusMonths(6);
	        case YEARLY -> startDate.plusYears(1);
	        default -> startDate.plusYears(1);
	    };
    }
    
    private String generateTransactionId() {
        return "TXN_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }
    
    public List<PurchaseResponse> getActivePolicies(Long customerId) {
        return purchasedPolicyRepository.findByCustomerId(customerId).stream()
                .filter(policy -> policy.getStatus() == PolicyStatus.ACTIVE)
                .map(purchasedPolicy -> {
                    Optional<Policy> policyOpt = policyRepository.findById(purchasedPolicy.getPolicyId());
                    Policy policy = policyOpt.orElseThrow(() -> 
                        new PurchaseException("Policy not found for ID: " + purchasedPolicy.getPolicyId())
                    );

                    PurchaseResponse response = new PurchaseResponse();
                    response.setPolicyId(purchasedPolicy.getPolicyId());
                    response.setStartDate(purchasedPolicy.getStartDate());
                    response.setEndDate(purchasedPolicy.getEndDate());
                    response.setStatus(purchasedPolicy.getStatus().toString());
                    response.setPolicyName(policy.getName());
                    response.setPremiumAmount(policy.getPremiumAmount());

                    return response;
                })
                .toList();
    }


    public List<PurchaseResponse> getExpiredPolicies(Long customerId) {
        return purchasedPolicyRepository.findByCustomerId(customerId).stream()
                .filter(policy -> policy.getStatus() == PolicyStatus.EXPIRED)
                .map(purchasedPolicy -> {
                    Optional<Policy> policyOpt = policyRepository.findById(purchasedPolicy.getPolicyId());
                    Policy policy = policyOpt.orElseThrow(() -> new PurchaseException("Policy not found for ID: " + purchasedPolicy.getPolicyId()));
                    
                    PurchaseResponse response = new PurchaseResponse();
                    response.setCustomerId(customerId);
                    response.setPolicyId(purchasedPolicy.getPolicyId());
                    response.setStartDate(purchasedPolicy.getStartDate());
                    response.setEndDate(purchasedPolicy.getEndDate());
                    response.setStatus(purchasedPolicy.getStatus().toString());
                    response.setPolicyName(policy.getName());
                    response.setPremiumAmount(policy.getPremiumAmount());
                    
                    return response;
                })
                .toList();
    }
    
    public Long getActivePolicyCount(Long customerId) {
        return purchasedPolicyRepository.countActivePoliciesByCustomer(customerId);
    }
    public Long getExpiredPolicyCount(Long customerId) {
        return purchasedPolicyRepository.countExpiredPoliciesByCustomer(customerId);
    }

    public Long getRenewalsDueSoon(Long customerId) {
        LocalDate today = LocalDate.now();
        LocalDate futureDate = today.plusDays(30); // next 30 days
        return purchasedPolicyRepository.countPoliciesExpiringSoon(customerId, today, futureDate);
    }
    
    /**
     * Runs every day at midnight to check expired policies
     */
    @Scheduled(cron = "0 0 0 * * ?")
    //@Scheduled(fixedRate = 60000)
    @Transactional
    public void markExpiredPolicies() {
        LocalDate today = LocalDate.now();

        List<PurchasedPolicy> activePolicies = purchasedPolicyRepository.findByStatus(PolicyStatus.ACTIVE);
        int expiredCount = 0;
        for (PurchasedPolicy policy : activePolicies) {
            if (policy.getEndDate().isBefore(today)) {
                policy.setStatus(PolicyStatus.EXPIRED);
                purchasedPolicyRepository.save(policy);
                expiredCount++;
            }
        }
        log.info("Scheduler ran at {} → Expired {} policies", today, expiredCount);
    }
    
    // Runs every 1 minute, but only deletes pending payments older than 15 mins
    @Scheduled(fixedRate = 60000)  // you can keep this as 1 min check
    @Transactional
    public void cleanupPendingPayments() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(15);
        
        // Fetch only policies with PENDING_PAYMENT and createdAt < cutoffTime
        List<PurchasedPolicy> expiredPendingPolicies =
                purchasedPolicyRepository.findByStatusAndCreatedAtBefore(
                        PolicyStatus.PENDING_PAYMENT, cutoffTime);

        if (!expiredPendingPolicies.isEmpty()) {
            List<Long> policyIds = expiredPendingPolicies.stream()
                    .map(PurchasedPolicy::getId)
                    .toList();

            // Bulk delete payments first
            paymentRepository.deleteByPurchasedPolicyIdIn(policyIds);

            // Then bulk delete purchased policies
            purchasedPolicyRepository.deleteByIdIn(policyIds);

            log.info("Deleted {} pending payment policies older than 15 minutes", policyIds.size());
        }
    }

}
