package io.github.susmisek.springaisamples.controller.output;

import static io.github.susmisek.springaisamples.utils.ChatUtils.FORMAT;

import io.github.susmisek.springaisamples.model.Author;
import io.github.susmisek.springaisamples.utils.ChatUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.parser.BeanOutputParser;
import org.springframework.ai.parser.MapOutputParser;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "ai", description = "Spring AI Sample Rest Apis")
@RequestMapping("/api/ai/books")
@Validated
public class BookController {

    private final ChatClient chatClient;
    private static final String AUTHOR = "author";
    private static final String DEFAULT_AUTHOR = "Ken Kousen";
    private static final String BOOK_MESSAGE_TEMPLATE = """
            Generate a list of books written by the author {author}. If you aren't positive that a book
            belongs to this author please don't include it.
            {format}
            """;
    private static final String LINKS_MESSAGE_TEMPLATE = """
            Generate a list of books written by the author {author}. If you aren't positive that a book
            belongs to this author please don't include it.
            {format}
            """;


    @Operation(summary = "Get books by Craig Walls", description = "Generate a list of books written by Craig Walls.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list of books",
            content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(implementation = String.class),
                examples = @ExampleObject(value = "Book 1, Book 2, Book 3"))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/craig")
    public String getBooksByCraig() {
        String message = BOOK_MESSAGE_TEMPLATE.replace("{format}", "");
        Map<String, Object> model = Map.of(AUTHOR, "Craig Walls");
        return chatClient.call(ChatUtils.buildPrompt(message, model))
            .getResult().getOutput().getContent();
    }

    @Operation(summary = "Get books by author",
        description = "Generate a list of books written by the specified author.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list of books",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = Author.class))),
        @ApiResponse(responseCode = "400", description = "Invalid author name",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/by-author")
    public Author getBooksByAuthor(
        @Parameter(description = "Name of the author", example = "Craig Walls")
        @RequestParam(value = AUTHOR, defaultValue = DEFAULT_AUTHOR)
        @NotBlank(message = "Author name must not be blank")
        @Size(min = 2, max = 50, message = "Author name must be between 2 and 50 characters")
        String author) {
        var outputParser = new BeanOutputParser<>(Author.class);
        String format = outputParser.getFormat();
        Map<String, Object> model = Map.of(AUTHOR, author, FORMAT, format);
        Generation generation = chatClient.call(ChatUtils.buildPrompt(BOOK_MESSAGE_TEMPLATE, model)).getResult();
        return ChatUtils.parseResponse(generation, outputParser);
    }

    @Operation(summary = "Get author links",
        description = "Generate a list of links for the specified author, including social network links.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved author links",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = Map.class), examples = @ExampleObject
                (value = "{\"John Doe\": {\"github\": \"http://www.example.com\", \"twitter\": \"@example\"}}"))),
        @ApiResponse(responseCode = "400", description = "Invalid author name",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/author/{author}")
    public Map<String, Object> getAuthorLinks(
        @PathVariable
        @NotBlank(message = "Author name must not be blank")
        @Size(min = 2, max = 50, message = "Author name must be between 2 and 50 characters")
        String author) {
        MapOutputParser outputParser = new MapOutputParser();
        String format = outputParser.getFormat();

        PromptTemplate promptTemplate = new PromptTemplate(LINKS_MESSAGE_TEMPLATE,
            Map.of(AUTHOR, author, FORMAT, format));
        Prompt prompt = promptTemplate.create();
        Generation generation = chatClient.call(prompt).getResult();
        return ChatUtils.parseResponse(generation, outputParser);
    }
}
