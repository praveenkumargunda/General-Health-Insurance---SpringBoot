//package com.resilenceindia.insurance.service;
//
//import com.resilenceindia.insurance.dto.PaymentConfirmationRequest;
//import com.resilenceindia.insurance.dto.PurchaseRequest;
//import com.resilenceindia.insurance.dto.PurchaseResponse;
//import com.resilenceindia.insurance.entity.*;
//import com.resilenceindia.insurance.repository.*;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.*;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class PurchaseServiceTest {
//
//    @InjectMocks
//    private PurchaseService purchaseService;
//
//    @Mock
//    private PurchasedPolicyRepository purchasedPolicyRepository;
//
//    @Mock
//    private PaymentRepository paymentRepository;
//
//    @Mock
//    private PolicyRepository policyRepository;
//
//    @Mock
//    private CustomerRepository customerRepository;
//
//    @Mock
//    private AgentService agentService;
//
//    @BeforeEach
//    void setup() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void testInitiatePurchase_success() {
//        Customer customer = new Customer();
//        customer.setId(1L);
//        customer.setFirstName("John");
//
//        Policy policy = new Policy();
//        policy.setId(1L);
//        policy.setName("Health");
//        policy.setPremiumAmount(BigDecimal.valueOf(1000));
//        policy.setTerm(Policy.Term.YEARLY);
//
//        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
//        when(policyRepository.findById(1L)).thenReturn(Optional.of(policy));
//        when(purchasedPolicyRepository.findByCustomerIdAndPolicyId(1L, 1L))
//                .thenReturn(Optional.empty());
//
//        PurchasedPolicy savedPolicy = new PurchasedPolicy();
//        savedPolicy.setId(10L);
//        savedPolicy.setCustomerId(1L);
//        savedPolicy.setPolicyId(1L);
//        savedPolicy.setStartDate(LocalDate.now());
//        savedPolicy.setEndDate(LocalDate.now().plusYears(1));
//        savedPolicy.setStatus(PolicyStatus.PENDING_PAYMENT);
//
//        when(purchasedPolicyRepository.save(any())).thenReturn(savedPolicy);
//
//        Payment savedPayment = new Payment();
//        savedPayment.setTransactionId("TXN_123456");
//
//        when(paymentRepository.save(any())).thenReturn(savedPayment);
//
//        PurchaseRequest request = new PurchaseRequest();
//        request.setCustomerId(1L);
//        request.setPolicyId(1L);
//
//        PurchaseResponse response = purchaseService.initiatePurchase(request);
//
//        assertNotNull(response);
//        assertEquals("Health", response.getPolicyName());
//        assertEquals("Purchase initiated successfully. Please complete payment.", response.getMessage());
//    }
//
//    @Test
//    void testConfirmPurchase_paymentNotFound() {
//        when(paymentRepository.findByTransactionId("INVALID")).thenReturn(Optional.empty());
//
//        // create DTO via no-arg + setters
//        PaymentConfirmationRequest req = new PaymentConfirmationRequest();
//        req.setTransactionId("INVALID");
//        req.setPaymentStatus("COMPLETED");
//        req.setPaymentMode("CARD");
//
//        RuntimeException ex = assertThrows(RuntimeException.class,
//            () -> purchaseService.confirmPurchase(req)
//        );
//
//        assertTrue(ex.getMessage().contains("Payment not found"));
//    }
//}
