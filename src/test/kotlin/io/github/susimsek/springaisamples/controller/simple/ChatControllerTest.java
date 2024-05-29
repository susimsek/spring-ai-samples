package io.github.susimsek.springaisamples.controller.simple;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.github.susimsek.springaisamples.config.LocaleConfig;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(ChatController.class)
@Import(LocaleConfig.class)
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChatClient chatClient;

    @Test
    void shouldGenerateChatResponse() throws Exception {
        String message = "Tell me a dad joke about dogs";
        String expectedResponse = "Why was the dog such a good baseball player? Because he was a great catcher!";

        when(chatClient.call(anyString())).thenReturn(expectedResponse);

        mockMvc.perform(get("/api/ai/chat/generate")
                .param("message", message)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string(expectedResponse));
    }

    @Test
    void shouldGenerateChatResponseWithDefaultMessage() throws Exception {
        String expectedResponse = "Why was the dog such a good baseball player? Because he was a great catcher!";

        when(chatClient.call(anyString())).thenReturn(expectedResponse);

        mockMvc.perform(get("/api/ai/chat/generate")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string(expectedResponse));
    }

    @Test
    void testGenerate_WithBlankMessage_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/ai/chat/generate")
                .param("message", "a")
                .contentType(MediaType.TEXT_PLAIN))
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON));
    }
}
