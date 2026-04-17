"""
OCR Module for Invoice and Document Processing
Handles text extraction, field parsing, and validation
"""
import re
from datetime import datetime
from abc import abstractmethod

class OCRProcessor:
    """Base OCR processor for document extraction"""
    
    def __init__(self):
        self.confidence_threshold = 0.80
        
    def extract_invoice_data(self, text):
        """Extract structured data from invoice text"""
        return {
            'invoice_number': self._extract_invoice_number(text),
            'date': self._extract_date(text),
            'supplier': self._extract_supplier(text),
            'items': self._extract_items(text),
            'amount_ht': self._extract_amount(text, 'HT'),
            'tva': self._extract_amount(text, 'TVA'),
            'amount_ttc': self._extract_amount(text, 'TTC'),
            'confidence': self._calculate_confidence(text)
        }
    
    def _extract_invoice_number(self, text):
        """Extract invoice/document number using regex patterns"""
        patterns = [
            r'(?:Invoice|Facture|F)\s*#?:?\s*(\w+)',
            r'(?:N°|No\.?)\s*(?:Facture|Devis)\s*(\w+)',
            r'(\w+-\d{4,})'
        ]
        for pattern in patterns:
            match = re.search(pattern, text, re.IGNORECASE)
            if match:
                return match.group(1)
        return None
    
    def _extract_date(self, text):
        """Extract invoice date"""
        date_patterns = [
            r'(?:Date|Date de facture)\s*[:\s]*(\d{1,2}[/\-\.]\d{1,2}[/\-\.]\d{2,4})',
            r'(\d{1,2}[/\-\.]\d{1,2}[/\-\.]\d{2,4})'
        ]
        for pattern in date_patterns:
            match = re.search(pattern, text, re.IGNORECASE)
            if match:
                try:
                    date_str = match.group(1)
                    return self._parse_date(date_str)
                except:
                    pass
        return None
    
    def _extract_supplier(self, text):
        """Extract supplier name"""
        lines = text.split('\n')
        for line in lines[:10]:  # Check first 10 lines
            if len(line.strip()) > 3 and len(line.strip()) < 100:
                return line.strip()
        return None
    
    def _extract_items(self, text):
        """Extract line items from invoice"""
        items = []
        # Look for patterns like: "item description qty unit_price total_price"
        item_pattern = r'(.+?)\s+(\d+)\s+(\d+[,\.]\d{2})\s+(\d+[,\.]\d{2})'
        matches = re.findall(item_pattern, text)
        for match in matches:
            items.append({
                'description': match[0].strip(),
                'quantity': int(match[1]),
                'unit_price': float(match[2].replace(',', '.')),
                'total_price': float(match[3].replace(',', '.'))
            })
        return items
    
    def _extract_amount(self, text, amount_type):
        """Extract financial amounts"""
        pattern = rf'(?:{amount_type})\s*[:\s]*(\d+[,\.]\d{{2}})'
        match = re.search(pattern, text, re.IGNORECASE)
        if match:
            return float(match.group(1).replace(',', '.'))
        return 0.0
    
    def _calculate_confidence(self, text):
        """Calculate OCR confidence score"""
        # Simple confidence based on field extraction success
        score = 0.7  # Base score
        if self._extract_invoice_number(text):
            score += 0.1
        if self._extract_date(text):
            score += 0.1
        if self._extract_items(text):
            score += 0.1
        return min(score, 1.0)
    
    def _parse_date(self, date_str):
        """Parse date string to ISO format"""
        for fmt in ['%d/%m/%Y', '%d-%m-%Y', '%d.%m.%Y', '%Y-%m-%d']:
            try:
                return datetime.strptime(date_str, fmt).isoformat()
            except:
                pass
        return None
    
    def validate_extraction(self, extracted_data):
        """Validate extracted data for consistency"""
        validation_errors = []
        
        if not extracted_data.get('invoice_number'):
            validation_errors.append('Missing invoice number')
        
        if extracted_data.get('confidence', 0) < self.confidence_threshold:
            validation_errors.append(f"Low confidence: {extracted_data['confidence']:.0%}")
        
        # Validate amounts
        if extracted_data.get('amount_ht') and extracted_data.get('tva') and extracted_data.get('amount_ttc'):
            expected_ttc = extracted_data['amount_ht'] + extracted_data['tva']
            if abs(expected_ttc - extracted_data['amount_ttc']) > 0.01:
                validation_errors.append('Amount inconsistency')
        
        return {
            'valid': len(validation_errors) == 0,
            'errors': validation_errors,
            'confidence': extracted_data['confidence']
        }


# Singleton instance
ocr_processor = OCRProcessor()


def process_invoice_text(text):
    """Process invoice text and return extracted data"""
    extracted_data = ocr_processor.extract_invoice_data(text)
    validation = ocr_processor.validate_extraction(extracted_data)
    
    return {
        'data': extracted_data,
        'validation': validation,
        'status': 'success' if validation['valid'] else 'requires_review'
    }
