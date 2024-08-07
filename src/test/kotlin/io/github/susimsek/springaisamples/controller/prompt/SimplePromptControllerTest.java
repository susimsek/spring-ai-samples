package io.github.susimsek.springaisamples.controller.prompt;

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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(SimplePromptController.class)
@Import(LocaleConfig.class)
class SimplePromptControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChatClient chatClient;

    @Test
    void testGetSimplePrompt() throws Exception {
        String expectedResponse = "Java has been around since 1995.";
        ChatResponse chatResponse = new ChatResponse(List.of(new Generation(expectedResponse)));

        when(chatClient.call(any(Prompt.class))).thenReturn(chatResponse);

        mockMvc.perform(get("/api/v1/ai/simple-prompt")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string(expectedResponse));
    }
}
