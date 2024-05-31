package io.github.susimsek.springaisamples.controller.multimodal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.github.susimsek.springaisamples.config.LocaleConfig;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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

@WebMvcTest(ImageController.class)
@Import(LocaleConfig.class)
class ImageControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ChatClient chatClient;

    ChatResponse chatResponse;

    @BeforeEach
    void setUp() {
        Generation generation = new Generation("Mocked response");
        chatResponse = new ChatResponse(List.of(generation));
    }

    @ParameterizedTest
    @MethodSource("provideEndpoints")
    void testEndpoints(String url) throws Exception {
        when(chatClient.call(any(Prompt.class))).thenReturn(chatResponse);

        mockMvc.perform(get(url).contentType(MediaType.TEXT_PLAIN))
            .andExpect(status().isOk())
            .andExpect(content().string("Mocked response"));
    }

    private static Stream<Arguments> provideEndpoints() {
        return Stream.of(
            Arguments.of("/api/ai/images/image-describe"),
            Arguments.of("/api/ai/images/code-describe"),
            Arguments.of("/api/ai/images/image-to-code")
        );
    }
}