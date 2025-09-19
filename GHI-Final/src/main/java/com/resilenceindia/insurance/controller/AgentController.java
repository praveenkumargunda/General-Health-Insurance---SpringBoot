package com.resilenceindia.insurance.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.resilenceindia.insurance.dto.AgentLoginRequest;
import com.resilenceindia.insurance.dto.AgentRegistrationRequest;
import com.resilenceindia.insurance.dto.AgentResponse;
import com.resilenceindia.insurance.dto.ApiResponse;
import com.resilenceindia.insurance.entity.Agent;
import com.resilenceindia.insurance.entity.Customer;
import com.resilenceindia.insurance.service.AgentService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/agent")
public class AgentController {
    
    @Autowired
    private AgentService agentService;
    
    // Web Pages
    
    @GetMapping("/register")
    public String showRegistrationPage(Model model) {
        model.addAttribute("agentRegistration", new AgentRegistrationRequest());
        return "agent/Registration";
    }
    
    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("agentLogin", new AgentLoginRequest());
        return "agent/login";
    }
    
    @PostMapping("/login")
    public String processLogin(@ModelAttribute AgentLoginRequest loginRequest,
                               HttpSession session) {
      
            Agent agent = agentService.authenticateAgent(loginRequest); // ✅ call your method
            session.removeAttribute("agent");
            session.setAttribute("agent", agent);
            return "redirect:/agent/dashboard";
    }

    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session, Model model) {
        Agent agent = (Agent) session.getAttribute("agent");
        if (agent == null) {
            return "redirect:/agent/login?error=Please login first";
        }

        // Refresh agent from DB in case something was updated
        agent = agentService.findAgentById(agent.getId()).orElse(agent);
        session.setAttribute("agent", agent);
        
        // Get assigned customers
        List<Customer> assignedCustomers = agentService.getAssignedCustomers(agent.getId());
        
        model.addAttribute("agent", agent);
        model.addAttribute("assignedCustomers", assignedCustomers);
        model.addAttribute("totalCustomers", assignedCustomers.size());
        
        return "agent/Dashboard";
    }
    
    // REST API Endpoints
    
    @PostMapping("/api/register")
    @ResponseBody
    public ResponseEntity<ApiResponse<AgentResponse>> registerAgent(
            @Valid @RequestBody AgentRegistrationRequest request) {

        Agent agent = agentService.registerAgent(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Agent registered successfully", new AgentResponse(agent)));
    }
    
    @PostMapping("/api/login")
    @ResponseBody
    public ResponseEntity<ApiResponse<AgentResponse>> loginAgent(
            @Valid @RequestBody AgentLoginRequest request,
            HttpSession session) {
    	Agent agent = agentService.authenticateAgent(request);

        session.setAttribute("agent", agent);

        return ResponseEntity.ok(ApiResponse.success("Login successful", new AgentResponse(agent)));  
            
    }
    
    @PostMapping("/api/logout")
    @ResponseBody
    public ResponseEntity<ApiResponse<Void>> logoutAgent(HttpSession session) {
        session.removeAttribute("agent");
        session.removeAttribute("agentId");
        session.invalidate();

        return ResponseEntity.ok(ApiResponse.success("Logout successful"));
    }
    
    @GetMapping("/api/profile")
    @ResponseBody
   
    public ResponseEntity<ApiResponse<AgentResponse>> getAgentProfile(HttpSession session) {
        Agent agent = (Agent) session.getAttribute("agent");
        if (agent == null) {
            return new ResponseEntity<>(
                    ApiResponse.error("Please login first"),
                    HttpStatus.UNAUTHORIZED
            );
        }
        return ResponseEntity.ok(ApiResponse.success("Profile retrieved successfully", new AgentResponse(agent)));
    }

    
    @GetMapping("/api/assigned-customers")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAssignedCustomers(HttpSession session) {
        Agent agent = (Agent) session.getAttribute("agent");
        if (agent == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Please login first"));
        }

        List<Customer> assignedCustomers = agentService.getAssignedCustomers(agent.getId());

        // put extra info like totalCustomers in a map
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("customers", assignedCustomers);
        responseData.put("totalCustomers", assignedCustomers.size());

        return ResponseEntity.ok(ApiResponse.success("Assigned customers retrieved successfully", responseData));
    }

    @PostMapping("/web/register")
    public String registerAgentWeb(@Valid @ModelAttribute("agentRegistration") AgentRegistrationRequest request,
                                  BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "agent/Registration";
        }
        
        try {
            agentService.registerAgent(request);
            model.addAttribute("successMessage", "Registration successful! Please login.");
            model.addAttribute("agentLogin", new AgentLoginRequest());
            return "agent/login";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "agent/Registration";
        }
    }
    
    @PostMapping("/web/login")
    public String loginAgentWeb(@Valid @ModelAttribute("agentLogin") AgentLoginRequest request,
                               BindingResult bindingResult, HttpSession session, Model model) {
        if (bindingResult.hasErrors()) {
            return "agent/login";
        }
        
        try {
            Agent agent = agentService.authenticateAgent(request);
            session.setAttribute("agent", agent);
            session.setAttribute("agentId", agent.getId());
            return "redirect:/agent/dashboard";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "agent/login";
        }
    }
    
    @GetMapping("/web/login")
    public String showWebLoginPage(@RequestParam(value = "error", required = false) String error,
                                   @RequestParam(value = "logout", required = false) String logout,
                                   Model model) {
        model.addAttribute("agentLogin", new AgentLoginRequest());

        if (error != null) {
            model.addAttribute("errorMessage", "Invalid email or password. Please try again.");
        }
        if (logout != null) {
            model.addAttribute("successMessage", "You have been logged out successfully.");
        }

        return "agent/login"; // your Thymeleaf login page
    }

    
    @GetMapping("/web/logout")
    public String logoutAgentWeb(HttpSession session) {
        session.removeAttribute("agent");
        session.removeAttribute("agentId");
        session.invalidate();
        return "redirect:/agent/login?message=Logged out successfully";
    }
    
    @PostMapping("/web/update-profile")
    public String updateProfile(@ModelAttribute("agent") Agent form,
                                HttpSession session) {

        Agent sessionAgent = (Agent) session.getAttribute("agent");
        if (sessionAgent == null) {
            return "redirect:/agent/login?error=Please login first";
        }

        // Fetch the fresh agent from DB (keeps required fields intact)
        Agent dbAgent = agentService.findAgentById(sessionAgent.getId())
                .orElseThrow(() -> new RuntimeException("Agent not found"));

        // Update only allowed fields
        dbAgent.setFirstName(form.getFirstName());
        dbAgent.setLastName(form.getLastName());
        dbAgent.setPhoneNumber(form.getPhoneNumber());
        dbAgent.setAddress(form.getAddress());
        dbAgent.setCity(form.getCity());
        dbAgent.setState(form.getState());
        dbAgent.setPinCode(form.getPinCode());

        // Save safely
        Agent updated = agentService.saveAgent(dbAgent);

        // Refresh session
        session.setAttribute("agent", updated);

        return "redirect:/agent/dashboard?success=Profile+updated+successfully!#profile";

    }


    @GetMapping("/api/check-email")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> checkEmailAvailability(@RequestParam String email) {
        Map<String, Object> response = new HashMap<>();
        boolean exists = agentService.emailExists(email);
        
        response.put("exists", exists);
        response.put("available", !exists);
        response.put("message", exists ? "Email already registered" : "Email available");
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @GetMapping("/api/check-phone")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> checkPhoneAvailability(@RequestParam String phoneNumber) {
        Map<String, Object> response = new HashMap<>();
        boolean exists = agentService.phoneNumberExists(phoneNumber);
        
        response.put("exists", exists);
        response.put("available", !exists);
        response.put("message", exists ? "Phone number already registered" : "Phone number available");
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @GetMapping("/api/check-document")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> checkDocumentAvailability(@RequestParam String documentNumber) {
        Map<String, Object> response = new HashMap<>();
        boolean exists = agentService.documentNumberExists(documentNumber);
        
        response.put("exists", exists);
        response.put("available", !exists);
        response.put("message", exists ? "Document number already registered" : "Document number available");
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}