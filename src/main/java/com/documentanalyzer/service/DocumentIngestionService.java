package com.documentanalyzer.service;

import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

@Service
public class DocumentIngestionService {

    private final VectorStore vectorStore;
    private final Resource pdfResource;

    public DocumentIngestionService(VectorStore vectorStore,
                                    @Value("${app.document.location:classpath:docs/Document-Analyzer.pdf}") Resource pdfResource) {
        this.vectorStore = vectorStore;
        this.pdfResource = pdfResource;
    }

    @PostConstruct
    public void ingestDocument() {
        // 1. Extract: Read the PDF
        TikaDocumentReader reader = new TikaDocumentReader(pdfResource);
        var documents = reader.get();

        // 2. Transform: Split into 500-token chunks with 100-token overlap
        TokenTextSplitter splitter = new TokenTextSplitter(500, 100, 5, 10000, true);
        var splitDocuments = splitter.apply(documents);

        // 3. Load: Generate embeddings and save to the store
        vectorStore.add(splitDocuments);
    }
}
