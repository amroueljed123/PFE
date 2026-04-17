package com.esb.appback.service;

import com.esb.appback.dto.OcrResultDTO;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Intelligent parsing layer that uses RegEx and fuzzy boundaries
 * to find the total TTC, HT, date, and invoice number.
 */
@Service
public class InvoiceParsingLogic {


    public OcrResultDTO parseText(String text) {
        OcrResultDTO result = new OcrResultDTO();
        int confidence = 0;
        
        // Clean text slightly (normalize spaces/newlines without breaking structure)
        String lowerText = text.toLowerCase();

        // 1. Extract Date (Looking for dd/MM/yyyy or dd-MM-yyyy or dd Month yyyy)
        // Usually date is near the top or near "date" keyword
        Pattern datePattern = Pattern.compile("(?i)(?:date[\\s:.\\-]*|le\\s+)?(\\d{1,2}[/\\-]\\d{1,2}[/\\-]\\d{4}|\\d{1,2}\\s+[a-zûé]+\\s+\\d{4})");
        Matcher dateMatcher = datePattern.matcher(lowerText);
        if (dateMatcher.find()) {
            String parsedDate = parseDateString(dateMatcher.group(1));
            if (parsedDate != null) {
                result.setDate(parsedDate);
                confidence += 20;
            }
        }

        // 2. Extract Invoice Number 
        // Handles "Facture N°", "Facture No:", "Invoice #", etc.
        Pattern invNumPattern = Pattern.compile("(?i)(?:facture|invoice)\\s*(?:n[°o]|#|num(?:[eé]ro)?)[\\s:.\\-]*([A-Z0-9\\-]+)", Pattern.CASE_INSENSITIVE);
        Matcher invMatcher = invNumPattern.matcher(text);
        if (invMatcher.find()) {
            result.setInvoiceNumber(invMatcher.group(1));
            confidence += 20;
        }

        // 3. Extract Supplier Code (SUP-***)
        // Look for SUP- followed by numbers, optionally with spaces
        Pattern supPattern = Pattern.compile("(?i)SUP\\s*[-:#]*\\s*0*(\\d+)");
        Matcher supMatcher = supPattern.matcher(text);
        if (supMatcher.find()) {
            try {
                result.setSupplierId(Long.parseLong(supMatcher.group(1)));
                confidence += 20;
            } catch (Exception e) {}
        }

        // 4. Extract Totals (TTC, HT, TVA)
        // Values usually look like 1200,00 or 1 200.00
        // We look for keywords like "total ttc", "net a payer", "montant ht", "tva"
        result.setAmountTTC(findAmountNearKeyword(lowerText, "ttc", "net", "payer"));
        result.setAmountHT(findAmountNearKeyword(lowerText, "ht", "hors taxe"));
        result.setTva(findAmountNearKeyword(lowerText, "tva", "taxe"));

        if (result.getAmountTTC() != null) confidence += 25;
        if (result.getAmountHT() != null) confidence += 20;
        if (result.getTva() != null) confidence += 15;

        // Smart fallback logic for missing fields
        if (result.getAmountTTC() != null && result.getAmountHT() != null && result.getTva() == null) {
            result.setTva(Math.round((result.getAmountTTC() - result.getAmountHT()) * 100.0) / 100.0);
        } else if (result.getAmountHT() != null && result.getTva() != null && result.getAmountTTC() == null) {
            result.setAmountTTC(result.getAmountHT() + result.getTva());
        }

        result.setConfidence(Math.min(confidence, 100));
        result.setArticles(extractArticles(lowerText, text));
        return result;
    }

    private java.util.List<com.esb.appback.dto.ParsedArticleDTO> extractArticles(String lowerText, String originalText) {
        java.util.List<com.esb.appback.dto.ParsedArticleDTO> list = new java.util.ArrayList<>();
        String[] types = {"ordinateur", "moniteur", "telephone", "usb", "imprimante", "cartouche", "peripherique", "bureau", "chaise", "meuble", "etagere"};
        
        String[] lines = originalText.split("\\r?\\n");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.isEmpty()) continue;
            
