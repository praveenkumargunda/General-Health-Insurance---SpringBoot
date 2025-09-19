package com.resilenceindia.insurance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.resilenceindia.insurance.entity.Policy;

@Repository
public interface PolicyRepository extends JpaRepository<Policy, Long> {
}
