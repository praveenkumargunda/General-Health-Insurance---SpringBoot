package com.resilenceindia.insurance.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.resilenceindia.insurance.entity.Agent;
import com.resilenceindia.insurance.entity.Customer;
import com.resilenceindia.insurance.service.AgentService;
import com.resilenceindia.insurance.service.CustomerService;

import jakarta.servlet.http.HttpSession;

@Controller
public class RenewalViewController {

	@Autowired
	AgentService agentService;
	
	@Autowired
	CustomerService customerService;
	
    @GetMapping("/renewal")
    public String renewalPage() {
        return "renewal";
    }

    @GetMapping("/gateway")
    public String gatewayPage() {
        return "gateway";
    }
    
    @GetMapping("/history")
    public String historyPage() {
        return "history";
    }


    
   /* @GetMapping("/history")
    public String showHistory(HttpSession session, Model model) {
        Agent agent = (Agent) session.getAttribute("agent");
        Customer customer = (Customer) session.getAttribute("customer");

        if (agent == null && customer == null) {
            // Nobody logged in
            return "redirect:/customer/login?error=Please login first";
        }

        if (agent != null) {
            // Refresh agent
            agent = agentService.findAgentById(agent.getId()).orElse(agent);
            session.setAttribute("agent", agent);

            // Agent’s assigned customers
            List<Customer> assignedCustomers = agentService.getAssignedCustomers(agent.getId());
            model.addAttribute("role", "agent");
            model.addAttribute("agent", agent);
            model.addAttribute("assignedCustomers", assignedCustomers);
        }
            else {
            // Customer login
            customer = customerService.findCustomerById(customer.getId()).orElse(customer);
            session.setAttribute("customer", customer);

            // Just their own record
            model.addAttribute("role", "customer");
            model.addAttribute("assignedCustomers", List.of(customer));
        }

        return "history"; // same Thymeleaf page
    
    } */
}


