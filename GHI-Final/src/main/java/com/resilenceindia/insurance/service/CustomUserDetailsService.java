package com.resilenceindia.insurance.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.resilenceindia.insurance.entity.PortalUser;
import com.resilenceindia.insurance.repository.PortalUserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private PortalUserRepository portalUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        PortalUser user = portalUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return User.builder()
                .username(user.getUsername())
                .password(user.getPassword()) // already BCrypt encoded
                .roles(user.getRole().replace("ROLE_", "")) 
                .build();
    }
}