package io.github.susimsek.springaisamples.controller.multimodal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.github.susimsek.springaisamples.config.LocaleConfig;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ChatModalController.class)
@Import(LocaleConfig.class)
class ChatModalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChatClient chatClient;

    private ChatResponse chatResponse;

    @BeforeEach
    void setUp() {
        Generation generation = new Generation("Mocked response");
        chatResponse = new ChatResponse(List.of(generation));
    }

    @Test
    void testJokesWithDogsTopic() throws Exception {
        when(chatClient.call(any(Prompt.class))).thenReturn(chatResponse);

        mockMvc.perform(get("/api/v1/ai/chat/dad-jokes")
                .param("topic", "Dogs")
                .contentType(MediaType.TEXT_PLAIN))
            .andExpect(status().isOk())
            .andExpect(content().string("Mocked response"));
    }
}