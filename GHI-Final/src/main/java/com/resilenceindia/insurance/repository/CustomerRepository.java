package com.resilenceindia.insurance.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.resilenceindia.insurance.entity.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    
    /**
     * Find customer by email address
     */
    Optional<Customer> findByEmail(String email);
    
    /**
     * Find customer by phone number
     */
    Optional<Customer> findByPhoneNumber(String phoneNumber);
    
    /**
     * Check if customer exists by email
     */
    boolean existsByEmail(String email);
    
    /**
     * Check if customer exists by phone number
     */
    boolean existsByPhoneNumber(String phoneNumber);
    
    /**
     * Find customer by email and password for login
     */
    @Query("SELECT c FROM Customer c WHERE c.email = :email AND c.password = :password AND c.isActive = true")
    Optional<Customer> findByEmailAndPasswordAndIsActiveTrue(@Param("email") String email, @Param("password") String password);
    
    /**
     * Find active customers only
     */
    @Query("SELECT c FROM Customer c WHERE c.isActive = true")
    java.util.List<Customer> findActiveCustomers();
    
    /**
     * Find customer by email and active status
     */
    Optional<Customer> findByEmailAndIsActiveTrue(String email);
    
    @Query("SELECT c FROM Customer c WHERE c.agent.id = :agentId AND c.isActive = true")
    List<Customer> findByAgentId(@Param("agentId") Long agentId);
}
