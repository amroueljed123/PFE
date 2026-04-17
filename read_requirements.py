from docx import Document

doc = Document(r'c:\Users\amrou\OneDrive\Desktop\PFE\app\cahier_de_charge (3).docx')
for paragraph in doc.paragraphs:
    if paragraph.text.strip():
        print(paragraph.text)
