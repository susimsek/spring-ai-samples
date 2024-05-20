package io.github.susmisek.springaisamples.controller.output;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.parser.ListOutputParser;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "ai", description = "Spring AI Sample Rest Apis")
public class SongController {

    private final ChatClient chatClient;

    private static final String DEFAULT_ARTIST = "Taylor Swift";
    private static final String SONGS_MESSAGE_TEMPLATE = """
        Please give me a list of top 10 songs for the artist {artist}.
        If you don't know the answer, just say "I don't know".
        {format}
        """;

    @Operation(summary = "Get top songs by artist as a list",
        description = "Returns a list of top 10 songs by the specified artist.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful operation",
            content = @Content(mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = String.class)),
                examples = @ExampleObject(value = "[\"Song 1\",\"Song 2\",\"Song 3\"]"))),
        @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/api/ai/songs-as-list")
    public List<String> getSongsByArtistAsList(
        @Parameter(description = "Name of the artist", example = "Taylor Swift")
        @RequestParam(value = "artist", defaultValue = DEFAULT_ARTIST) String artist) {
        ListOutputParser outputParser = new ListOutputParser(new DefaultConversionService());
        PromptTemplate promptTemplate =
            new PromptTemplate(SONGS_MESSAGE_TEMPLATE,
                Map.of("artist", artist, "format", outputParser.getFormat()));
        Prompt prompt = promptTemplate.create();
        ChatResponse response = chatClient.call(prompt);
        return outputParser.parse(response.getResult().getOutput().getContent());
    }

    @Operation(summary = "Get top songs by artist as a string",
        description = "Returns a string containing the top 10 songs by the specified artist.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful operation",
            content = @Content(mediaType = "text/plain",
                schema = @Schema(implementation = String.class),
                    examples = @ExampleObject(value = "Song 1, Song 2, Song 3"))),
        @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping("/api/ai/songs")
    public String getSongsByArtist(
        @Parameter(description = "Name of the artist", example = "Taylor Swift")
        @RequestParam(value = "artist", defaultValue = DEFAULT_ARTIST) String artist) {
        String message = SONGS_MESSAGE_TEMPLATE.replace("{format}", "");
        PromptTemplate promptTemplate = new PromptTemplate(message, Map.of("artist", artist));
        Prompt prompt = promptTemplate.create();
        ChatResponse response = chatClient.call(prompt);
        return response.getResult().getOutput().getContent();
    }

}