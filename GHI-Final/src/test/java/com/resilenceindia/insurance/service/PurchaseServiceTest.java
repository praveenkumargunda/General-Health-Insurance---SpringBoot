package com.resilenceindia.insurance.service;

import com.resilenceindia.insurance.dto.PaymentConfirmationRequest;
import com.resilenceindia.insurance.dto.PurchaseRequest;
import com.resilenceindia.insurance.dto.PurchaseResponse;
import com.resilenceindia.insurance.entity.*;
import com.resilenceindia.insurance.exception.PaymentException;
import com.resilenceindia.insurance.exception.PurchaseException;
import com.resilenceindia.insurance.repository.CustomerRepository;
import com.resilenceindia.insurance.repository.PaymentRepository;
import com.resilenceindia.insurance.repository.PolicyRepository;
import com.resilenceindia.insurance.repository.PurchasedPolicyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PurchaseServiceTest {

    @InjectMocks
    private PurchaseService purchaseService;

    @Mock
    private PurchasedPolicyRepository purchasedPolicyRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PolicyRepository policyRepository;

    @Mock
    private CustomerRepository customerRepository;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    private Customer createCustomer() {
        Customer c = new Customer();
        c.setId(1L);
        c.setFirstName("John");
        return c;
    }

    private Policy createPolicy() {
        Policy p = new Policy();
        p.setId(10L);
        p.setName("Health Insurance");
        p.setPremiumAmount(BigDecimal.valueOf(5000));
        p.setTerm(Policy.Term.YEARLY);
        return p;
    }

    // ---------- initiatePurchase ----------

    @Test
    void initiatePurchase_success_createsNewPolicyAndPayment() {
        PurchaseRequest req = new PurchaseRequest();
        req.setCustomerId(1L);
        req.setPolicyId(10L);

        Customer customer = createCustomer();
        Policy policy = createPolicy();

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(policyRepository.findById(10L)).thenReturn(Optional.of(policy));
        when(purchasedPolicyRepository.findByCustomerIdAndPolicyId(1L, 10L)).thenReturn(Optional.empty());

        PurchasedPolicy savedPolicy = new PurchasedPolicy();
        savedPolicy.setId(100L);
        when(purchasedPolicyRepository.save(any(PurchasedPolicy.class))).thenReturn(savedPolicy);

        Payment savedPayment = new Payment();
        savedPayment.setTransactionId("TXN_123456");
        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);

        PurchaseResponse response = purchaseService.initiatePurchase(req);

        assertNotNull(response);
        assertEquals("John", response.getFirstName());
        assertEquals("Health Insurance", response.getPolicyName());
        assertEquals("TXN_123456", response.getTransactionId());
    }

    @Test
    void initiatePurchase_customerNotFound_throwsException() {
        PurchaseRequest req = new PurchaseRequest();
        req.setCustomerId(99L);
        req.setPolicyId(10L);

        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(PurchaseException.class, () -> purchaseService.initiatePurchase(req));
    }

    @Test
    void initiatePurchase_policyNotFound_throwsException() {
        PurchaseRequest req = new PurchaseRequest();
        req.setCustomerId(1L);
        req.setPolicyId(99L);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(createCustomer()));
        when(policyRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(PurchaseException.class, () -> purchaseService.initiatePurchase(req));
    }

    @Test
    void initiatePurchase_existingActivePolicy_throwsException() {
        Customer customer = createCustomer();
        Policy policy = createPolicy();

        PurchasedPolicy existing = new PurchasedPolicy();
        existing.setStatus(PolicyStatus.ACTIVE);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(policyRepository.findById(10L)).thenReturn(Optional.of(policy));
        when(purchasedPolicyRepository.findByCustomerIdAndPolicyId(1L, 10L))
                .thenReturn(Optional.of(existing));

        PurchaseRequest req = new PurchaseRequest();
        req.setCustomerId(1L);
        req.setPolicyId(10L);

        assertThrows(PurchaseException.class, () -> purchaseService.initiatePurchase(req));
    }

    @Test
    void initiatePurchase_existingExpiredPolicy_throwsException() {
        Customer customer = createCustomer();
        Policy policy = createPolicy();

        PurchasedPolicy existing = new PurchasedPolicy();
        existing.setStatus(PolicyStatus.EXPIRED);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(policyRepository.findById(10L)).thenReturn(Optional.of(policy));
        when(purchasedPolicyRepository.findByCustomerIdAndPolicyId(1L, 10L))
                .thenReturn(Optional.of(existing));

        PurchaseRequest req = new PurchaseRequest();
        req.setCustomerId(1L);
        req.setPolicyId(10L);

        assertThrows(PurchaseException.class, () -> purchaseService.initiatePurchase(req));
    }

    @Test
    void initiatePurchase_existingPendingPayment_reusesTransaction() {
        Customer customer = createCustomer();
        Policy policy = createPolicy();

        PurchasedPolicy existing = new PurchasedPolicy();
        existing.setId(200L);
        existing.setStatus(PolicyStatus.PENDING_PAYMENT);

        Payment payment = new Payment();
        payment.setTransactionId("TXN_EXISTING");

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(policyRepository.findById(10L)).thenReturn(Optional.of(policy));
        when(purchasedPolicyRepository.findByCustomerIdAndPolicyId(1L, 10L))
                .thenReturn(Optional.of(existing));
        when(paymentRepository.findByPurchasedPolicyId(200L)).thenReturn(Optional.of(payment));

        PurchaseRequest req = new PurchaseRequest();
        req.setCustomerId(1L);
        req.setPolicyId(10L);

        PurchaseResponse response = purchaseService.initiatePurchase(req);

        assertEquals("TXN_EXISTING", response.getTransactionId());
    }

    // ---------- confirmPurchase ----------

    @Test
    void confirmPurchase_completed_updatesPolicyAndPayment() {
        Payment payment = new Payment();
        payment.setPurchasedPolicyId(100L);
        payment.setTransactionId("TXN_1");

        PurchasedPolicy policy = new PurchasedPolicy();
        policy.setId(100L);
        policy.setStatus(PolicyStatus.PENDING_PAYMENT);
        policy.setEndDate(LocalDate.now().plusYears(1));

        when(paymentRepository.findByTransactionId("TXN_1")).thenReturn(Optional.of(payment));
        when(purchasedPolicyRepository.findById(100L)).thenReturn(Optional.of(policy));

        PaymentConfirmationRequest req = new PaymentConfirmationRequest();
        req.setTransactionId("TXN_1");
        req.setPaymentStatus("COMPLETED");
        req.setPaymentMode("CARD");

        String result = purchaseService.confirmPurchase(req);

        assertEquals("Payment confirmation processed successfully", result);
        verify(paymentRepository, times(1)).save(any(Payment.class));
        verify(purchasedPolicyRepository, times(1)).save(any(PurchasedPolicy.class));
    }

    @Test
    void confirmPurchase_failed_deletesPolicyAndPayment() {
        Payment payment = new Payment();
        payment.setPurchasedPolicyId(100L);
        payment.setTransactionId("TXN_2");

        PurchasedPolicy policy = new PurchasedPolicy();
        policy.setId(100L);

        when(paymentRepository.findByTransactionId("TXN_2")).thenReturn(Optional.of(payment));
        when(purchasedPolicyRepository.findById(100L)).thenReturn(Optional.of(policy));

        PaymentConfirmationRequest req = new PaymentConfirmationRequest();
        req.setTransactionId("TXN_2");
        req.setPaymentStatus("FAILED");
        req.setPaymentMode("UPI");

        String result = purchaseService.confirmPurchase(req);

        assertTrue(result.contains("Payment failed"));
        verify(purchasedPolicyRepository, times(1)).delete(policy);
        verify(paymentRepository, times(1)).delete(payment);
    }

    @Test
    void confirmPurchase_paymentNotFound_throwsException() {
        when(paymentRepository.findByTransactionId("INVALID")).thenReturn(Optional.empty());

        PaymentConfirmationRequest req = new PaymentConfirmationRequest();
        req.setTransactionId("INVALID");

        assertThrows(PaymentException.class, () -> purchaseService.confirmPurchase(req));
    }

    // ---------- getActivePolicies / getExpiredPolicies ----------

    @Test
    void getActivePolicies_returnsMappedResponses() {
        PurchasedPolicy purchasedPolicy = new PurchasedPolicy();
        purchasedPolicy.setPolicyId(10L);
        purchasedPolicy.setStatus(PolicyStatus.ACTIVE);
        purchasedPolicy.setStartDate(LocalDate.now());
        purchasedPolicy.setEndDate(LocalDate.now().plusYears(1));

        when(purchasedPolicyRepository.findByCustomerId(1L)).thenReturn(List.of(purchasedPolicy));
        when(policyRepository.findById(10L)).thenReturn(Optional.of(createPolicy()));

        List<PurchaseResponse> responses = purchaseService.getActivePolicies(1L);

        assertEquals(1, responses.size());
        assertEquals("Health Insurance", responses.get(0).getPolicyName());
    }

    @Test
    void getExpiredPolicies_returnsMappedResponses() {
        PurchasedPolicy purchasedPolicy = new PurchasedPolicy();
        purchasedPolicy.setPolicyId(10L);
        purchasedPolicy.setStatus(PolicyStatus.EXPIRED);
        purchasedPolicy.setStartDate(LocalDate.now().minusYears(1));
        purchasedPolicy.setEndDate(LocalDate.now());

        when(purchasedPolicyRepository.findByCustomerId(1L)).thenReturn(List.of(purchasedPolicy));
        when(policyRepository.findById(10L)).thenReturn(Optional.of(createPolicy()));

        List<PurchaseResponse> responses = purchaseService.getExpiredPolicies(1L);

        assertEquals(1, responses.size());
        assertEquals("EXPIRED", responses.get(0).getStatus());
    }

    // ---------- counts ----------

    @Test
    void getActivePolicyCount_returnsValue() {
        when(purchasedPolicyRepository.countActivePoliciesByCustomer(1L)).thenReturn(5L);
        assertEquals(5L, purchaseService.getActivePolicyCount(1L));
    }

    @Test
    void getExpiredPolicyCount_returnsValue() {
        when(purchasedPolicyRepository.countExpiredPoliciesByCustomer(1L)).thenReturn(3L);
        assertEquals(3L, purchaseService.getExpiredPolicyCount(1L));
    }

    @Test
    void getRenewalsDueSoon_returnsValue() {
        when(purchasedPolicyRepository.countPoliciesExpiringSoon(anyLong(), any(), any())).thenReturn(2L);
        assertEquals(2L, purchaseService.getRenewalsDueSoon(1L));
    }

    // ---------- schedulers ----------

    @Test
    void markExpiredPolicies_updatesExpiredOnes() {
        PurchasedPolicy policy = new PurchasedPolicy();
        policy.setId(1L);
        policy.setStatus(PolicyStatus.ACTIVE);
        policy.setEndDate(LocalDate.now().minusDays(1));

        when(purchasedPolicyRepository.findByStatus(PolicyStatus.ACTIVE)).thenReturn(List.of(policy));

        purchaseService.markExpiredPolicies();

        verify(purchasedPolicyRepository, times(1)).save(policy);
        assertEquals(PolicyStatus.EXPIRED, policy.getStatus());
    }

    @Test
    void cleanupPendingPayments_deletesExpiredOnes() {
        PurchasedPolicy pendingPolicy = new PurchasedPolicy();
        pendingPolicy.setId(50L);
        pendingPolicy.setStatus(PolicyStatus.PENDING_PAYMENT);
        pendingPolicy.setCreatedAt(LocalDateTime.now().minusMinutes(20));

        when(purchasedPolicyRepository.findByStatusAndCreatedAtBefore(eq(PolicyStatus.PENDING_PAYMENT), any()))
                .thenReturn(List.of(pendingPolicy));

        purchaseService.cleanupPendingPayments();

        verify(paymentRepository).deleteByPurchasedPolicyIdIn(List.of(50L));
        verify(purchasedPolicyRepository).deleteByIdIn(List.of(50L));
    }
}
