package com.resilenceindia.insurance.service;


import com.resilenceindia.insurance.dto.AgentLoginRequest;
import com.resilenceindia.insurance.dto.AgentRegistrationRequest;
import com.resilenceindia.insurance.entity.Agent;
import com.resilenceindia.insurance.exception.CustomerAlreadyExistsException;
import com.resilenceindia.insurance.exception.InvalidCredentialsException;
import com.resilenceindia.insurance.repository.AgentRepository;
import com.resilenceindia.insurance.repository.CustomerRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AgentServiceTest {

    @Mock
    private AgentRepository agentRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private AgentService agentService;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerAgent_success() {
        AgentRegistrationRequest request = new AgentRegistrationRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john@example.com");
        request.setPhoneNumber("9876543210");
        request.setPassword("secret");
        request.setDocumentNumber("DOC123");
        request.setDateOfBirth("1-1-1990");

        when(agentRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(agentRepository.existsByPhoneNumber("9876543210")).thenReturn(false);
        when(agentRepository.existsByDocumentNumber("DOC123")).thenReturn(false);

        Agent saved = new Agent();
        saved.setId(1L);
        saved.setEmail("john@example.com");

        when(agentRepository.save(any(Agent.class))).thenReturn(saved);

        Agent result = agentService.registerAgent(request);

        assertNotNull(result);
        assertEquals("john@example.com", result.getEmail());
        verify(agentRepository, times(1)).save(any(Agent.class));
    }

    @Test
    void registerAgent_duplicateEmail_throwsException() {
        AgentRegistrationRequest request = new AgentRegistrationRequest();
        request.setEmail("john@example.com");

        when(agentRepository.existsByEmail("john@example.com")).thenReturn(true);

        assertThrows(CustomerAlreadyExistsException.class,
                () -> agentService.registerAgent(request));
    }

    @Test
    void authenticateAgent_success() {
        String raw = "mypassword";
        String encoded = encoder.encode(raw);

        Agent agent = new Agent();
        agent.setId(1L);
        agent.setEmail("john@example.com");
        agent.setPassword(encoded);

        AgentLoginRequest request = new AgentLoginRequest();
        request.setEmail("john@example.com");
        request.setPassword(raw);

        when(agentRepository.findByEmailAndIsActiveTrueAndIsApprovedTrue("john@example.com"))
                .thenReturn(Optional.of(agent));

        Agent result = agentService.authenticateAgent(request);

        assertNotNull(result);
        assertEquals("john@example.com", result.getEmail());
    }

    @Test
    void authenticateAgent_wrongPassword_throwsException() {
        Agent agent = new Agent();
        agent.setEmail("john@example.com");
        agent.setPassword(encoder.encode("correct"));

        AgentLoginRequest request = new AgentLoginRequest();
        request.setEmail("john@example.com");
        request.setPassword("wrong");

        when(agentRepository.findByEmailAndIsActiveTrueAndIsApprovedTrue("john@example.com"))
                .thenReturn(Optional.of(agent));

        assertThrows(InvalidCredentialsException.class,
                () -> agentService.authenticateAgent(request));
    }
}
