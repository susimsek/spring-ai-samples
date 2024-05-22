package io.github.susmisek.springaisamples.controller.stuff;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(OlympicController.class)
class OlympicControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChatClient chatClient;

    @MockBean(name = "docsToStuffResource")
    private Resource docsToStuffResource;

    @MockBean(name = "olympicSportsResource")
    private Resource olympicSportsResource;

    @Test
    void testGet2024OlympicSports_ValidRequest() throws Exception {
        // Mock chatClient response
        ChatResponse mockResponse = new ChatResponse(Collections.singletonList(new Generation("Athletics, Swimming, Gymnastics")));
        when(chatClient.call(any(Prompt.class))).thenReturn(mockResponse);

        // Mock Resource objects with ByteArrayInputStream
        String content = "This is a sample text.";
        InputStream inputStream = new ByteArrayInputStream(content.getBytes());
        when(docsToStuffResource.getInputStream()).thenReturn(inputStream);
        when(olympicSportsResource.getInputStream()).thenReturn(inputStream);

        mockMvc.perform(get("/api/ai/olympics/2024"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
            .andExpect(content().string("Athletics, Swimming, Gymnastics"));
    }

    @Test
    void testGet2024OlympicSports_InvalidRequest() throws Exception {
        mockMvc.perform(get("/api/ai/olympics/invalidUrl"))
            .andExpect(status().isNotFound());
    }

    @Test
    void testGet2024OlympicSports_WithContent() throws Exception {
        // Mock chatClient response
        ChatResponse mockResponse = new ChatResponse(Collections.singletonList(new Generation("Test Response")));
        when(chatClient.call(any(Prompt.class))).thenReturn(mockResponse);

        // Mock Resource objects with ByteArrayInputStream
        String content = "This is a sample text.";
        InputStream inputStream = new ByteArrayInputStream(content.getBytes());
        when(docsToStuffResource.getInputStream()).thenReturn(inputStream);
        when(olympicSportsResource.getInputStream()).thenReturn(inputStream);

        mockMvc.perform(get("/api/ai/olympics/2024")
                .param("message", "What sports are being included in the 2024 Summer Olympics?")
                .param("stuffit", "true"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
            .andExpect(content().string("Test Response"));
    }

    @Test
    void testGet2024OlympicSports_WithShortMessage_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/ai/olympics/2024")
                .param("message", "a")
                .param("stuffit", "true"))
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON));
    }
}
