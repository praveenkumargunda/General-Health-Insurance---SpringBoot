package com.resilenceindia.insurance.service;

import com.resilenceindia.insurance.dto.RenewalPaymentRequest;
import com.resilenceindia.insurance.dto.RenewalResponse;
import com.resilenceindia.insurance.entity.*;
import com.resilenceindia.insurance.exception.GlobalExceptionHandler.BusinessException;
import com.resilenceindia.insurance.repository.CustomerRepository;
import com.resilenceindia.insurance.repository.PurchasedPolicyRepository;
import com.resilenceindia.insurance.repository.RenewalPaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RenewalServiceImplTest {

    @Mock
    private RenewalPaymentRepository paymentRepository;

    @Mock
    private PurchasedPolicyRepository policyRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private RenewalServiceImpl renewalService;

    private Customer customer;
    private PurchasedPolicy policy;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        customer = new Customer();
        customer.setId(1L);

        policy = new PurchasedPolicy();
        policy.setId(100L);
        policy.setPolicyId(200L);
        policy.setCustomerId(1L);
    }

    // ---------- getRenewalsByCustomerId ----------

    //Testing for Invalid customer Id like 0 or negative
    @Test
    void testGetRenewals_InvalidCustomerId() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> renewalService.getRenewalsByCustomerId(-9L));

        assertEquals("INVALID_CUSTOMER_ID", ex.getErrorCode());
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }
    
//Testing to see when we enter in history page with a customer Id that doesn't exist
    @Test
    void testGetRenewals_CustomerNotFound() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> renewalService.getRenewalsByCustomerId(1L));

        assertEquals("CUSTOMER_NOT_FOUND", ex.getErrorCode());
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }
    
//Testing when a customer is there, but has no policies
    @Test
    void testGetRenewals_NoPoliciesFound() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer)); //Mocks the repository: customer exists.
        when(policyRepository.findByCustomerId(1L)).thenReturn(Collections.emptyList()); //Mocks the repository: no policies found for customer.

        BusinessException ex = assertThrows(BusinessException.class,
                () -> renewalService.getRenewalsByCustomerId(1L));

        assertEquals("POLICY_NOT_FOUND", ex.getErrorCode());
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }
    
//Test verifies it correctly fetches renewals for a valid customer with policies and payments or not
    @Test
    void testGetRenewals_Success() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        policy.setStatus(PolicyStatus.EXPIRED);
        when(policyRepository.findByCustomerId(1L)).thenReturn(List.of(policy));

        Payment payment = new Payment();
        payment.setId(999L);
        payment.setAmount(BigDecimal.valueOf(5000));
        payment.setPaymentStatus(PaymentStatus.COMPLETED);
        payment.setPaymentMode("CARD");
        payment.setPaymentDate(LocalDateTime.now());
        payment.setNextPremiumDate(LocalDate.now().plusYears(1));

        when(paymentRepository.findTopByPurchasedPolicyIdOrderByPaymentDateDesc(100L))
                .thenReturn(Optional.of(payment));

        List<RenewalResponse> responses = renewalService.getRenewalsByCustomerId(1L);

        assertEquals(1, responses.size());
        RenewalResponse response = responses.get(0);
        assertEquals(999L, response.getPaymentId());
        assertEquals(BigDecimal.valueOf(5000), response.getAmount());
        assertEquals("Policy Expired. Renewal required.", response.getMessage());
    }

    // ---------- payPremium ----------
    
//Testing whether we entered amount correct or not
    @Test
    void testPayPremium_InvalidAmount() {
        RenewalPaymentRequest request = new RenewalPaymentRequest(200L, BigDecimal.valueOf(-9000), "CARD", "yearly", 1L);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> renewalService.payPremium(request));

        assertEquals("INVALID_PAYMENT_AMOUNT", ex.getErrorCode());
    }
    
