package com.resilenceindia.insurance.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.resilenceindia.insurance.entity.Policy;
import com.resilenceindia.insurance.service.PolicyService;

@Controller
public class PolicyViewController {

    private final PolicyService policyService;

    public PolicyViewController(PolicyService policyService) {
        this.policyService = policyService;
    }

    private void prepareModel(Model model) {
        model.addAttribute("policy", new Policy());
        model.addAttribute("policies", policyService.getAll());
    }

    @GetMapping("/")
    public String redirectRoot() {
        return "redirect:/customer/login";
    }

    @GetMapping("/policies/agent")
    public String homeAgent(Model model) {
        prepareModel(model);
        return "index";
    }
    
    @GetMapping("/policies/customer")
    public String homeCustomer(Model model) {
        prepareModel(model);
        return "catalogue";
    }

    @PostMapping("/policies")
    public String create(@ModelAttribute Policy policy, Model model) {
        if (policy.getPremiumAmount().compareTo(policy.getCoverageAmount()) > 0) {
            model.addAttribute("errorMessage", "Premium cannot be greater than Coverage Amount.");
            model.addAttribute("policy", policy);
            model.addAttribute("policies", policyService.getAll());
            return "index"; // return same page with error
        }

        policyService.create(policy);
        return "redirect:/policies/agent"; // success
    }

    @GetMapping("/policies/delete/{id}")
    public String delete(@PathVariable Long id) {
        policyService.delete(id);
        return "redirect:/policies/agent"; // consistent redirect
    }
    
    @GetMapping("/policies/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        Policy policy = policyService.get(id);
        model.addAttribute("policy", policy);          // pre-filled values
        model.addAttribute("policies", policyService.getAll());
        model.addAttribute("editMode", true);          // flag for form mode
        return "index";  // reuse same page
    }

    @PostMapping("/policies/update/{id}")
    public String update(@PathVariable Long id, @ModelAttribute Policy policy, Model model) {
        if (policy.getPremiumAmount().compareTo(policy.getCoverageAmount()) > 0) {
            model.addAttribute("errorMessage", "Premium cannot be greater than Coverage Amount.");
            model.addAttribute("policy", policy);
            model.addAttribute("policies", policyService.getAll());
            model.addAttribute("editMode", true);
            return "index";
        }

        policyService.update(id, policy);
        return "redirect:/policies/agent";
    }

}