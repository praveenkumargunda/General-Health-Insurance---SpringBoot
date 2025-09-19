package com.resilenceindia.insurance.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.resilenceindia.insurance.entity.Agent;

@Repository
public interface AgentRepository extends JpaRepository<Agent, Long> {
    
    Optional<Agent> findByEmail(String email);
    Optional<Agent> findByPhoneNumber(String phoneNumber);
    
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByDocumentNumber(String documentNumber);
    
    @Query("SELECT a FROM Agent a WHERE a.isActive = true")
    List<Agent> findActiveAgents();
    
    Optional<Agent> findByEmailAndIsActiveTrueAndIsApprovedTrue(String email);
    
    @Query("SELECT a FROM Agent a WHERE a.isApproved = true AND a.isActive = true")
    List<Agent> findApprovedAgents();
    
    @Query("SELECT COUNT(a) FROM Agent a WHERE a.isActive = true AND a.isApproved = true")
    Long countActiveAgents();
    
    List<Agent> findByCityAndIsActiveTrueAndIsApprovedTrue(String city);
    List<Agent> findByStateAndIsActiveTrueAndIsApprovedTrue(String state);
    
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END " +
            "FROM Customer c " +
            "WHERE c.id = :customerId AND c.agent.id = :agentId")
     boolean isCustomerAssignedToAgent(@Param("customerId") Long customerId,
                                       @Param("agentId") Long agentId);
}