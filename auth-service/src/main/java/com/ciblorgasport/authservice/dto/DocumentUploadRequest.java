package com.ciblorgasport.authservice.dto;

import java.util.List;

public class DocumentUploadRequest {
    private String username;
    private List<String> documents;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public List<String> getDocuments() { return documents; }
    public void setDocuments(List<String> documents) { this.documents = documents; }
}
