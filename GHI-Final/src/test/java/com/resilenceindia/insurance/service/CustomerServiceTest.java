package com.resilenceindia.insurance.service;


import com.resilenceindia.insurance.dto.CustomerLoginRequest;
import com.resilenceindia.insurance.dto.CustomerRegistrationRequest;
import com.resilenceindia.insurance.entity.Agent;
import com.resilenceindia.insurance.entity.Customer;
import com.resilenceindia.insurance.exception.CustomerAlreadyExistsException;
import com.resilenceindia.insurance.exception.InvalidCredentialsException;
import com.resilenceindia.insurance.repository.AgentRepository;
import com.resilenceindia.insurance.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerServiceTest {

    @InjectMocks
    private CustomerService customerService;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private AgentRepository agentRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private CustomerRegistrationRequest buildRegistrationRequest() {
        CustomerRegistrationRequest req = new CustomerRegistrationRequest();
        req.setFirstName("John");
        req.setLastName("Doe");
        req.setEmail("john@example.com");
        req.setPhoneNumber("9876543210");
        req.setPassword("password");
        req.setDateOfBirth("1990-01-01");
        req.setGender("Male");
        req.setAddress("123 Street");
        req.setCity("City");
        req.setState("State");
        req.setPinCode("123456");
        return req;
    }

    @Test
    void registerCustomer_success() {
        CustomerRegistrationRequest request = buildRegistrationRequest();

        when(customerRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(customerRepository.existsByPhoneNumber(request.getPhoneNumber())).thenReturn(false);

        Agent agent = new Agent();
        agent.setId(1L);
        agent.setFirstName("Agent");
        agent.setCustomers(new ArrayList<>());

        when(agentRepository.findAll()).thenReturn(List.of(agent));
        when(customerRepository.save(any(Customer.class))).thenAnswer(i -> i.getArguments()[0]);

        Customer saved = customerService.registerCustomer(request);

        assertNotNull(saved);
        assertEquals("John", saved.getFirstName());
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void registerCustomer_emailExists_throwsException() {
        CustomerRegistrationRequest request = buildRegistrationRequest();
        when(customerRepository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThrows(CustomerAlreadyExistsException.class, () -> customerService.registerCustomer(request));
    }

    @Test
    void authenticateCustomer_success() {
        CustomerLoginRequest login = new CustomerLoginRequest();
        login.setEmail("john@example.com");
        login.setPassword("password");

        Customer customer = new Customer();
        customer.setEmail("john@example.com");
        customer.setPassword(new BCryptPasswordEncoder().encode("password"));

        when(customerRepository.findByEmailAndIsActiveTrue(login.getEmail()))
                .thenReturn(Optional.of(customer));

        Customer authenticated = customerService.authenticateCustomer(login);

        assertNotNull(authenticated);
        assertEquals("john@example.com", authenticated.getEmail());
    }

    @Test
    void authenticateCustomer_invalidCredentials_throwsException() {
        CustomerLoginRequest login = new CustomerLoginRequest();
        login.setEmail("john@example.com");
        login.setPassword("wrong");

        Customer customer = new Customer();
        customer.setEmail("john@example.com");
        customer.setPassword(new BCryptPasswordEncoder().encode("password"));

        when(customerRepository.findByEmailAndIsActiveTrue(login.getEmail()))
                .thenReturn(Optional.of(customer));

        assertThrows(InvalidCredentialsException.class, () -> customerService.authenticateCustomer(login));
    }

    @Test
    void deactivateCustomer_success() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setIsActive(true);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        customerService.deactivateCustomer(1L);

        assertFalse(customer.getIsActive());
        verify(customerRepository, times(1)).save(customer);
    }
}