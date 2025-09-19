package com.resilenceindia.insurance.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.resilenceindia.insurance.dto.RenewalPaymentRequest;
import com.resilenceindia.insurance.dto.RenewalResponse;
import com.resilenceindia.insurance.entity.Customer;
import com.resilenceindia.insurance.service.AgentService;
import com.resilenceindia.insurance.service.RenewalService;

import jakarta.servlet.http.HttpSession;
import java.util.*;

@RestController
@RequestMapping("/renewals")
public class RenewalController {

    private final RenewalService renewalService;
    
    @Autowired
    private final AgentService agentService;

    public RenewalController(RenewalService renewalService, AgentService agentService) {
        this.renewalService = renewalService;
        this.agentService = agentService;
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<?> getRenewalsByCustomer(@PathVariable Long customerId, HttpSession session) {
    	 
        // Check for agent session first
        Object agentObj = session.getAttribute("agent");
        Long agentId = (Long) session.getAttribute("agentId");
        
        boolean isAgentLoggedIn = false;
        if (agentObj != null || agentId != null) {
            isAgentLoggedIn = true;
        }
        
        // Get logged-in customer from session
        Customer loggedInCustomer = (Customer) session.getAttribute("customer");
        if (loggedInCustomer == null) {
            loggedInCustomer = (Customer) session.getAttribute("loggedInCustomer");
        }
        
        Long loggedInCustomerId = null;
        if (loggedInCustomer != null) {
            loggedInCustomerId = loggedInCustomer.getId();
        } else {
            loggedInCustomerId = (Long) session.getAttribute("customerId");
        }
        
        // Check if anyone is logged in
        if (!isAgentLoggedIn && loggedInCustomerId == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "UNAUTHORIZED");
            errorResponse.put("message", "Please log in first");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
        
        // If agent is logged in, they can view any customer's renewal history
        if (isAgentLoggedIn) {
            List<RenewalResponse> responses = renewalService.getRenewalsByCustomerId(customerId);
            return ResponseEntity.ok(responses);
        }
        
        // If customer is logged in, they can only view their own renewal history
        if (loggedInCustomerId != null) {
            if (!loggedInCustomerId.equals(customerId)) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "ACCESS_DENIED");
                errorResponse.put("message", "You can only view your own payment details. This is not your Customer ID.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
            }
            
            List<RenewalResponse> responses = renewalService.getRenewalsByCustomerId(customerId);
            return ResponseEntity.ok(responses);
        }
        
        // This should not reach here, but just in case
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "UNAUTHORIZED");
        errorResponse.put("message", "Please log in first");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }


    @PostMapping("/pay")
    public ResponseEntity<?> payPremium(@RequestBody RenewalPaymentRequest request, HttpSession session) {
    	 if (request.getCustomerId() == null || request.getCustomerId() <= 0) {
             Map<String, Object> errorResponse = new HashMap<>();
             errorResponse.put("error", "INVALID_CUSTOMER_ID");
             errorResponse.put("message", "Customer ID must be a positive number");
             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
         }
        // Only customers can make payments - check for customer session only
        Customer loggedInCustomer = (Customer) session.getAttribute("customer");
        if (loggedInCustomer == null) {
            loggedInCustomer = (Customer) session.getAttribute("loggedInCustomer");
        }
        
        Long loggedInCustomerId = null;
        if (loggedInCustomer != null) {
            loggedInCustomerId = loggedInCustomer.getId();
        } else {
            loggedInCustomerId = (Long) session.getAttribute("customerId");
        }
        
        
        // Check if customer is making payment for their own policy
        if (!loggedInCustomerId.equals(request.getCustomerId())) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "ACCESS_DENIED");
            errorResponse.put("message", "You can only make renewals for your own policies. This is not your Customer ID.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
        }
        
        // Process the payment
        RenewalResponse response = renewalService.payPremium(request);
        return ResponseEntity.ok(response);
    }
}