package com.bidstream.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TextChunkingService {

    @Value("${document.chunk.size:1000}")
    private int defaultChunkSize;

    @Value("${document.chunk.overlap:200}")
    private int defaultOverlap;

    public List<String> chunkText(String text) {
        return chunkText(text, defaultChunkSize, defaultOverlap);
    }

    public List<String> chunkText(String text, int chunkSize, int overlap) {
        List<String> chunks = new ArrayList<>();
        if (text == null || text.trim().isEmpty()) {
            return chunks;
        }

        // Clean up the text before chunking
        String normalizedText = text.replaceAll("\\r\\n", "\n").replaceAll("\\n{2,}", "\n\n");
        String[] words = normalizedText.split("\\s+");
        
        int currentIndex = 0;
        while (currentIndex < words.length) {
            int endIndex = Math.min(currentIndex + chunkSize, words.length);
            
            StringBuilder chunkBuilder = new StringBuilder();
            for (int i = currentIndex; i < endIndex; i++) {
                chunkBuilder.append(words[i]).append(" ");
            }
            chunks.add(chunkBuilder.toString().trim());

            if (endIndex == words.length) {
                break;
            }
            
            // Move index forward by (chunkSize - overlap)
            currentIndex += (chunkSize - overlap);
        }

        return chunks;
    }
}
