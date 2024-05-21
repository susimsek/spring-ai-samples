package io.github.susmisek.springaisamples.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.github.susmisek.springaisamples.model.Author;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.Generation;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.parser.BeanOutputParser;
import org.springframework.ai.parser.MapOutputParser;

class ChatUtilsTest {

    @Test
    void testBuildPrompt() {
        // Given
        String template = "Generate a list of books written by the author {author}.";
        String expectedContents = "Generate a list of books written by the author Craig Walls.";
        Map<String, Object> model = Map.of("author", "Craig Walls");

        // When
        Prompt actualPrompt = ChatUtils.buildPrompt(template, model);

        // Then
        assertEquals(expectedContents, actualPrompt.getContents());
    }

    @Test
    void testParseResponseWithBeanOutputParser() {
        // Given
        String content = "{\"author\": \"Craig Walls\", \"books\": [\"Spring in Action\"]}";
        Generation generation = mock(Generation.class);
        AssistantMessage assistantMessage = new AssistantMessage(content);
        BeanOutputParser<Author> outputParser = new BeanOutputParser<>(Author.class);
        when(generation.getOutput()).thenReturn(assistantMessage);
        Author expectedAuthor = new Author("Craig Walls", List.of("Spring in Action"));

        // When
        Author actualAuthor = ChatUtils.parseResponse(generation, outputParser);

        // Then
        assertEquals(expectedAuthor, actualAuthor);
    }

    @Test
    void testParseResponseWithMapOutputParser() {
        // Given
        String content = "{\"John Doe\": {\"github\": \"http://www.example.com\", \"twitter\": \"@example\"}}";
        Generation generation = mock(Generation.class);
        AssistantMessage assistantMessage = new AssistantMessage(content);
        MapOutputParser outputParser = new MapOutputParser();
        when(generation.getOutput()).thenReturn(assistantMessage);
        Map<String, Object> expectedMap = Map.of("John Doe", Map.of("github", "http://www.example.com", "twitter", "@example"));

        // When
        Map<String, Object> actualMap = ChatUtils.parseResponse(generation, outputParser);

        // Then
        assertEquals(expectedMap, actualMap);
    }
}
