package io.github.susimsek.springaisamples.controller.function;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.github.susimsek.springaisamples.config.LocaleConfig;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CityController.class)
@Import(LocaleConfig.class)
class CityControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ChatClient chatClient;

    @Test
    void testCityFaq() throws Exception {
        String userMessage = "Tell me about Paris";
        String expectedResponse = "Paris is the capital of France.";

        // Mocking the Generation
        Generation generation = new Generation(expectedResponse);

        // Mocking the ChatResponse
        ChatResponse mockResponse = new ChatResponse(List.of(generation));

        // Stubbing the chatClient call
        when(chatClient.call(any(Prompt.class))).thenReturn(mockResponse);

        mockMvc.perform(get("/api/v1/ai/cities")
                .param("message", userMessage))
            .andExpect(status().isOk())
            .andExpect(content().string(expectedResponse));
    }

    @Test
    void testCityFaqInvalidInput() throws Exception {
        mockMvc.perform(get("/api/v1/ai/cities")
                .param("message", ""))
            .andExpect(status().isBadRequest());
    }
}