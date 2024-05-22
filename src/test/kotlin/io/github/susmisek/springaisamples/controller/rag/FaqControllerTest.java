package io.github.susmisek.springaisamples.controller.rag;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(FaqController.class)
class FaqControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChatClient chatClient;

    @MockBean
    private VectorStore vectorStore;

    @MockBean
    private Resource ragPromptTemplate;

    @Test
    void faq_ReturnsFAQAnswer_WhenValidMessageProvided() throws Exception {
        // Given
        String message = "How can I buy tickets for the Olympic Games Paris 2024";
        String expectedAnswer = "The Olympic Games Paris 2024 will take place in France from 26 July to 11 August.";
        List<Document> similarDocuments = List.of(new Document(expectedAnswer));
        ChatResponse chatResponse = new ChatResponse(List.of(new Generation(expectedAnswer)));

        InputStream mockInputStream = new ByteArrayInputStream("Mock prompt content".getBytes());
        when(ragPromptTemplate.getInputStream()).thenReturn(mockInputStream);
        when(vectorStore.similaritySearch(any(SearchRequest.class))).thenReturn(similarDocuments);
        when(chatClient.call(any(Prompt.class))).thenReturn(chatResponse);

        // When/Then
        mockMvc.perform(get("/faq")
                .param("message", message))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
            .andExpect(content().string(expectedAnswer));

        verify(vectorStore, times(1)).similaritySearch(any(SearchRequest.class));
        verify(chatClient, times(1)).call(any(Prompt.class));
    }


    @Test
    void faq_ReturnsInternalServerError_WhenExceptionThrown() throws Exception {
        // Given
        String message = "How can I buy tickets for the Olympic Games Paris 2024";

        when(vectorStore.similaritySearch(any(SearchRequest.class))).thenThrow(new RuntimeException("SimSearch failed"));

        // When/Then
        mockMvc.perform(get("/faq")
                .param("message", message))
            .andExpect(status().isInternalServerError());

        verify(vectorStore, times(1)).similaritySearch(any(SearchRequest.class));
        verifyNoInteractions(chatClient);
    }

    @Test
    void faq_ReturnsBadRequest_WhenBlankMessageProvided() throws Exception {
        // When/Then
        mockMvc.perform(get("/faq")
                .param("message", "1"))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON));

        verifyNoInteractions(vectorStore, chatClient);
    }
}
