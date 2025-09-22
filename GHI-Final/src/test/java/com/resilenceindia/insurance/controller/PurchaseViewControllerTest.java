package com.resilenceindia.insurance.controller;

import com.resilenceindia.insurance.dto.PaymentConfirmationRequest;
import com.resilenceindia.insurance.dto.PurchaseRequest;
import com.resilenceindia.insurance.dto.PurchaseResponse;
import com.resilenceindia.insurance.entity.Agent;
import com.resilenceindia.insurance.entity.Customer;
import com.resilenceindia.insurance.entity.Policy;
import com.resilenceindia.insurance.service.AgentService;
import com.resilenceindia.insurance.service.PolicyService;
import com.resilenceindia.insurance.service.PurchaseService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PurchaseViewController.class)
@AutoConfigureMockMvc(addFilters = false)
class PurchaseViewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PurchaseService purchaseService;

    @MockBean
    private AgentService agentService;

    @MockBean
    private PolicyService policyService;

    @Test
    void showPurchaseForm_redirectsToLoginIfNoAgentInSession() throws Exception {
        mockMvc.perform(get("/agent/purchase/policies"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/agent/login?error=Please login first"));
    }

    @Test
    void showPurchaseForm_returnsPurchaseFormPage() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("agentId", 1L);

        List<Customer> customers = List.of(new Customer());
        List<Policy> policies = List.of(new Policy());

        when(agentService.getAssignedCustomers(1L)).thenReturn(customers);
        when(policyService.getAll()).thenReturn(policies);

        mockMvc.perform(get("/agent/purchase/policies").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("purchase-form"))
                .andExpect(model().attributeExists("assignedCustomers"))
                .andExpect(model().attributeExists("availablePolicies"))
                .andExpect(model().attributeExists("purchaseRequest"));
    }

    @Test
    void initiatePurchase_success_returnsPaymentPage() throws Exception {
        PurchaseResponse response = new PurchaseResponse();
        when(purchaseService.initiatePurchase(any(PurchaseRequest.class))).thenReturn(response);

        mockMvc.perform(post("/agent/purchase/payment")
                        .param("customerId", "1")
                        .param("policyId", "2"))
                .andExpect(status().isOk())
                .andExpect(view().name("payment-page"))
                .andExpect(model().attributeExists("purchaseResponse"));
    }

    @Test
    void initiatePurchase_failure_returnsPurchaseFormWithError() throws Exception {
        when(purchaseService.initiatePurchase(any(PurchaseRequest.class)))
                .thenThrow(new RuntimeException("Policy expired"));

        MockHttpSession session = new MockHttpSession();
        Agent agent = new Agent();
        agent.setId(1L);
        session.setAttribute("agent", agent);

        when(agentService.getAssignedCustomers(1L)).thenReturn(List.of(new Customer()));
        when(policyService.getAll()).thenReturn(List.of(new Policy()));

        mockMvc.perform(post("/agent/purchase/payment")
                        .param("customerId", "1")
                        .param("policyId", "2")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("purchase-form"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attributeExists("assignedCustomers"))
                .andExpect(model().attributeExists("availablePolicies"));
    }

    @Test
    void confirmPayment_success_returnsConfirmationPage() throws Exception {
        when(purchaseService.confirmPurchase(any(PaymentConfirmationRequest.class)))
                .thenReturn("Payment Successful!");

        mockMvc.perform(post("/agent/purchase/confirm")
                        .param("transactionId", "TXN123")
                        .param("status", "SUCCESS"))
                .andExpect(status().isOk())
                .andExpect(view().name("confirmation-page"))
                .andExpect(model().attribute("confirmationMessage", "Payment Successful!"));
    }

    @Test
    void confirmPayment_failure_returnsPaymentDenied() throws Exception {
        when(purchaseService.confirmPurchase(any(PaymentConfirmationRequest.class)))
                .thenThrow(new RuntimeException("Payment Failed"));

        mockMvc.perform(post("/agent/purchase/confirm")
                        .param("transactionId", "TXN123")
                        .param("status", "FAILED"))
                .andExpect(status().isOk())
                .andExpect(view().name("payment-denied"));
    }

    @Test
    void paymentFallback_returnsPaymentDenied() throws Exception {
        mockMvc.perform(get("/agent/purchase/payment"))
                .andExpect(status().isOk())
                .andExpect(view().name("payment-denied"));
    }

    @Test
    void confirmFallback_returnsPaymentDenied() throws Exception {
        mockMvc.perform(get("/agent/purchase/confirm"))
                .andExpect(status().isOk())
                .andExpect(view().name("payment-denied"));
    }
}
