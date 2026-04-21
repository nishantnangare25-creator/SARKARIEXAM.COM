import * as pdfjsLib from 'pdfjs-dist';

// Configure the worker correctly for Vite/Browser environments
pdfjsLib.GlobalWorkerOptions.workerSrc = `//cdnjs.cloudflare.com/ajax/libs/pdf.js/${pdfjsLib.version}/pdf.worker.min.mjs`;

export const extractTextFromPdfFile = async (file) => {
  try {
    const arrayBuffer = await file.arrayBuffer();
    const pdf = await pdfjsLib.getDocument({ data: arrayBuffer }).promise;
    
    let fullText = '';
    
    // Extract text from the first 50 pages maximum to prevent browser crashing on massive files
    const maxPages = Math.min(pdf.numPages, 50);
    
    for (let i = 1; i <= maxPages; i++) {
       const page = await pdf.getPage(i);
       const textContent = await page.getTextContent();
       const pageText = textContent.items.map(item => item.str).join(' ');
       fullText += pageText + '\n\n';
    }
    
    return fullText.trim();
  } catch (error) {
    console.error("Error extracting text from PDF:", error);
    throw new Error('Failed to extract text from the provided PDF file. It might be corrupted or secured.');
  }
};
