package com.documentanalyzer;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.documentanalyzer.controller.ChatController;
import com.documentanalyzer.service.DocumentIngestionService;

/**
 * Boots the full application context with the vector store mocked, so startup ingestion
 * runs without calling Ollama.
 */
@SpringBootTest
class DocumentAnalyzerApplicationTests {

	@MockitoBean
	private VectorStore vectorStore;

	@Autowired
	private ChatController chatController;

	@Autowired
	private DocumentIngestionService ingestionService;

	@Test
	void contextLoads() {
		assertThat(chatController).isNotNull();
		assertThat(ingestionService).isNotNull();
	}

}
