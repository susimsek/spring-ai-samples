package io.github.susmisek.springaisamples.controller.prompt;

import static org.mockito.ArgumentMatchers.any;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(YouTubeController.class)
class YouTubeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChatClient chatClient;

    @MockBean
    private Resource ytPromptResource;

    @Test
    void testFindPopularYouTubersStepOne() throws Exception {
        String expectedResponse = "List 10 of the most popular YouTubers in tech along with their current subscriber counts. If you don't know the answer, just say \"I don't know\".";
        ChatResponse chatResponse = new ChatResponse(List.of(new Generation(expectedResponse)));

        when(chatClient.call(any(Prompt.class))).thenReturn(chatResponse);

        mockMvc.perform(get("/api/ai/youtube/popular-step-one")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string(expectedResponse));
    }

    @Test
    void whenGenreIsTooShort_thenBadRequest() throws Exception {
        mockMvc.perform(get("/api/ai/youtube/popular-step-one")
                .param("genre", "a"))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON));
    }

    @Test
    void testFindPopularYouTubers() throws Exception {
        String expectedResponse = "List 10 of the most popular YouTubers in tech along with their current subscriber counts. If you don't know the answer, just say \"I don't know\".";
        ChatResponse chatResponse = new ChatResponse(List.of(new Generation(expectedResponse)));

        // Mock input stream for ytPromptResource
        InputStream mockInputStream = new ByteArrayInputStream("Mock prompt content".getBytes());
        when(ytPromptResource.getInputStream()).thenReturn(mockInputStream);

        when(chatClient.call(any(Prompt.class))).thenReturn(chatResponse);

        mockMvc.perform(get("/api/ai/youtube/popular")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string(expectedResponse));
    }
}
