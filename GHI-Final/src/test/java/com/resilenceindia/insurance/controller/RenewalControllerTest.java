package com.resilenceindia.insurance.controller;

import com.resilenceindia.insurance.dto.RenewalPaymentRequest;
import com.resilenceindia.insurance.dto.RenewalResponse;
import com.resilenceindia.insurance.entity.Customer;
import com.resilenceindia.insurance.service.AgentService;
import com.resilenceindia.insurance.service.RenewalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpSession;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RenewalControllerTest {

    @Mock
    private RenewalService renewalService;

    @Mock
    private AgentService agentService;

    @Mock
    private HttpSession session;

    @InjectMocks
    private RenewalController renewalController;

    private Customer customer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        customer = new Customer();
        customer.setId(1L);
    }
//Testing payment history when no user is logged-in
    @Test
    void testGetRenewalsByCustomer_Unauthorized_NoSession() {
        when(session.getAttribute("agent")).thenReturn(null);
        when(session.getAttribute("agentId")).thenReturn(null);
        when(session.getAttribute("customer")).thenReturn(null);
        when(session.getAttribute("loggedInCustomer")).thenReturn(null);
        when(session.getAttribute("customerId")).thenReturn(null);

        ResponseEntity<?> response = renewalController.getRenewalsByCustomer(1L, session);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("UNAUTHORIZED", ((Map<?, ?>) response.getBody()).get("error"));
    }
    
  //Testing payment history when agent is logged-in
    @Test
    void testGetRenewalsByCustomer_AgentLoggedIn() {
        when(session.getAttribute("agent")).thenReturn(new Object());
        List<RenewalResponse> mockResponses = Collections.singletonList(new RenewalResponse());
        when(renewalService.getRenewalsByCustomerId(1L)).thenReturn(mockResponses);

        ResponseEntity<?> response = renewalController.getRenewalsByCustomer(1L, session);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponses, response.getBody());
    }

  //Customer logs in and requests their own renewals history
    @Test
    void testGetRenewalsByCustomer_CustomerLoggedIn_AccessOwnData() {
        when(session.getAttribute("agent")).thenReturn(null);
        when(session.getAttribute("agentId")).thenReturn(null);
        when(session.getAttribute("customer")).thenReturn(customer);

        List<RenewalResponse> mockResponses = Collections.singletonList(new RenewalResponse());
        when(renewalService.getRenewalsByCustomerId(1L)).thenReturn(mockResponses);

        ResponseEntity<?> response = renewalController.getRenewalsByCustomer(1L, session);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponses, response.getBody());
    }
    
    //Customer logs in and requests other renewals history
    @Test
    void testGetRenewalsByCustomer_CustomerLoggedIn_TryingOthersData() {
        when(session.getAttribute("agent")).thenReturn(null);
        when(session.getAttribute("agentId")).thenReturn(null);
        when(session.getAttribute("customer")).thenReturn(customer);

        ResponseEntity<?> response = renewalController.getRenewalsByCustomer(2L, session);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("ACCESS_DENIED", ((Map<?, ?>) response.getBody()).get("error"));
    }
    
//customer trying to pay their expired premium
    @Test
    void testPayPremium_Success() {
        RenewalPaymentRequest request = new RenewalPaymentRequest(10L, BigDecimal.valueOf(5000), "CARD", "yearly", 1L);
        when(session.getAttribute("customer")).thenReturn(customer);

        RenewalResponse mockResponse = new RenewalResponse();
        mockResponse.setMessage("Payment successful");
        when(renewalService.payPremium(request)).thenReturn(mockResponse);

        ResponseEntity<?> response = renewalController.payPremium(request, session);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Payment successful", ((RenewalResponse) response.getBody()).getMessage());
    }
    
  //customer trying to pay other's expired premium
    @Test
    void testPayPremium_AccessDenied() {
        RenewalPaymentRequest request = new RenewalPaymentRequest(10L, BigDecimal.valueOf(5000), "CARD", "yearly", 2L);
        when(session.getAttribute("customer")).thenReturn(customer);

        ResponseEntity<?> response = renewalController.payPremium(request, session);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("ACCESS_DENIED", ((Map<?, ?>) response.getBody()).get("error"));
    }
}
