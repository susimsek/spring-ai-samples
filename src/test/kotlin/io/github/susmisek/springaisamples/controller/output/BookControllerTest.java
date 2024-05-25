package io.github.susmisek.springaisamples.controller.output;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import io.github.susmisek.springaisamples.config.LocaleConfig;
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

@WebMvcTest(BookController.class)
@Import(LocaleConfig.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChatClient chatClient;

    @Test
    void whenGetBooksByCraig_thenReturnTextPlain() throws Exception {
        // Given
        String output = "Book 1, Book 2, Book 3";
        when(chatClient.call(any(Prompt.class))).thenReturn(new ChatResponse(List.of(new Generation(output))));

        // When
        mockMvc.perform(MockMvcRequestBuilders.get("/api/ai/books/craig")
                .contentType(MediaType.TEXT_PLAIN))

            // Then
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
            .andExpect(MockMvcResultMatchers.content().string(output));
    }

    @Test
    void whenGetBooksByAuthor_thenReturnJson() throws Exception {
        // Given
        String author = "John Doe";
        String output = "{\"author\":\"John Doe\",\"books\":[\"Book 1\",\"Book 2\",\"Book 3\"]}";
        when(chatClient.call(any(Prompt.class))).thenReturn(new ChatResponse(List.of
            (new Generation(output))));

        // When
        mockMvc.perform(MockMvcRequestBuilders.get("/api/ai/books/by-author")
                .param("author", author)
                .contentType(MediaType.APPLICATION_JSON))

            // Then
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$.author", is(author))) // Burada $.name yerine $.author kullanıldı
            .andExpect(MockMvcResultMatchers.jsonPath("$.books", hasSize(3)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.books", contains("Book 1", "Book 2", "Book 3")));
    }

    @Test
    void testGetBooksByAuthor_WithInvalidAuthorName_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/ai/books/by-author")
                .param("author", "1"))
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON));
    }

    @Test
    void whenGetAuthorLinks_thenReturnJson() throws Exception {
        // Given
        String author = "John Doe";
        String output = "{\"John Doe\": {\"github\": \"http://www.example.com\", \"twitter\": \"@example\"}}";
        when(chatClient.call(any(Prompt.class))).thenReturn(new ChatResponse(List.of(new Generation(output))));

        // When
        mockMvc.perform(MockMvcRequestBuilders.get("/api/ai/books/author/{author}", author)
                .contentType(MediaType.APPLICATION_JSON))

            // Then
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$['John Doe'].github", is("http://www.example.com")))
            .andExpect(MockMvcResultMatchers.jsonPath("$['John Doe'].twitter", is("@example")));
    }
}
