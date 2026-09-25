package com.documentanalyzer.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;

class VectorStoreConfigTest {

    private final VectorStoreConfig config = new VectorStoreConfig();
    private EmbeddingModel embeddingModel;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        // A fake embedding model so no Ollama instance is needed.
        embeddingModel = mock(EmbeddingModel.class);
        when(embeddingModel.embed(anyList(), any(), any())).thenReturn(List.of(new float[] {1f, 0f}));
        when(embeddingModel.embed(anyString())).thenReturn(new float[] {1f, 0f});
        when(embeddingModel.embed(any(Document.class))).thenReturn(new float[] {1f, 0f});
    }

    @Test
    void startsEmptyWhenNoSavedStoreExists() {
        VectorStore store = config.vectorStore(embeddingModel, tempDir.resolve("missing.json").toString());

        List<Document> results = store.similaritySearch(SearchRequest.builder().query("anything").build());

        assertThat(results).isEmpty();
    }

    @Test
    void loadsPreviouslySavedStoreFromDisk() {
        File file = tempDir.resolve("store.json").toFile();
        SimpleVectorStore original = SimpleVectorStore.builder(embeddingModel).build();
        original.add(List.of(new Document("Liverpool play at Anfield.")));
        original.save(file);

        VectorStore loaded = config.vectorStore(embeddingModel, file.getPath());

        List<Document> results = loaded.similaritySearch(SearchRequest.builder().query("stadium").topK(1).build());
        assertThat(results).extracting(Document::getText).containsExactly("Liverpool play at Anfield.");
    }
}
