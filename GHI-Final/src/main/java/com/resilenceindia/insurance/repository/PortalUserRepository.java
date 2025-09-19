package com.resilenceindia.insurance.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.resilenceindia.insurance.entity.PortalUser;

public interface PortalUserRepository extends JpaRepository<PortalUser, Long> {
    Optional<PortalUser> findByUsername(String username);
}
