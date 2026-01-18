package com.bidstream.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/documents")
@CrossOrigin(origins = "*")
public class DocumentController {

    @PostMapping("/upload/{auctionId}")
    public ResponseEntity<?> uploadDocument(@PathVariable Long auctionId, @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "File is empty"));
        }
        
        if (!file.getContentType().equals("application/pdf")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Only PDF files are allowed"));
        }

        // Logic for handling the upload will be implemented in subsequent commits
        return ResponseEntity.ok(Map.of(
                "message", "File uploaded successfully",
                "fileName", file.getOriginalFilename(),
                "auctionId", auctionId
        ));
    }
}
