package com.hackathon.service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class DocumentService {

    public String extractText(MultipartFile file) throws IOException {
        String name = file.getOriginalFilename();
        if (name == null) name = "";
        String ext = name.toLowerCase();
        int dot = ext.lastIndexOf('.');
        ext = dot >= 0 ? ext.substring(dot + 1) : "";

        if ("pdf".equals(ext)) {
            try (PDDocument doc = Loader.loadPDF(file.getBytes())) {
                return new PDFTextStripper().getText(doc);
            }
        }
        if ("txt".equals(ext) || "md".equals(ext)) {
            return new String(file.getBytes(), StandardCharsets.UTF_8);
        }
        throw new IOException("Desteklenmeyen dosya tipi: ." + ext);
    }
}
