package com.ciblorgasport.controller;

import com.ciblorgasport.entity.User;
import com.ciblorgasport.service.UserDetailsServiceImpl;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserDetailsServiceImpl userService;

    public UserController(UserDetailsServiceImpl userService) {
        this.userService = userService;
    }

    @PostMapping("/upload-documents")
    public ResponseEntity<?> uploadDocuments(
            @RequestParam("passeport") MultipartFile passeport,
            @RequestParam("certificat") MultipartFile certificat,
            Authentication authentication) throws IOException {

        String username = authentication.getName();
        User user = userService.getUserByUsername(username);

        Path userDir = Paths.get("uploads").resolve(String.valueOf(user.getId()));
        Files.createDirectories(userDir);

        user.getDocuments().removeIf(doc -> doc.startsWith("passeport:"));
        user.getDocuments().removeIf(doc -> doc.startsWith("certificat:"));

        if (!passeport.isEmpty()) {
            Path filePath = userDir.resolve(passeport.getOriginalFilename());
            passeport.transferTo(filePath);
            user.addDocument("passeport: " + passeport.getOriginalFilename());
        }

        if (!certificat.isEmpty()) {
            Path filePath = userDir.resolve(certificat.getOriginalFilename());
            certificat.transferTo(filePath);
            user.addDocument("certificat: " + certificat.getOriginalFilename());
        }

        userService.save(user);

        Map<String, Object> response = new HashMap<>();
        response.put("documents", user.getDocuments());
        response.put("validated", user.isValidated());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete-document")
    public ResponseEntity<?> deleteDocument(@RequestBody Map<String, String> body, Authentication authentication) throws IOException {
        String username = authentication.getName();
        User user = userService.getUserByUsername(username);

        String docPath = body.get("document");
        if (docPath != null && user.getDocuments().contains(docPath)) {
            String[] parts = docPath.split(": ");
            if (parts.length == 2) {
                String fileName = parts[1];
                Path filePath = Paths.get("uploads").resolve(String.valueOf(user.getId())).resolve(fileName);
                Files.deleteIfExists(filePath);
            }

            user.getDocuments().remove(docPath);
            userService.save(user);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("documents", user.getDocuments());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/documents/{userId}/{fileName}")
    public ResponseEntity<Resource> getDocument(
            @PathVariable Long userId,
            @PathVariable String fileName) throws IOException {

        Path filePath = Paths.get("uploads/" + userId + "/" + fileName);
        if (!Files.exists(filePath)) {
            return ResponseEntity.notFound().build();
        }

        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(filePath));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                .body(resource);
    }
    
    @PutMapping("/update-profile")
    public ResponseEntity<?> updateProfile(@RequestBody Map<String, String> body, Authentication authentication) {
        String currentUsername = authentication.getName();
        User user = userService.getUserByUsername(currentUsername);

        if (user == null) {
            return ResponseEntity.status(404).body(Map.of("error", "Utilisateur non trouvé"));
        }

        String newUsername = body.get("username");
        String newEmail = body.get("email");

        if (newUsername != null && !newUsername.equals(user.getUsername())) {
            if (userService.existsByUsername(newUsername)) {
                return ResponseEntity.status(409).body(Map.of("error", "Nom d'utilisateur déjà utilisé"));
            }
            user.setUsername(newUsername);
        }

        if (newEmail != null && !newEmail.equals(user.getEmail())) {
            if (userService.existsByEmail(newEmail)) {
                return ResponseEntity.status(409).body(Map.of("error", "Email déjà utilisé"));
            }
            user.setEmail(newEmail);
        }

        userService.save(user);

        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("username", user.getUsername());
        response.put("email", user.getEmail());
        response.put("role", user.getRole());
        response.put("documents", user.getDocuments());
        response.put("validated", user.isValidated());

        return ResponseEntity.ok(response);
    }


    }



