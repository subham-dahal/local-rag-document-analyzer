package com.example.Document.Anaylzer;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
public class VectorStoreConfig {

    @Bean
    public VectorStore vectorStore(EmbeddingModel embeddingModel, 
                                   @Value("${app.vector-store.path:./rag-vectorstore.json}") String storePath) {
        SimpleVectorStore store = SimpleVectorStore.builder(embeddingModel).build();
        
        File file = new File(storePath);
        if (file.exists()) {
            store.load(file);
        }
        return store;
    }
}