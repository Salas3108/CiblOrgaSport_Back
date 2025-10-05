package com.ciblorgasport.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "users")
public class User implements UserDetails {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Size(max = 50)
    @Column(unique = true)
    private String username;
    
    @NotBlank
    @Size(max = 100)
    @Email
    @Column(unique = true)
    private String email;
    
    @NotBlank
    @Size(max = 120)
    private String password;
    
    @Enumerated(EnumType.STRING)
    private Role role;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructeurs
    public User() {}
    
    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = Role.USER; // Rôle par défaut
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public User(String username, String email, String password, Role role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters et Setters
    public Long getId() { 
        return id; 
    }
    
    public void setId(Long id) { 
        this.id = id; 
    }
    
    public void setUsername(String username) { 
        this.username = username; 
    }
    
    public String getEmail() { 
        return email; 
    }
    
    public void setEmail(String email) { 
        this.email = email; 
    }
    
    public void setPassword(String password) { 
        this.password = password; 
    }
    
    public Role getRole() { 
        return role; 
    }
    
    public void setRole(Role role) { 
        this.role = role; 
    }
    
    public LocalDateTime getCreatedAt() { 
        return createdAt; 
    }
    
    public void setCreatedAt(LocalDateTime createdAt) { 
        this.createdAt = createdAt; 
    }
    
    public LocalDateTime getUpdatedAt() { 
        return updatedAt; 
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) { 
        this.updatedAt = updatedAt; 
    }
    
    // UserDetails methods
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }
    
    @Override
    public String getPassword() {
        return this.password;
    }
    
    @Override
    public String getUsername() {
        return this.username;
    }
    
    @Override
    public boolean isAccountNonExpired() { 
        return true; 
    }
    
    @Override
    public boolean isAccountNonLocked() { 
        return true; 
    }
    
    @Override
    public boolean isCredentialsNonExpired() { 
        return true; 
    }
    
    @Override
    public boolean isEnabled() { 
        return true; 
    }
}