package com.documentanalyzer.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ChatController.class)
class ChatControllerTest {

    @TestConfiguration
    static class ChatClientBuilderConfig {
        // The builder is created before the controller, so it has to be stubbed at bean-creation time.
        @Bean
        ChatClient.Builder chatClientBuilder(ChatClient chatClient) {
            ChatClient.Builder builder = org.mockito.Mockito.mock(ChatClient.Builder.class);
            when(builder.defaultAdvisors(any(Advisor[].class))).thenReturn(builder);
            when(builder.build()).thenReturn(chatClient);
            return builder;
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ChatClient.Builder builder;

    @MockitoBean(answers = Answers.RETURNS_DEEP_STUBS)
    private ChatClient chatClient;

    @MockitoBean
    private VectorStore vectorStore;

    @BeforeEach
    void stubModel() {
        when(chatClient.prompt().user("Where do Liverpool play?").call().content())
                .thenReturn("Liverpool play at Anfield.");
    }

    @Test
    void answersQuestionUsingChatClient() throws Exception {
        mockMvc.perform(get("/api/ask").param("query", "Where do Liverpool play?"))
                .andExpect(status().isOk())
                .andExpect(content().string("Liverpool play at Anfield."));
    }

    @Test
    void registersRetrievalAdvisorSoEveryQuestionSearchesTheDocument() {
        ArgumentCaptor<Advisor[]> advisors = ArgumentCaptor.forClass(Advisor[].class);
        verify(builder).defaultAdvisors(advisors.capture());

        assertThat(advisors.getValue()).singleElement().isInstanceOf(QuestionAnswerAdvisor.class);
    }

    @Test
    void missingQueryParameterIsBadRequest() throws Exception {
        mockMvc.perform(get("/api/ask"))
                .andExpect(status().isBadRequest());
    }
}
