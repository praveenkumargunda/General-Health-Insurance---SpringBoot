package com.resilenceindia.insurance.service;

import jakarta.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.resilenceindia.insurance.entity.Customer;
import com.resilenceindia.insurance.repository.CustomerRepository;
import com.resilenceindia.insurance.dto.RenewalPaymentRequest;
import com.resilenceindia.insurance.dto.RenewalResponse;
import com.resilenceindia.insurance.entity.Payment;
import com.resilenceindia.insurance.entity.PaymentStatus;
import com.resilenceindia.insurance.entity.PolicyStatus;
import com.resilenceindia.insurance.entity.PurchasedPolicy;
import com.resilenceindia.insurance.exception.GlobalExceptionHandler.BusinessException;
import com.resilenceindia.insurance.repository.PurchasedPolicyRepository;
import com.resilenceindia.insurance.repository.RenewalPaymentRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class RenewalServiceImpl implements RenewalService {

    private final RenewalPaymentRepository paymentRepository;
    private final PurchasedPolicyRepository policyRepository;
    private final CustomerRepository customerRepository;

    public RenewalServiceImpl(RenewalPaymentRepository paymentRepository, PurchasedPolicyRepository policyRepository, CustomerRepository customerRepository) {
        this.paymentRepository = paymentRepository;
        this.policyRepository = policyRepository;
        this.customerRepository = customerRepository; 
    }

    private LocalDate calculateNextPremiumDate(String option) {
        LocalDate now = LocalDate.now();
        if ("half-yearly".equalsIgnoreCase(option)) {
            return now.plusMonths(6);
        } else if ("yearly".equalsIgnoreCase(option)) {
            return now.plusYears(1);
        }
        return now;
    }

    @Override
    public List<RenewalResponse> getRenewalsByCustomerId(Long customerId) {
    //  VALIDATION FOR POSITIVE CUSTOMER ID
        if (customerId <= 0) {
            throw new BusinessException(
                "INVALID_CUSTOMER_ID",
                "Customer ID must be a positive number",
                HttpStatus.BAD_REQUEST
            );
        }
    	
    	//  FOR CUSTOMER VALIDATION FIRST
        Optional<Customer> customerOpt = customerRepository.findById(customerId);
        if (customerOpt.isEmpty()) {
            throw new BusinessException(
                "CUSTOMER_NOT_FOUND",
                "Customer not found with ID: " + customerId,
                HttpStatus.NOT_FOUND
            );
        }
        List<PurchasedPolicy> policies = policyRepository.findByCustomerId(customerId);
        if (policies.isEmpty()) {
            throw new BusinessException(
                "POLICY_NOT_FOUND",
                "No policies found for customer ID: " + customerId,
                HttpStatus.NOT_FOUND
            );
        }

        List<RenewalResponse> responses = new ArrayList<>();

        for (PurchasedPolicy policy : policies) {
            Optional<Payment> latestPaymentOpt = paymentRepository
                    .findTopByPurchasedPolicyIdOrderByPaymentDateDesc(policy.getId());

            RenewalResponse response = new RenewalResponse();

            if (latestPaymentOpt.isPresent()) {  //when i insert expired purchased_policy through db, it wont have a payment that's why we dont get
                Payment payment = latestPaymentOpt.get(); //details regarding the payment ones
                response.setPaymentId(payment.getId());
                response.setAmount(payment.getAmount());
                response.setPaymentStatus(payment.getPaymentStatus());
                response.setPaymentMode(payment.getPaymentMode());
                response.setPaymentDate(payment.getPaymentDate());
                response.setNextPremiumDate(payment.getNextPremiumDate());
            }

            response.setPolicyStatus(policy.getStatus()); // these details will be there when i insert purchased_policy one, that's why 
            response.setPolicyId(policy.getPolicyId());  //i get these details in the front end 
            response.setCustomerId(policy.getCustomerId());

            switch (policy.getStatus()) {
	            case ACTIVE -> response.setMessage("Policy already Active (Renewed).");
	            case PENDING_PAYMENT -> response.setMessage("Policy is Pending. Awaiting payment.");
	            case EXPIRED -> response.setMessage("Policy Expired. Renewal required.");
	            case CANCELLED -> response.setMessage("Policy has been Cancelled.");
	            default -> response.setMessage("Policy status: " + policy.getStatus());
            }

            responses.add(response);
        }

        return responses;
    }
    private String generateTransactionId() {
        return "TXN_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }
    

    @Override
    public RenewalResponse payPremium(RenewalPaymentRequest request) {
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0.0) {
            throw new BusinessException(
                "INVALID_PAYMENT_AMOUNT",
                "Payment amount must be greater than zero",
                HttpStatus.BAD_REQUEST
            );
        }
        
     //  Add validation for max amount (1,00,000)
//        if (request.getAmount().compareTo(new BigDecimal("100000")) > 0) {
//            throw new BusinessException(
//                "PAYMENT_LIMIT_EXCEEDED",
//                "You are exceeding the limit of amount",
//                HttpStatus.BAD_REQUEST
//            );
//        }
        if (request.getpolicyId() == null || request.getpolicyId() <= 0) {
            throw new BusinessException(
                "INVALID_POLICY_ID",
                "Policy ID must be a positive number",
                HttpStatus.BAD_REQUEST
            );
        }

        if (request.getCustomerId() == null || request.getCustomerId() <= 0) {
            throw new BusinessException(
                "INVALID_CUSTOMER_ID",
                "Customer ID must be a positive number",
                HttpStatus.BAD_REQUEST
            );
        }

        PurchasedPolicy policy = policyRepository.findByCustomerIdAndPolicyId(
                request.getCustomerId(), request.getpolicyId())
                .orElseThrow(() -> new BusinessException(
                    "POLICY_NOT_FOUND",
                    "No policy found for customer ID: " + request.getCustomerId() +
                    " and policy ID: " + request.getpolicyId(),
                    HttpStatus.NOT_FOUND
                ));

//        if (!policy.getCustomerId().equals(request.getCustomerId())) {
//            throw new BusinessException(
//                "CUSTOMER_POLICY_MISMATCH",
//                "Policy ID " + request.getpolicyId() + " does not belong to customer ID " + request.getCustomerId(),
//                HttpStatus.BAD_REQUEST
//            );
//        }
        
        if (policy.getStatus() == PolicyStatus.ACTIVE) {
            throw new BusinessException("POLICY_ALREADY_ACTIVE",
                "Policy is already Active and cannot be renewed again",
                HttpStatus.CONFLICT);
        }
        if (policy.getStatus() == PolicyStatus.PENDING_PAYMENT) {
            throw new BusinessException("POLICY_PENDING_PAYMENT",
                "Policy is pending for payment. Cannot be renwed",
                HttpStatus.CONFLICT);
        }
        if (policy.getStatus() == PolicyStatus.CANCELLED) {
            throw new BusinessException("POLICY_PENDING_PAYMENT",
                "Policy is cancelled. Cannot be renwed",
                HttpStatus.CONFLICT);
        }
        RenewalResponse response = new RenewalResponse();
        
        if(policy.getStatus() == PolicyStatus.EXPIRED) {  //here when i do renewal payment, these down will get done in payment table
        		Payment payment = new Payment();
            payment.setPurchasedPolicyId(policy.getId());
            payment.setAmount(request.getAmount());
            payment.setPaymentStatus(PaymentStatus.COMPLETED);
            payment.setPaymentMode(request.getPaymentMode());
            payment.setPaymentDate(LocalDateTime.now());
            payment.setTransactionId(generateTransactionId());
            payment.setNextPremiumDate(calculateNextPremiumDate(request.getOption()));

            Payment saved = paymentRepository.save(payment);

            policy.setStatus(PolicyStatus.ACTIVE);
            policy.setEndDate(saved.getNextPremiumDate());
            
            policyRepository.save(policy);

    //By setting all those details before, now lets fill the RenewalResponse DTO..        
            response.setPaymentId(saved.getId());
            response.setAmount(saved.getAmount());
            response.setPaymentStatus(saved.getPaymentStatus());
            response.setPaymentMode(saved.getPaymentMode());
            response.setPaymentDate(saved.getPaymentDate());
            response.setNextPremiumDate(saved.getNextPremiumDate());
            response.setPolicyStatus(policy.getStatus());
            response.setPolicyId(policy.getPolicyId());
            response.setCustomerId(policy.getCustomerId());
            response.setMessage("Payment successful. Policy has been renewed.");
        }

        return response;
    }
}
