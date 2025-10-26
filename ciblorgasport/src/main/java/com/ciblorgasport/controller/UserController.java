package com.ciblorgasport.controller;

import com.ciblorgasport.entity.User;
import com.ciblorgasport.service.UserDetailsServiceImpl;


import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserDetailsServiceImpl userService;

    public UserController(UserDetailsServiceImpl userService) {
        this.userService = userService;
    }

    @PostMapping("/upload-documents")
    public ResponseEntity<Map<String, Object>> uploadDocuments(@RequestBody List<String> documents, 
                                                               Authentication authentication) {
        String username = authentication.getName(); // utilisateur connecté
        User updatedUser = userService.uploadDocuments(username, documents);

        Map<String, Object> response = new HashMap<>();
        response.put("user", updatedUser);
        response.put("validated", updatedUser.isValidated());
        return ResponseEntity.ok(response);
    }

}
