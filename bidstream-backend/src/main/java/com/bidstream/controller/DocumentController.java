package com.bidstream.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/documents")
@CrossOrigin(origins = "*")
public class DocumentController {

    private final com.bidstream.service.DocumentStorageService storageService;

    public DocumentController(com.bidstream.service.DocumentStorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping("/upload/{auctionId}")
    public ResponseEntity<?> uploadDocument(@PathVariable Long auctionId, @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "File is empty"));
        }
        
        if (!file.getContentType().equals("application/pdf")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Only PDF files are allowed"));
        }

        String storedPath = storageService.storeFile(file);

        return ResponseEntity.ok(Map.of(
                "message", "File uploaded successfully",
                "fileName", file.getOriginalFilename(),
                "storedPath", storedPath,
                "auctionId", auctionId
        ));
    }
}
