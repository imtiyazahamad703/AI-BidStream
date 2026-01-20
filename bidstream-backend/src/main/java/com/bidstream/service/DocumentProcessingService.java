package com.bidstream.service;

import com.bidstream.domain.DocumentNode;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DocumentProcessingService {

    private final PdfExtractionService pdfExtractionService;
    private final TextChunkingService textChunkingService;
    private final GeminiEmbeddingService geminiEmbeddingService;

    public DocumentProcessingService(PdfExtractionService pdfExtractionService,
                                     TextChunkingService textChunkingService,
                                     GeminiEmbeddingService geminiEmbeddingService) {
        this.pdfExtractionService = pdfExtractionService;
        this.textChunkingService = textChunkingService;
        this.geminiEmbeddingService = geminiEmbeddingService;
    }

    public List<DocumentNode> processDocument(Long auctionId, String filePath, String originalFileName) {
        // 1. Extract Text
        String extractedText = pdfExtractionService.extractTextFromPdf(filePath);

        // 2. Chunk Text
        List<String> chunks = textChunkingService.chunkText(extractedText);

        // 3. Generate Embeddings for each chunk
        List<DocumentNode> documentNodes = new ArrayList<>();
        
        for (String chunk : chunks) {
            List<Double> embedding = geminiEmbeddingService.generateEmbedding(chunk);
            DocumentNode node = new DocumentNode(auctionId, originalFileName, chunk, embedding);
            documentNodes.add(node);
        }

        return documentNodes;
    }
}
