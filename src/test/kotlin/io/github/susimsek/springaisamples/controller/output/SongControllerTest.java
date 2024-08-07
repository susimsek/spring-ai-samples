package io.github.susimsek.springaisamples.controller.output;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(SongController.class)
@Import(LocaleConfig.class)
class SongControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChatClient chatClient;

    @Test
    void whenGetSongsByArtistAsList_thenReturnJsonArray() throws Exception {
        // Given
        String artist = "Taylor Swift";
        Generation generation = new Generation("output");
        ChatResponse chatResponse = new ChatResponse(List.of(generation));

        when(chatClient.call(any(Prompt.class))).thenReturn(chatResponse);

        // When
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/ai/songs-as-list")
                .param("artist", artist)
                .contentType(MediaType.APPLICATION_JSON))

            // Then
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0]", is("output")));
    }

    @Test
    void testGetSongsByArtist_WithShortArtistName_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/ai/songs")
                .param("artist", "A"))
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON));
    }

    @Test
    void whenGetSongsByArtist_thenReturnTextPlain() throws Exception {
        // Given
        String artist = "Taylor Swift";
        String songs = "Song 1, Song 2, Song 3";
        Generation generation = new Generation(songs);
        ChatResponse chatResponse = new ChatResponse(List.of(generation));

        when(chatClient.call(any(Prompt.class))).thenReturn(chatResponse);

        // When
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/ai/songs")
                .param("artist", artist)
                .contentType(MediaType.TEXT_PLAIN))

            // Then
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
            .andExpect(MockMvcResultMatchers.content().string(songs));
    }
}
