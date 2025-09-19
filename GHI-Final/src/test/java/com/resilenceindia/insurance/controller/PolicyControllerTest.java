

package com.resilenceindia.insurance.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.math.BigDecimal;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.resilenceindia.insurance.entity.Policy;
import com.resilenceindia.insurance.entity.Policy.Term;
import com.resilenceindia.insurance.service.PolicyService;

public class PolicyControllerTest {

    @Mock
    private PolicyService policyService;

    @InjectMocks
    private PolicyViewController policyViewController;

    private MockMvc mockMvc;
    private Policy policy;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(policyViewController).build();

        policy = new Policy();
        policy.setId(1L);
        policy.setName("Test Policy");
        policy.setCoverageAmount(BigDecimal.valueOf(100000));
        policy.setPremiumAmount(BigDecimal.valueOf(5000));
        policy.setTerm(Term.YEARLY);
        policy.setDescription("Test Desc");
    }

    @Test
    void testRedirectRoot() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/customer/login"));
    }

    @Test
    void testHomeAgent() throws Exception {
        when(policyService.getAll()).thenReturn(Collections.singletonList(policy));

        mockMvc.perform(get("/policies/agent"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("policy"))
                .andExpect(model().attributeExists("policies"));
    }

    @Test
    void testHomeCustomer() throws Exception {
        when(policyService.getAll()).thenReturn(Collections.singletonList(policy));

        mockMvc.perform(get("/policies/customer"))
                .andExpect(status().isOk())
                .andExpect(view().name("catalogue"))
                .andExpect(model().attributeExists("policy"))
                .andExpect(model().attributeExists("policies"));
    }

    @Test
    void testCreatePolicy_Success() throws Exception {
        when(policyService.create(any(Policy.class))).thenReturn(policy);

        mockMvc.perform(post("/policies")
                .param("name", "New Policy")
                .param("coverageAmount", "100000")
                .param("premiumAmount", "5000")
                .param("term", "YEARLY")
                .param("description", "desc"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/policies/agent"));
    }

    @Test
    void testCreatePolicy_ValidationFail() throws Exception {
        mockMvc.perform(post("/policies")
                .param("name", "Invalid Policy")
                .param("coverageAmount", "1000")
                .param("premiumAmount", "5000")
                .param("term", "YEARLY")
                .param("description", "desc"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("errorMessage"));
    }

    @Test
    void testDeletePolicy() throws Exception {
        doNothing().when(policyService).delete(1L);

        mockMvc.perform(get("/policies/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/policies/agent"));
    }

    @Test
    void testEditPolicy() throws Exception {
        when(policyService.get(1L)).thenReturn(policy);
        when(policyService.getAll()).thenReturn(Collections.singletonList(policy));

        mockMvc.perform(get("/policies/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("policy"))
                .andExpect(model().attribute("editMode", true));
    }

    @Test
    void testUpdatePolicy_Success() throws Exception {
        when(policyService.update(eq(1L), any(Policy.class))).thenReturn(policy);

        mockMvc.perform(post("/policies/update/1")
                .param("name", "Updated Policy")
                .param("coverageAmount", "100000")
                .param("premiumAmount", "5000")
                .param("term", "YEARLY")
                .param("description", "desc"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/policies/agent"));
    }

    @Test
    void testUpdatePolicy_ValidationFail() throws Exception {
        mockMvc.perform(post("/policies/update/1")
                .param("name", "Invalid Update")
                .param("coverageAmount", "1000")
                .param("premiumAmount", "5000")
                .param("term", "YEARLY")
                .param("description", "desc"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("editMode", true));
    }
}