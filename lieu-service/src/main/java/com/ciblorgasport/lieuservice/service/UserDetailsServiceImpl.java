package com.ciblorgasport.lieuservice.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Autowired;
import com.ciblorgasport.lieuservice.client.AuthServiceClient;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private AuthServiceClient authServiceClient;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username == null || username.isEmpty()) {
            throw new UsernameNotFoundException("User Not Found with username: " + username);
        }
        return authServiceClient.fetchUserByUsername(username);
    }
}
