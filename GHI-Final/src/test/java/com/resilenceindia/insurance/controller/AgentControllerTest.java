package com.resilenceindia.insurance.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.resilenceindia.insurance.dto.AgentLoginRequest;
import com.resilenceindia.insurance.dto.AgentRegistrationRequest;
import com.resilenceindia.insurance.entity.Agent;
import com.resilenceindia.insurance.service.AgentService;

@WebMvcTest(controllers = AgentController.class)
@AutoConfigureMockMvc(addFilters = false)   // 🚀 disables Spring Security
class AgentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AgentService agentService;

    @Autowired
    private ObjectMapper objectMapper;

    private Agent agent;

    @BeforeEach
    void setup() {
        agent = new Agent();
        agent.setId(1L);
        agent.setFirstName("John");
        agent.setLastName("Doe");
        agent.setEmail("john@example.com");
    }

   /* @Test
    void testRegisterAgentApi_success() throws Exception {
        AgentRegistrationRequest request = new AgentRegistrationRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john@example.com");
        request.setPhoneNumber("9999999999");
        request.setPassword("secret");
        request.setDocumentNumber("DOC123");

        Mockito.when(agentService.registerAgent(any(AgentRegistrationRequest.class)))
                .thenReturn(agent);

        mockMvc.perform(post("/agent/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Agent registered successfully"));
    }*/

    @Test
    void testLoginAgentApi_success() throws Exception {
        AgentLoginRequest request = new AgentLoginRequest();
        request.setEmail("john@example.com");
        request.setPassword("secret");

        Mockito.when(agentService.authenticateAgent(any(AgentLoginRequest.class)))
                .thenReturn(agent);

        mockMvc.perform(post("/agent/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Login successful"));
    }
}
