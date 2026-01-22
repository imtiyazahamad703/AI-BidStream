package com.bidstream.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class PdfExtractionServiceTest {

    @Autowired
    private PdfExtractionService pdfExtractionService;

    @Test
    void extractTextFromPdf_FileNotFound_ThrowsException() {
        assertThrows(RuntimeException.class, () -> {
            pdfExtractionService.extractTextFromPdf("non-existent-file.pdf");
        });
    }
}
