package com.resilenceindia.insurance.controller;



import java.time.LocalDateTime;
import java.util.HashMap;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.resilenceindia.insurance.dto.CustomerLoginRequest;
import com.resilenceindia.insurance.dto.CustomerRegistrationRequest;
import com.resilenceindia.insurance.dto.CustomerResponse;
import com.resilenceindia.insurance.dto.ForgotPasswordRequest;
import com.resilenceindia.insurance.entity.Customer;
import com.resilenceindia.insurance.service.CustomerService;
import com.resilenceindia.insurance.service.PurchaseService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/customer")
public class CustomerController {
    
    @Autowired
    private CustomerService customerService;
    
    @Autowired
    private PurchaseService purchaseService;
    
    // Web Pages
    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
      //  model.addAttribute("customerLoginRequest", new CustomerLoginRequest());
        return "customer/forgot-password"; // this looks for forgot-password.html in templates/
    }
    
    @GetMapping("/register")
    public String showRegistrationPage(Model model) {
        model.addAttribute("customerRegistration", new CustomerRegistrationRequest());
        return "customer/register";
    }
    
    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("customerLogin", new CustomerLoginRequest());
        return "customer/login";
    }
    
    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session, Model model) {
        Customer customer = (Customer) session.getAttribute("customer");
        if (customer == null) {
            return "redirect:/customer/login?error=Please login first";
        }
        Long activePolicyCount = purchaseService.getActivePolicyCount(customer.getId());
        Long expiredPolicyCount = purchaseService.getExpiredPolicyCount(customer.getId());
        Long renewalsDueSoon = purchaseService.getRenewalsDueSoon(customer.getId());

        model.addAttribute("activePolicyCount", activePolicyCount);
        model.addAttribute("expiredPolicyCount", expiredPolicyCount);
        model.addAttribute("renewalsDueSoon", renewalsDueSoon);
        
        model.addAttribute("customer", customer);
        return "customer/dashboard";
    }
    
    @GetMapping("/mypolicies")
    public String viewPolicies(@RequestParam(value = "filter", required = false) String filter, HttpSession session, Model model) {
        Customer customer = (Customer) session.getAttribute("customer");
        if (customer == null) {
            return "redirect:/customer/login";
        }
        
        model.addAttribute("activePolicies", purchaseService.getActivePolicies(customer.getId()));
        model.addAttribute("expiredPolicies", purchaseService.getExpiredPolicies(customer.getId()));
        
        // Default = active, unless ?filter=expired is present
        model.addAttribute("selectedTab", "expired".equalsIgnoreCase(filter) ? "expired" : "active");
        
        return "customer/customer-policies"; 
    }
    
    // REST API Endpoints
    
    @PostMapping("/api/register")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> registerCustomer(@Valid @RequestBody CustomerRegistrationRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Customer customer = customerService.registerCustomer(request);
            CustomerResponse customerResponse = new CustomerResponse(customer);
            
            response.put("timestamp", LocalDateTime.now());
            response.put("status", HttpStatus.CREATED.value());
            response.put("message", "Customer registered successfully");
            response.put("data", customerResponse);
            response.put("success", true);
            
            return new ResponseEntity<>(response, HttpStatus.CREATED);
            
        } catch (Exception e) {
            response.put("timestamp", LocalDateTime.now());
            response.put("status", HttpStatus.BAD_REQUEST.value());
            response.put("message", e.getMessage());
            response.put("success", false);
            
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
    
    @PostMapping("/api/login")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> loginCustomer(@Valid @RequestBody CustomerLoginRequest request, 
                                                            HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Customer customer = customerService.authenticateCustomer(request);
            CustomerResponse customerResponse = new CustomerResponse(customer);
            
            // Store customer in session
            session.setAttribute("customer", customer);
            session.setAttribute("customerId", customer.getId());
            
            response.put("timestamp", LocalDateTime.now());
            response.put("status", HttpStatus.OK.value());
            response.put("message", "Login successful");
            response.put("data", customerResponse);
            response.put("success", true);
            
            return new ResponseEntity<>(response, HttpStatus.OK);
            
        } catch (Exception e) {
            response.put("timestamp", LocalDateTime.now());
            response.put("status", HttpStatus.UNAUTHORIZED.value());
            response.put("message", e.getMessage());
            response.put("success", false);
            
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }
    
    @PostMapping("/api/logout")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> logoutCustomer(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        session.removeAttribute("customer");
        session.removeAttribute("customerId");
        session.invalidate();
        
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.OK.value());
        response.put("message", "Logout successful");
        response.put("success", true);
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @GetMapping("/api/profile")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getCustomerProfile(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        Customer customer = (Customer) session.getAttribute("customer");
        if (customer == null) {
            response.put("timestamp", LocalDateTime.now());
            response.put("status", HttpStatus.UNAUTHORIZED.value());
            response.put("message", "Please login first");
            response.put("success", false);
            
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
        
        CustomerResponse customerResponse = new CustomerResponse(customer);
        
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.OK.value());
        response.put("message", "Profile retrieved successfully");
        response.put("data", customerResponse);
        response.put("success", true);
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @PostMapping("/web/register")
    public String registerCustomerWeb(@Valid @ModelAttribute("customerRegistration") CustomerRegistrationRequest request,
                                     BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "customer/register";
        }
        
        try {
            customerService.registerCustomer(request);
            model.addAttribute("successMessage", "Registration successful! Please login.");
            model.addAttribute("customerLogin", new CustomerLoginRequest());
            return "customer/login";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "customer/register";
        }
    }
    
    @PostMapping("/web/login")
    public String loginCustomerWeb(@Valid @ModelAttribute("customerLogin") CustomerLoginRequest request,
                                  BindingResult bindingResult, HttpSession session, Model model) {
        if (bindingResult.hasErrors()) {
            return "customer/login";
        }
        
        try {
            Customer customer = customerService.authenticateCustomer(request);
            session.setAttribute("customer", customer);
            session.setAttribute("customerId", customer.getId());
            return "redirect:/customer/dashboard";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "customer/login";
        }
    }
    @PostMapping("/web/forgot-password")
    public String processForgotPassword(@ModelAttribute ForgotPasswordRequest request, Model model) {
        try {
            customerService.resetPassword(request);
            model.addAttribute("successMessage", "Password reset successful. Please log in.");
            model.addAttribute("customerLogin", new CustomerLoginRequest());  // <-- add this
            return "customer/login";  // Return login page with success message
        } catch (RuntimeException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "customer/forgot-password";  // Show forgot-password page with error
        }
    }


    @GetMapping("/web/logout")
    public String logoutCustomerWeb(HttpSession session) {
        session.removeAttribute("customer");
        session.removeAttribute("customerId");
        session.invalidate();
        return "redirect:/customer/login?message=Logged out successfully";
    }
    
    @GetMapping("/api/check-email")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> checkEmailAvailability(@RequestParam String email) {
        Map<String, Object> response = new HashMap<>();
        boolean exists = customerService.emailExists(email);
        
        response.put("exists", exists);
        response.put("available", !exists);
        response.put("message", exists ? "Email already registered" : "Email available");
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @GetMapping("/api/check-phone")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> checkPhoneAvailability(@RequestParam String phoneNumber) {
        Map<String, Object> response = new HashMap<>();
        boolean exists = customerService.phoneNumberExists(phoneNumber);
        
        response.put("exists", exists);
        response.put("available", !exists);
        response.put("message", exists ? "Phone number already registered" : "Phone number available");
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute("customer") Customer form,
                                HttpSession session) {

        Customer sessionCustomer = (Customer) session.getAttribute("customer");
        if (sessionCustomer == null) {
            return "redirect:/customer/login?error=Please login first";
        }

        // Fetch the fresh customer from DB
        Customer dbCustomer = customerService.findCustomerById(sessionCustomer.getId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        // Update only allowed fields
        dbCustomer.setFirstName(form.getFirstName());
        dbCustomer.setLastName(form.getLastName());
        dbCustomer.setPhoneNumber(form.getPhoneNumber());
        dbCustomer.setDateOfBirth(form.getDateOfBirth());
        dbCustomer.setGender(form.getGender());
        dbCustomer.setAddress(form.getAddress());
        dbCustomer.setCity(form.getCity());
        dbCustomer.setState(form.getState());
        dbCustomer.setPinCode(form.getPinCode());

        // Save safely
        Customer updated = customerService.saveCustomer(dbCustomer);

        // Refresh session
        session.setAttribute("customer", updated);

        return "redirect:/customer/dashboard?success=Profile+updated+successfully!#profile";
    }
    
    
}