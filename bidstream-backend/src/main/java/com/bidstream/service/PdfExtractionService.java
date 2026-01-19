package com.bidstream.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class PdfExtractionService {

    public String extractTextFromPdf(String filePath) {
        File file = new File(filePath);
        if (!file.exists() || !file.canRead()) {
            throw new RuntimeException("Cannot read PDF file at: " + filePath);
        }

        try (PDDocument document = org.apache.pdfbox.Loader.loadPDF(file)) {
            if (document.isEncrypted()) {
                throw new RuntimeException("Cannot extract text from encrypted PDF: " + filePath);
            }

            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            return stripper.getText(document);

        } catch (IOException e) {
            throw new RuntimeException("Failed to extract text from PDF", e);
        }
    }
}
