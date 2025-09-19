//package com.resilenceindia.insurance.controller;
//
////import com.resilenceindia.insurance.config.TestSecurityConfig;
//import com.resilenceindia.insurance.dto.PaymentConfirmationRequest;
//import com.resilenceindia.insurance.dto.PurchaseRequest;
//import com.resilenceindia.insurance.dto.PurchaseResponse;
//import com.resilenceindia.insurance.service.PurchaseService;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.context.annotation.Import;
//import org.springframework.test.web.servlet.MockMvc;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(controllers = PurchaseViewController.class,
//excludeAutoConfiguration = {
//    org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
//    org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration.class
//}
//)
//class PurchaseViewControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private PurchaseService purchaseService;
//
//    @Test
//    void testShowPurchaseForm() throws Exception {
//        mockMvc.perform(get("/agent/purchase/policies"))
//                .andExpect(status().isOk())
//                .andExpect(view().name("purchase-form"))
//                .andExpect(model().attributeExists("purchaseRequest"));
//    }
//
//    @Test
//    void testInitiatePurchase_success() throws Exception {
//        PurchaseResponse response = new PurchaseResponse();
//        response.setMessage("Purchase initiated successfully");
//
//        when(purchaseService.initiatePurchase(any(PurchaseRequest.class)))
//                .thenReturn(response);
//
//        mockMvc.perform(post("/agent/purchase/payment"))
//                .andExpect(status().isOk())
//                .andExpect(view().name("payment-page"))
//                .andExpect(model().attributeExists("purchaseResponse"));
//    }
//
//    @Test
//    void testInitiatePurchase_failure() throws Exception {
//        when(purchaseService.initiatePurchase(any(PurchaseRequest.class)))
//                .thenThrow(new RuntimeException("Customer not found"));
//
//        mockMvc.perform(post("/agent/purchase/payment"))
//                .andExpect(status().isOk())
//                .andExpect(view().name("purchase-form"))
//                .andExpect(model().attributeExists("errorMessage"))
//                .andExpect(model().attribute("errorMessage", "Customer not found"));
//    }
//
//    @Test
//    void testConfirmPayment_success() throws Exception {
//        when(purchaseService.confirmPurchase(any(PaymentConfirmationRequest.class)))
//                .thenReturn("Payment confirmation processed successfully");
//
//        mockMvc.perform(post("/agent/purchase/confirm"))
//                .andExpect(status().isOk())
//                .andExpect(view().name("confirmation-page"))
//                .andExpect(model().attributeExists("confirmationMessage"))
//                .andExpect(model().attribute("confirmationMessage", "Payment confirmation processed successfully"));
//    }
//}