            String lowerLine = line.toLowerCase();
            for (String t : types) {
                if (lowerLine.startsWith(t) || lowerLine.contains(" " + t + " ") || lowerLine.contains(t + " ")) {
                    com.esb.appback.dto.ParsedArticleDTO article = new com.esb.appback.dto.ParsedArticleDTO();
                    article.setType(t.toUpperCase());
                    article.setDescription(line);
                    
                    // Look at this line and the next 1-2 lines to find QTY, Price and Characteristics
                    String block = line;
                    for (int j = 1; j <= 2 && (i+j) < lines.length; j++) {
                        block += " " + lines[i+j].trim();
                    }
                    
                    // Extract Characteristics: "Key: Value," or "Key: Value"
                    Matcher charMatcher = Pattern.compile("([a-zA-Zéàè]+)\\s*:\\s*([a-zA-Z0-9_\\-\\sÀ-ÿ]+?)(?:,|€|$)").matcher(block);
                    while(charMatcher.find()) {
                        String key = charMatcher.group(1).trim().toLowerCase();
                        String val = charMatcher.group(2).trim();
                        // Ignore garbage keys
                        if (!key.equals("ttc") && !key.equals("ht") && !key.equals("tva")) {
                            article.getCharacteristics().put(key, val);
                        }
                    }
                    
                    // Try to guess Price and Quantity (Assuming format like "2 850,00 €" -> QTY=2, Price=850.00)
                    Matcher numMatcher = Pattern.compile("(?:^|\\s)(\\d+)\\s+(\\d{1,3}(?:[\\s']\\d{3})*(?:[\\.,]\\d{2})?)\\s*(?:€|da|dzd)", Pattern.CASE_INSENSITIVE).matcher(block);
                    if (numMatcher.find()) {
                        try {
                            article.setQuantity(Integer.parseInt(numMatcher.group(1)));
                            String priceRaw = numMatcher.group(2).replaceAll("[\\s']", "").replace(",", ".");
                            article.setUnitPrice(Double.parseDouble(priceRaw));
                        } catch (Exception e) {}
                    } else {
                        // Fallback defaults
                        article.setQuantity(1);
                        article.setUnitPrice(0.0);
                    }
                    
                    // Prevent dupes
                    boolean hasDuplicateTypeNearby = false;
                    for (com.esb.appback.dto.ParsedArticleDTO existing : list) {
                        if (existing.getType().equals(article.getType()) && existing.getDescription().equals(article.getDescription())) {
                            hasDuplicateTypeNearby = true;
                        }
                    }
                    if (!hasDuplicateTypeNearby) {
                        list.add(article);
                    }
                    break;
                }
            }
        }
        return list;
    }

    private Double findAmountNearKeyword(String text, String... keywords) {
        for (String keyword : keywords) {
            // Regex explanation: look for keyword, followed by optional chars.
            // Negative lookahead (?!\\s*%|\\d) to avoid parsing "2" from "20%" when engine backtracks
            String regex = keyword + "[\\s\\S]{0,30}?(?:[:=])?\\s*(?:€|dzd|da)?\\s*(\\d{1,3}(?:[\\s\\.,']?\\d{3})*(?:[\\.,]\\d{2})?)(?!\\s*%|\\d)";
            Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(text);
            if (m.find()) {
                try {
                    String rawNum = m.group(1);
                    // Normalize number (remove spaces, change comma to dot)
                    rawNum = rawNum.replaceAll("[\\s']", "").replace(",", ".");
                    return Double.parseDouble(rawNum);
                } catch (Exception e) {
                    continue;
                }
            }
        }
        return null;
    }

    private String parseDateString(String rawDate) {
        try {
            if (rawDate.contains("/") || rawDate.contains("-")) {
                String[] parts = rawDate.split("[/\\-]");
                if (parts.length == 3 && parts[2].length() == 4) {
                    String d = parts[0].length() == 1 ? "0" + parts[0] : parts[0];
                    String m = parts[1].length() == 1 ? "0" + parts[1] : parts[1];
                    return parts[2] + "-" + m + "-" + d;
                }
            } else {
                String[] parts = rawDate.split("\\s+");
                if (parts.length >= 3) {
                    String d = parts[0].length() == 1 ? "0" + parts[0] : parts[0];
                    String y = parts[2];
                    String mStr = parts[1].toLowerCase();
                    String m = "01";
                    if (mStr.startsWith("f")) m = "02";
                    else if (mStr.startsWith("mar")) m = "03";
                    else if (mStr.startsWith("avr")) m = "04";
                    else if (mStr.startsWith("mai")) m = "05";
                    else if (mStr.startsWith("juin")) m = "06";
                    else if (mStr.startsWith("juil")) m = "07";
                    else if (mStr.startsWith("ao")) m = "08";
                    else if (mStr.startsWith("sep")) m = "09";
                    else if (mStr.startsWith("oct")) m = "10";
                    else if (mStr.startsWith("nov")) m = "11";
                    else if (mStr.startsWith("d")) m = "12";
                    return y + "-" + m + "-" + d;
                }
            }
        } catch (Exception e) {}
        return null;
    }
}
