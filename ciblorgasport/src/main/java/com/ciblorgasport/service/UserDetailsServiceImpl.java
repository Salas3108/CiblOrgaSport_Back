package com.ciblorgasport.service;

import com.ciblorgasport.entity.User;
import com.ciblorgasport.repository.UserRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));
        return user;
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé : " + username));
    }

    public void save(User user) {
        userRepository.save(user);
    }

	public boolean existsByUsername(String newUsername) {
		return userRepository.existsByUsername(newUsername);
	}

	public boolean existsByEmail(String email) {
	    return userRepository.existsByEmail(email);
	}
}
