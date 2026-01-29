package com.bidstream.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * Controller for handling the full document ingestion and retrieval pipeline.
 * Coordinates uploading, text extraction, embedding generation, and vector similarity search.
 */
@RestController
@RequestMapping("/api/documents")
@CrossOrigin(origins = "*")
public class DocumentController {

    private final com.bidstream.service.DocumentStorageService storageService;
    private final com.bidstream.service.DocumentProcessingService processingService;
    private final com.bidstream.service.VectorSearchService vectorSearchService;

    public DocumentController(com.bidstream.service.DocumentStorageService storageService,
                              com.bidstream.service.DocumentProcessingService processingService,
                              com.bidstream.service.VectorSearchService vectorSearchService) {
        this.storageService = storageService;
        this.processingService = processingService;
        this.vectorSearchService = vectorSearchService;
    }

    @PostMapping("/upload/{auctionId}")
    public ResponseEntity<?> uploadDocument(
            @PathVariable Long auctionId, 
            @RequestParam(value = "itemId", required = false) Long itemId,
            @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "File is empty"));
        }
        
        String contentType = file.getContentType();
        if (contentType == null || !contentType.equals("application/pdf")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Unsupported file type. Only PDF documents are allowed for auction items."));
        }
        
        long MAX_SIZE = 10 * 1024 * 1024; // 10MB
        if (file.getSize() > MAX_SIZE) {
            return ResponseEntity.badRequest().body(Map.of("error", "File exceeds maximum size of 10MB"));
        }

        String storedPath = storageService.storeFile(file);
        processingService.processDocument(auctionId, storedPath, file.getOriginalFilename());

        return ResponseEntity.ok(Map.of(
                "message", "File uploaded and processed successfully",
                "fileName", file.getOriginalFilename(),
                "auctionId", auctionId
        ));
    }

    @GetMapping("/context/{auctionId}")
    public ResponseEntity<?> getAuctionContext(@PathVariable Long auctionId, @RequestParam("query") String query) {
        var results = vectorSearchService.searchSimilarDocuments(auctionId, query, 3);
        
        // Extract the text chunks to return
        java.util.List<String> contexts = results.stream()
                .map(com.bidstream.domain.DocumentNode::getContent)
                .toList();

        return ResponseEntity.ok(Map.of(
                "auctionId", auctionId,
                "query", query,
                "context", contexts
        ));
    }

    @GetMapping("/chunks/{auctionId}")
    public ResponseEntity<?> getRelevantChunks(@PathVariable Long auctionId, @RequestParam("query") String query) {
        var results = vectorSearchService.searchSimilarDocuments(auctionId, query, 5);
        
        // Return full chunk metadata and content
        return ResponseEntity.ok(Map.of(
                "auctionId", auctionId,
                "query", query,
                "chunks", results
        ));
    }
}
