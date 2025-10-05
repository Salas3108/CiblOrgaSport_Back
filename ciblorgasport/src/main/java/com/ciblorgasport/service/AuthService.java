package com.ciblorgasport.service;

import com.ciblorgasport.dto.LoginRequest;
import com.ciblorgasport.dto.RegisterRequest;
import com.ciblorgasport.dto.JwtResponse;
import com.ciblorgasport.entity.User;
import com.ciblorgasport.entity.Role;
import com.ciblorgasport.repository.UserRepository;
import com.ciblorgasport.security.JwtUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    
    public AuthService(AuthenticationManager authenticationManager, 
                      UserRepository userRepository, 
                      PasswordEncoder passwordEncoder, 
                      JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }
    
    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        
        User user = (User) authentication.getPrincipal();
        return new JwtResponse(jwt, user.getUsername(), user.getEmail(), user.getRole().name());
    }
    
    public String registerUser(RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            return "Error: Username is already taken!";
        }
        
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            return "Error: Email is already in use!";
        }
        
        User user = new User(registerRequest.getUsername(), 
                           registerRequest.getEmail(),
                           passwordEncoder.encode(registerRequest.getPassword()),
                           registerRequest.getRole());
        
        userRepository.save(user);
        return "User registered successfully!";
    }
}