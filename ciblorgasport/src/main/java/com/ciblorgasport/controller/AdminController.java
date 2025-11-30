package com.ciblorgasport.controller;

import com.ciblorgasport.entity.User;
import com.ciblorgasport.service.UserDetailsServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserDetailsServiceImpl userService;

    public AdminController(UserDetailsServiceImpl userService) {
        this.userService = userService;
    }

    // GET /api/admin/pending-users
    @GetMapping("/pending-users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getPendingUsers() {
        List<User> pending = userService.getAllUsers().stream()
                                        .filter(u -> !u.isValidated())
                                        .toList();
        return ResponseEntity.ok(pending);
    }

    // PUT /api/admin/validate/{id}
    @PutMapping("/validate/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> validateUser(@PathVariable Long id) {
        User user = userService.getUserById(id);
        user.setValidated(true);
        userService.save(user);

        return ResponseEntity.ok("Utilisateur validé avec succès");
    }
}
