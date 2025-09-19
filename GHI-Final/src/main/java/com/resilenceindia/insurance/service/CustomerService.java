package com.resilenceindia.insurance.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.resilenceindia.insurance.dto.CustomerLoginRequest;
import com.resilenceindia.insurance.dto.CustomerRegistrationRequest;
import com.resilenceindia.insurance.dto.ForgotPasswordRequest;
import com.resilenceindia.insurance.entity.Agent;
import com.resilenceindia.insurance.entity.Customer;
import com.resilenceindia.insurance.exception.CustomerAlreadyExistsException;
import com.resilenceindia.insurance.exception.InvalidCredentialsException;
import com.resilenceindia.insurance.repository.AgentRepository;
import com.resilenceindia.insurance.repository.CustomerRepository;

@Service
@Transactional
public class CustomerService {
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private AgentRepository agentRepository;
    
    
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    /**
     * Register a new customer
     */
    public Customer registerCustomer(CustomerRegistrationRequest request) {
        // Check if customer already exists
        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new CustomerAlreadyExistsException("Customer with email " + request.getEmail() + " already exists");
        }

        if (customerRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new CustomerAlreadyExistsException("Customer with phone number " + request.getPhoneNumber() + " already exists");
        }

        // Create new customer
        Customer customer = new Customer();
        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setEmail(request.getEmail());
        customer.setPhoneNumber(request.getPhoneNumber());
        customer.setPassword(passwordEncoder.encode(request.getPassword())); // Encrypt password
        customer.setDateOfBirth(request.getDateOfBirth());
        customer.setGender(request.getGender());
        customer.setAddress(request.getAddress());
        customer.setCity(request.getCity());
        customer.setState(request.getState());
        customer.setPinCode(request.getPinCode());
    	customer.setFavouriteColour(request.getFavouriteColour());
        customer.setIsActive(true);

        // --- Agent assignment ---
        Agent assignedAgent;
        var agents = agentRepository.findAll();

        if (agents.isEmpty()) {
            // Create default agent if none exists
            Agent newAgent = new Agent();
            newAgent.setFirstName("Default");
            newAgent.setLastName("Agent");
            newAgent.setEmail("default@agent.com");
            newAgent.setPhoneNumber("9123456789");
            newAgent.setPassword(passwordEncoder.encode("password123"));
            newAgent.setDateOfBirth("1990-01-01");
            newAgent.setAddress("Default Address");
            newAgent.setCity("City");
            newAgent.setState("State");
            newAgent.setPinCode("123456");
            newAgent.setDocumentType("ID");
            newAgent.setDocumentNumber("1234567890");
            assignedAgent = agentRepository.save(newAgent);
        } else {
            // Assign agent with least customers
            assignedAgent = agents.stream()
                    .min((a1, a2) -> a1.getCustomers().size() - a2.getCustomers().size())
                    .get();
        }

        customer.setAgent(assignedAgent);

        return customerRepository.save(customer);
    }

    
    /**
     * Authenticate customer login
     */
    public Customer authenticateCustomer(CustomerLoginRequest request) {
        Optional<Customer> customerOpt = customerRepository.findByEmailAndIsActiveTrue(request.getEmail());
        
        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            if (passwordEncoder.matches(request.getPassword(), customer.getPassword())) {
                return customer;
            }
        }
        
        throw new InvalidCredentialsException("Invalid email or password");
    }
    
    
    
    /**
     * Find customer by ID
     */
    public Optional<Customer> findCustomerById(Long id) {
        return customerRepository.findById(id);
    }
    
    /**
     * Find customer by email
     */
    public Optional<Customer> findCustomerByEmail(String email) {
        return customerRepository.findByEmailAndIsActiveTrue(email);
    }
    
    /**
     * Update customer profile
     */
    public Customer updateCustomer(Long id, CustomerRegistrationRequest request) {
        Optional<Customer> customerOpt = customerRepository.findById(id);
        
        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            customer.setFirstName(request.getFirstName());
            customer.setLastName(request.getLastName());
            customer.setPhoneNumber(request.getPhoneNumber());
            customer.setDateOfBirth(request.getDateOfBirth());
            customer.setGender(request.getGender());
            customer.setAddress(request.getAddress());
            customer.setCity(request.getCity());
            customer.setState(request.getState());
            customer.setPinCode(request.getPinCode());
            
            return customerRepository.save(customer);
        }
        
        throw new RuntimeException("Customer not found");
    }
    
    /**
     * Deactivate customer account
     */
    public void deactivateCustomer(Long id) {
        Optional<Customer> customerOpt = customerRepository.findById(id);
        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            customer.setIsActive(false);
            customerRepository.save(customer);
        }
    }
    
    /**
     * Check if email already exists
     */
    public boolean emailExists(String email) {
        return customerRepository.existsByEmail(email);
    }
    
    /**
     * Check if phone number already exists
     */
    public boolean phoneNumberExists(String phoneNumber) {
        return customerRepository.existsByPhoneNumber(phoneNumber);
    }
    public void updateCustomer(Customer customer) {
        // Save will update if ID is present
        customerRepository.save(customer);
    }
    public Customer saveCustomer(Customer customer) {
        return customerRepository.save(customer);
    }
    
    public void resetPassword(ForgotPasswordRequest request) {
        Customer customer = customerRepository.findByEmailAndIsActiveTrue(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email not found"));

        if (!customer.getFavouriteColour().equalsIgnoreCase(request.getFavouriteColour())) {
            throw new RuntimeException("Favourite colour does not match");
        }

        // update password
        customer.setPassword(passwordEncoder.encode(request.getNewPassword()));
        customerRepository.save(customer);
    }

}