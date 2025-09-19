package com.resilenceindia.insurance.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.resilenceindia.insurance.dto.CustomerLoginRequest;
import com.resilenceindia.insurance.dto.CustomerRegistrationRequest;
import com.resilenceindia.insurance.entity.Customer;
import com.resilenceindia.insurance.service.CustomerService;
import com.resilenceindia.insurance.service.PurchaseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
@AutoConfigureMockMvc(addFilters = false)  // disable Spring Security in tests
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    @MockBean
    private PurchaseService purchaseService;

   /* @Test
    void testRegisterCustomerApi_Success() throws Exception {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setEmail("john@example.com");
        customer.setPhoneNumber("1234567890");
        customer.setDateOfBirth("1990-01-01");

        when(customerService.registerCustomer(any())).thenReturn(customer);

        String requestJson = """
            {
  "firstName": "Dhinesh",
  "lastName": "Ka",
  "email": "dhinesh@example.com",
  "password": "password123",
  "phoneNumber": "9876543210",
  "dateOfBirth": "2000-05-15",
  "gender": "Male",
  "address": "123 Main Street",
  "city": "Chennai",
  "state": "Tamil Nadu",
  "pinCode": "600001"
}


            """;

        mockMvc.perform(post("/customer/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true));
    }*/


    @Test
    void testLoginCustomerApi_Success() throws Exception {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setEmail("john@example.com");
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setDateOfBirth("1990-01-01");

        when(customerService.authenticateCustomer(any())).thenReturn(customer);

        mockMvc.perform(post("/customer/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"john@example.com\",\"password\":\"pass123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testGetCustomerProfileApi_Success() throws Exception {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setEmail("john@example.com");
        customer.setDateOfBirth("1990-01-01");

        mockMvc.perform(get("/customer/api/profile")
                .sessionAttr("customer", customer))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testGetCustomerProfileApi_Unauthorized() throws Exception {
        mockMvc.perform(get("/customer/api/profile"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }
}

