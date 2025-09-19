package com.resilenceindia.insurance.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.resilenceindia.insurance.dto.AgentLoginRequest;
import com.resilenceindia.insurance.dto.AgentRegistrationRequest;
import com.resilenceindia.insurance.entity.Agent;
import com.resilenceindia.insurance.entity.Customer;
import com.resilenceindia.insurance.repository.AgentRepository;
import com.resilenceindia.insurance.repository.CustomerRepository;
import com.resilenceindia.insurance.exception.CustomerAlreadyExistsException;
import com.resilenceindia.insurance.exception.InvalidCredentialsException;

@Service
@Transactional
public class AgentService {
    
    @Autowired
    private AgentRepository agentRepository;
    
    @Autowired
    private CustomerRepository customerRepository;
    
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    /**
     * Register a new agent
     */
    public Agent registerAgent(AgentRegistrationRequest request) {
        // Check if agent already exists
        if (agentRepository.existsByEmail(request.getEmail())) {
            throw new CustomerAlreadyExistsException("Agent with email " + request.getEmail() + " already exists");
        }
        
        if (agentRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new CustomerAlreadyExistsException("Agent with phone number " + request.getPhoneNumber() + " already exists");
        }
        
        if (agentRepository.existsByDocumentNumber(request.getDocumentNumber())) {
            throw new CustomerAlreadyExistsException("Agent with document number " + request.getDocumentNumber() + " already exists");
        }
        
        // Create new agent
        Agent agent = new Agent();
        agent.setFirstName(request.getFirstName());
        agent.setLastName(request.getLastName());
        agent.setEmail(request.getEmail());
        agent.setPhoneNumber(request.getPhoneNumber());
        agent.setPassword(passwordEncoder.encode(request.getPassword())); // Encrypt password
        agent.setDateOfBirth(request.getDateOfBirth());
        agent.setAddress(request.getAddress());
        agent.setCity(request.getCity());
        agent.setState(request.getState());
        agent.setPinCode(request.getPinCode());
        agent.setDocumentType(request.getDocumentType());
        agent.setDocumentNumber(request.getDocumentNumber());
        agent.setExperienceYears(request.getExperienceYears());
        agent.setIsActive(true);
        agent.setIsApproved(true); 
        
        return agentRepository.save(agent);
    }
    
    /**
     * Authenticate agent login
     */
    public Agent authenticateAgent(AgentLoginRequest request) {
        Optional<Agent> agentOpt = agentRepository.findByEmailAndIsActiveTrueAndIsApprovedTrue(request.getEmail());
        
        if (agentOpt.isPresent()) {
            Agent agent = agentOpt.get();
            if (passwordEncoder.matches(request.getPassword(), agent.getPassword())) {
                return agent;
            }
        }
        
        throw new InvalidCredentialsException("Invalid email or password");
    }
    
    /**
     * Find agent by ID
     */
    public Optional<Agent> findAgentById(Long id) {
        return agentRepository.findById(id);
    }
    
    /**
     * Find agent by email
     */
    public Optional<Agent> findAgentByEmail(String email) {
        return agentRepository.findByEmailAndIsActiveTrueAndIsApprovedTrue(email);
    }
    
    /**
     * Update agent profile
     */
    @Transactional
    public Agent updateAgentProfile(Long id, Agent form) {
        Optional<Agent> agentOpt = agentRepository.findById(id);

        if (agentOpt.isPresent()) {
            Agent agent = agentOpt.get();

            // Update only editable fields
            agent.setFirstName(form.getFirstName());
            agent.setLastName(form.getLastName());
            agent.setPhoneNumber(form.getPhoneNumber());
            agent.setAddress(form.getAddress());
            agent.setCity(form.getCity());
            agent.setState(form.getState());
            agent.setPinCode(form.getPinCode());

            return agentRepository.save(agent);
        }

        throw new RuntimeException("Agent not found");
    }

    
    /**
     * Deactivate agent account
     */
    public void deactivateAgent(Long id) {
        Optional<Agent> agentOpt = agentRepository.findById(id);
        if (agentOpt.isPresent()) {
            Agent agent = agentOpt.get();
            agent.setIsActive(false);
            agentRepository.save(agent);
        }
    }
    
    /**
     * Check if email already exists
     */
    public boolean emailExists(String email) {
        return agentRepository.existsByEmail(email);
    }
    
    /**
     * Check if phone number already exists
     */
    public boolean phoneNumberExists(String phoneNumber) {
        return agentRepository.existsByPhoneNumber(phoneNumber);
    }
    
    /**
     * Check if document number already exists
     */
    public boolean documentNumberExists(String documentNumber) {
        return agentRepository.existsByDocumentNumber(documentNumber);
    }
    
    /**
     * Get assigned customers for an agent (Auto-assignment logic)
     */
    public List<Customer> getAssignedCustomers(Long agentId) {
        return customerRepository.findByAgentId(agentId);
    }

    
    /**
     * Get all active agents
     */
    public List<Agent> getAllActiveAgents() {
        return agentRepository.findApprovedAgents();
    }
    
    /**
     * Get agent statistics
     */
    public Long getTotalActiveAgents() {
        return agentRepository.countActiveAgents();
    }
    
    /**
     * Save agent entity directly
     */
    public Agent saveAgent(Agent agent) {
        return agentRepository.save(agent);
    }
    
    /**
     * Method to check if the customer is assigned to the agent
     */
    public boolean isCustomerAssignedToAgent(Long customerId, Long agentId) {
        return agentRepository.isCustomerAssignedToAgent(customerId, agentId);
    }

}