//Tests whether the policy id we entered is correct or not.. like it sees whether did we enter any negative number or zero
    @Test
    void testPayPremium_InvalidPolicyId() {
        RenewalPaymentRequest request = new RenewalPaymentRequest(0L, BigDecimal.valueOf(5000), "CARD", "yearly", 1L);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> renewalService.payPremium(request));

        assertEquals("INVALID_POLICY_ID", ex.getErrorCode());
    }
    
  //Tests whether the customer id we entered is correct or not.. like it sees whether did we enter any negative number or zero
    @Test
    void testPayPremium_InvalidCustomerId() {
        RenewalPaymentRequest request = new RenewalPaymentRequest(200L, BigDecimal.valueOf(5000), "CARD", "yearly", 0L);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> renewalService.payPremium(request));

        assertEquals("INVALID_CUSTOMER_ID", ex.getErrorCode());
    }
    
//Testing when , like what happens when we enter a policy id that is not there with the customer
    @Test
    void testPayPremium_PolicyNotFound() {
        RenewalPaymentRequest request = new RenewalPaymentRequest(200L, BigDecimal.valueOf(5000), "CARD", "yearly", 1L);

        when(policyRepository.findByCustomerIdAndPolicyId(1L, 200L)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> renewalService.payPremium(request));

        assertEquals("POLICY_NOT_FOUND", ex.getErrorCode());
    }

//Tests what happens when we try to pay premium for a policy that is active
    @Test
    void testPayPremium_PolicyAlreadyActive() {
        RenewalPaymentRequest request = new RenewalPaymentRequest(200L, BigDecimal.valueOf(5000), "CARD", "yearly", 1L);

        policy.setStatus(PolicyStatus.ACTIVE);
        when(policyRepository.findByCustomerIdAndPolicyId(1L, 200L)).thenReturn(Optional.of(policy));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> renewalService.payPremium(request));

        assertEquals("POLICY_ALREADY_ACTIVE", ex.getErrorCode());
    }
    
  //Tests what happens when we try to pay premium for a policy that is pending
    @Test
    void testPayPremium_PolicyPendingPayment() {
        RenewalPaymentRequest request = new RenewalPaymentRequest(200L, BigDecimal.valueOf(5000), "CARD", "yearly", 1L);

        policy.setStatus(PolicyStatus.PENDING_PAYMENT);
        when(policyRepository.findByCustomerIdAndPolicyId(1L, 200L)).thenReturn(Optional.of(policy));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> renewalService.payPremium(request));

        assertEquals("POLICY_PENDING_PAYMENT", ex.getErrorCode());
    }
    
  //Tests what happens when we try to pay premium for a policy that is cancelled
    @Test
    void testPayPremium_PolicyCancelled() {
        RenewalPaymentRequest request = new RenewalPaymentRequest(200L, BigDecimal.valueOf(5000), "CARD", "yearly", 1L);

        policy.setStatus(PolicyStatus.CANCELLED);
        when(policyRepository.findByCustomerIdAndPolicyId(1L, 200L)).thenReturn(Optional.of(policy));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> renewalService.payPremium(request));

        assertEquals("POLICY_PENDING_PAYMENT", ex.getErrorCode());
    }
    
//This is main 
   //checks when we give all the details correct and sees whether we are able to pay the premium or not
    @Test
    void testPayPremium_Success_PolicyExpired() {
        RenewalPaymentRequest request = new RenewalPaymentRequest(200L, BigDecimal.valueOf(5000), "CARD", "yearly", 1L);

        policy.setStatus(PolicyStatus.EXPIRED);
        when(policyRepository.findByCustomerIdAndPolicyId(1L, 200L)).thenReturn(Optional.of(policy));

        Payment savedPayment = new Payment();
        savedPayment.setId(999L);
        savedPayment.setAmount(request.getAmount());
        savedPayment.setPaymentStatus(PaymentStatus.COMPLETED);
        savedPayment.setPaymentMode("CARD");
        savedPayment.setPaymentDate(LocalDateTime.now());
        savedPayment.setNextPremiumDate(LocalDate.now().plusYears(1));

        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);

        RenewalResponse response = renewalService.payPremium(request);

        assertEquals(999L, response.getPaymentId());
        assertEquals("Payment successful. Policy has been renewed.", response.getMessage());
        assertEquals(PolicyStatus.ACTIVE, response.getPolicyStatus());
    }
}
