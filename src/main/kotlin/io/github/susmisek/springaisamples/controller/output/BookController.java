package io.github.susmisek.springaisamples.controller.output;

import io.github.susmisek.springaisamples.model.Author;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.parser.BeanOutputParser;
import org.springframework.ai.parser.MapOutputParser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "ai", description = "Spring AI Sample Rest Apis")
@RequestMapping("/api/ai/books")
public class BookController {

    private final ChatClient chatClient;
    private static final String DEFAULT_AUTHOR = "Ken Kousen";
    private static final String BOOK_MESSAGE_TEMPLATE = "Generate a list of books written by the author {author}.";
    private static final String LINKS_MESSAGE_TEMPLATE = "Generate a list of links for the author {author}.";


    @Operation(summary = "Get books by Craig Walls", description = "Generate a list of books written by Craig Walls.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list of books",
            content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class),
                examples = @ExampleObject(value = "Book 1, Book 2, Book 3"))),
        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/craig")
    public String getBooksByCraig() {
        return chatClient.call(buildPrompt(BOOK_MESSAGE_TEMPLATE, "Craig Walls"))
            .getResult().getOutput().getContent();
    }

    @Operation(summary = "Get books by author",
        description = "Generate a list of books written by the specified author.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list of books",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Author.class))),
        @ApiResponse(responseCode = "400", description = "Invalid author name", content = @Content),
        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/by-author")
    public Author getBooksByAuthor(
        @Parameter(description = "Name of the author", example = "Craig Walls")
        @RequestParam(value = "author", defaultValue = DEFAULT_AUTHOR) String author) {
        String promptMessage = """
            Generate a list of books written by the author {author}. If you aren't positive that a book
            belongs to this author please don't include it.
            {format}
            """;

        var outputParser = new BeanOutputParser<>(Author.class);
        String format = outputParser.getFormat();
        PromptTemplate promptTemplate = new PromptTemplate(promptMessage,
            Map.of("author", author, "format", format));
        Prompt prompt = promptTemplate.create();
        Generation generation = chatClient.call(prompt).getResult();
        return parseResponse(generation, Author.class);
    }

    @Operation(summary = "Get author links",
        description = "Generate a list of links for the specified author, including social network links.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved author links",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class),
                examples = @ExampleObject(value = "{\"John Doe\": {\"github\": \"http://www.example.com\", \"twitter\": \"@example\"}}"))),
        @ApiResponse(responseCode = "400", description = "Invalid author name", content = @Content),
        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/author/{author}")
    public Map<String, Object> getAuthorLinks(@PathVariable String author) {
        MapOutputParser outputParser = new MapOutputParser();
        String format = outputParser.getFormat();

        PromptTemplate promptTemplate = new PromptTemplate(LINKS_MESSAGE_TEMPLATE,
            Map.of("author", author, "format", format));
        Prompt prompt = promptTemplate.create();
        Generation generation = chatClient.call(prompt).getResult();
        return outputParser.parse(generation.getOutput().getContent());
    }

    private Prompt buildPrompt(String template, String author) {
        return new PromptTemplate(template, Map.of("author", author)).create();
    }

    private <T> T parseResponse(Generation generation, Class<T> responseType) {
        return new BeanOutputParser<>(responseType).parse(generation.getOutput().getContent());
    }

}