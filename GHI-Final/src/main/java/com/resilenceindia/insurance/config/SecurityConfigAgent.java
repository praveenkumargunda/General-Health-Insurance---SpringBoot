//package com.resilenceindia.insurance.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfigAgent {
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//            .csrf(csrf -> csrf.disable())
//            .authorizeHttpRequests(auth -> auth
//                .requestMatchers(
//                    "/agent/login",
//                    "/agent/register",
//                    "/agent/dashboard",
//                    "/agent/purchase/policies",
//                    "/agent/purchase/payment",
//                    "/agent/purchase/confim",
//                    "/agent/dashboard",
//                    "/agent/web/login",
//                    "/agent/web/register",
//                    "/agent/api/login",
//                    "/agent/api/register",
//                    "/agent/api/dashboard",
//                    "/agent/web/dashboard",
//                    "/resilience-insurance/api/login",
//                    "/resilience-insurance/api/register",
//                    "/resilience-insurance/api/dashboard",
//                    "/css/**", "/js/**", "/images/**"
//                ).permitAll()
//                .anyRequest().authenticated()
//            )
//            .logout(logout -> logout
//                .logoutUrl("/agent/web/logout")
//                .logoutSuccessUrl("/agent/login?logout=true")
//                .permitAll()
//            );
//
//        return http.build();
//    }
//}


//package com.resilenceindia.insurance.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.provisioning.InMemoryUserDetailsManager;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfigAgent {
//
//    @Bean
//    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
//        UserDetails agentUser = User.withUsername("admin")
//                .password(passwordEncoder.encode("admin@123")) // define password here
//                .roles("AGENT")
//                .build();
//
//        return new InMemoryUserDetailsManager(agentUser);
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//            .csrf(csrf -> csrf.disable())
//            .authorizeHttpRequests(auth -> auth
//                // every /agent/** endpoint requires authentication
//                .requestMatchers("/agent/**").hasRole("AGENT")
//
//                // all other endpoints are open
//                .anyRequest().permitAll()
//            )
//            .formLogin(form -> form
//                // use Spring Security's default login page
//                .defaultSuccessUrl("/agent/dashboard", true)
//                .permitAll()
//            )
//            .logout(logout -> logout
//                .logoutUrl("/agent/web/logout")
//                .logoutSuccessUrl("/agent/login?logout=true")
//                .permitAll()
//            );
//
//        return http.build();
//    }
//}
