package com.documentanalyzer.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;

@ExtendWith(MockitoExtension.class)
class DocumentIngestionServiceTest {

    @Mock
    private VectorStore vectorStore;

    @Captor
    private ArgumentCaptor<List<Document>> documentsCaptor;

    @Test
    void extractsTextFromBundledPdfAndStoresIt() {
        var service = new DocumentIngestionService(vectorStore, new ClassPathResource("docs/Document-Analyzer.pdf"));

        service.ingestDocument();

        verify(vectorStore).add(documentsCaptor.capture());
        List<Document> chunks = documentsCaptor.getValue();
        assertThat(chunks).isNotEmpty();
        String allText = String.join(" ", chunks.stream().map(Document::getText).toList());
        assertThat(allText).contains("Liverpool Football Club", "Anfield");
    }

    @Test
    void splitsLongDocumentsIntoOverlappingChunks() {
        String longText = "The quick brown fox jumps over the lazy dog near the river bank. ".repeat(400);
        var resource = new ByteArrayResource(longText.getBytes(StandardCharsets.UTF_8)) {
            @Override
            public String getFilename() {
                return "long.txt";
            }
        };
        var service = new DocumentIngestionService(vectorStore, resource);

        service.ingestDocument();

        verify(vectorStore).add(documentsCaptor.capture());
        List<Document> chunks = documentsCaptor.getValue();
        assertThat(chunks).hasSizeGreaterThan(1);
        assertThat(chunks).allSatisfy(chunk -> assertThat(chunk.getText()).isNotBlank());
    }
}
