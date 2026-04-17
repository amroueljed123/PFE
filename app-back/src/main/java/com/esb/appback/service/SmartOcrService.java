package com.esb.appback.service;

import com.esb.appback.dto.OcrResultDTO;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
public class SmartOcrService {

    @Autowired
    private InvoiceParsingLogic parsingLogic;

    public OcrResultDTO extractData(MultipartFile file) throws Exception {
        String filename = file.getOriginalFilename() != null ? file.getOriginalFilename().toLowerCase() : "";
        String extractedText = "";

        // Attempt Fast PDF Text Extraction first
        if (filename.endsWith(".pdf")) {
            extractedText = extractTextFromPdfNatively(file);
            
            // If the PDF doesn't have selectable text (it's a scanned photo), fallback to OCR
            if (extractedText == null || extractedText.trim().length() < 10) {
                System.out.println("[OCR] PDF has no native text. Falling back to Tess4J Image OCR...");
                extractedText = extractTextFromPdfViaOcr(file);
            }
        } 
        // If it's an image
        else if (filename.endsWith(".jpg") || filename.endsWith(".jpeg") || filename.endsWith(".png")) {
            System.out.println("[OCR] Image detected. Running Tess4J OCR...");
            extractedText = extractTextFromImage(file);
        } else {
            throw new IllegalArgumentException("Unsupported file type for Smart OCR.");
        }

        System.out.println("[OCR] Found Text Length: " + extractedText.length());
        
        // Pass text to our AI/Logic Parsing Engine
        return parsingLogic.parseText(extractedText);
    }

    // 1. FAST: Pure Java PDF Text Stripper (Works for 90% of software-generated PDFs)
    private String extractTextFromPdfNatively(MultipartFile file) {
        try (PDDocument document = Loader.loadPDF(file.getBytes())) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        } catch (IOException e) {
            System.err.println("[OCR] Error reading PDF natively: " + e.getMessage());
            return "";
        }
    }

    // 2. SLOW: Converts PDF pages to Images, then runs Tesseract
    private String extractTextFromPdfViaOcr(MultipartFile file) {
        StringBuilder textBuilder = new StringBuilder();
        try (PDDocument document = Loader.loadPDF(file.getBytes())) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            Tesseract tesseract = getTesseract();

            // Just process the first page to save time for invoices
            BufferedImage bim = pdfRenderer.renderImageWithDPI(0, 300, ImageType.RGB);
            String result = tesseract.doOCR(bim);
            textBuilder.append(result);
            
        } catch (Exception e) {
            System.err.println("[OCR] Error during PDF OCR: " + e.getMessage());
        }
        return textBuilder.toString();
    }

    // 3. SLOW: Runs exactly on image uploads
    private String extractTextFromImage(MultipartFile file) {
        try {
            File convFile = new File(System.getProperty("java.io.tmpdir") + "/" + file.getOriginalFilename());
            try (FileOutputStream fos = new FileOutputStream(convFile)) {
                fos.write(file.getBytes());
            }
            Tesseract tesseract = getTesseract();
            String result = tesseract.doOCR(convFile);
            convFile.delete();
            return result;
        } catch (Exception e) {
            System.err.println("[OCR] Error during Image OCR: " + e.getMessage());
            return "";
        }
    }

    private Tesseract getTesseract() {
        Tesseract tesseract = new Tesseract();
        // Uses the current app root folder or predefined path
        tesseract.setDatapath(new File("tessdata").getAbsolutePath());
        tesseract.setLanguage("fra"); // Using French out of the box
        return tesseract;
    }
}
