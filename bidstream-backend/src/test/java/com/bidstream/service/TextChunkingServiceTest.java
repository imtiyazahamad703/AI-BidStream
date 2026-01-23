package com.bidstream.service;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TextChunkingServiceTest {

    private final TextChunkingService chunkingService = new TextChunkingService();

    @Test
    void chunkText_EmptyText_ReturnsEmptyList() {
        List<String> chunks = chunkingService.chunkText("");
        assertTrue(chunks.isEmpty());
    }

    @Test
    void chunkText_WithText_ReturnsExpectedChunks() {
        String text = "This is a simple test text that should be chunked properly based on words.";
        // Custom chunk size 4, overlap 2
        List<String> chunks = chunkingService.chunkText(text, 4, 2);
        
        // "This is a simple" (4)
        // "a simple test text" (4)
        // "test text that should" (4) ... etc.
        assertTrue(chunks.size() > 1);
        assertEquals("This is a simple", chunks.get(0));
    }
}
