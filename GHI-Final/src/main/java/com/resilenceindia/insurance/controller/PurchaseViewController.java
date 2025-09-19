package com.resilenceindia.insurance.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.resilenceindia.insurance.dto.PaymentConfirmationRequest;
import com.resilenceindia.insurance.dto.PurchaseRequest;
import com.resilenceindia.insurance.dto.PurchaseResponse;
import com.resilenceindia.insurance.entity.Agent;
import com.resilenceindia.insurance.entity.Customer;
import com.resilenceindia.insurance.entity.Policy;
import com.resilenceindia.insurance.service.AgentService;
import com.resilenceindia.insurance.service.PolicyService;
import com.resilenceindia.insurance.service.PurchaseService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/agent/purchase")
public class PurchaseViewController {

    @Autowired
    private PurchaseService purchaseService;
    
    @Autowired
    private AgentService agentService; // inject this to get assigned customers

    @Autowired
    private PolicyService policyService; // create this if you don't have it yet


    // Show purchase form
    @GetMapping("/policies")
    public String showPurchaseForm(HttpSession session, Model model) {
        Long agentId = (Long) session.getAttribute("agentId");
        if (agentId == null) {
            return "redirect:/agent/login?error=Please login first";
        }

        List<Customer> assignedCustomers = agentService.getAssignedCustomers(agentId);
        List<Policy> availablePolicies = policyService.getAll();

        model.addAttribute("assignedCustomers", assignedCustomers != null ? assignedCustomers : List.of());
        model.addAttribute("availablePolicies", availablePolicies != null ? availablePolicies : List.of());
        model.addAttribute("purchaseRequest", new PurchaseRequest());

        return "purchase-form";
    }

    // Handle purchase initiation
    @PostMapping("/payment")
    public String initiatePurchase(@ModelAttribute PurchaseRequest purchaseRequest, Model model, HttpSession session) {
        try {
//        		Long agentId = (Long) session.getAttribute("agentId");
//        		if (agentId == null) {
//        			return "redirect:/agent/login";
//            }
        		PurchaseResponse response = purchaseService.initiatePurchase(purchaseRequest);
            model.addAttribute("purchaseResponse", response);
            return "payment-page";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            
            //added extra 
            
         // 🔑 Re-populate data so dropdowns don’t go blank
            Agent agent = (Agent) session.getAttribute("agent");
            if (agent != null) {
                model.addAttribute("assignedCustomers", agentService.getAssignedCustomers(agent.getId()));
            }
            model.addAttribute("availablePolicies", policyService.getAll());
            // Preserve entered values
            model.addAttribute("purchaseRequest", purchaseRequest);

            return "purchase-form"; // stay on purchase page
        }
    }

    // Handle payment confirmation
    @PostMapping("/confirm")
    public String confirmPayment(@ModelAttribute PaymentConfirmationRequest confirmationRequest, Model model) {
    	try{
    		String message = purchaseService.confirmPurchase(confirmationRequest);
        model.addAttribute("confirmationMessage", message);
        return "confirmation-page";
    	}catch(RuntimeException e){
    		return "payment-denied";
    	}
    }
    
    @GetMapping("/payment")
    public String paymentFallback() {
        return "payment-denied";
    }

    @GetMapping("/confirm")
    public String confirmFallback() {
        return "payment-denied";
    }
}
