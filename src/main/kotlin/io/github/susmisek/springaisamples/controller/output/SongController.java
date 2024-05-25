package io.github.susmisek.springaisamples.controller.output;

import static io.github.susmisek.springaisamples.utils.ChatUtils.FORMAT;

import io.github.susmisek.springaisamples.utils.ChatUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.parser.ListOutputParser;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "ai", description = "Spring AI Sample Rest Apis")
@Validated
public class SongController {

    private final ChatClient chatClient;
    private static final String ARTIST = "artist";
    private static final String DEFAULT_ARTIST = "Taylor Swift";
    private static final String ARTIST_PATTERN = "^[a-zA-Z0-9_@\\s-]*$";
    private static final String SONGS_MESSAGE_TEMPLATE = """
        Please give me a list of top 10 songs for the artist {artist}.
        If you don't know the answer, just say "I don't know".
        {format}
        """;

    @Operation(summary = "Get top songs by artist as a list",
        description = "Returns a list of top 10 songs by the specified artist.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful operation",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                array = @ArraySchema(schema = @Schema(implementation = String.class)),
                examples = @ExampleObject(value = "[\"Song 1\",\"Song 2\",\"Song 3\"]"))),
        @ApiResponse(responseCode = "400", description = "Invalid input",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/api/ai/songs-as-list")
    public List<String> getSongsByArtistAsList(
        @Parameter(description = "Name of the artist", example = "Taylor Swift")
        @RequestParam(value = ARTIST, defaultValue = DEFAULT_ARTIST)
        @NotBlank(message = "{validation.field.notBlank}")
        @Size(min = 2, max = 50, message = "{validation.field.size}")
        @Pattern(regexp = ARTIST_PATTERN, message = "{validation.artist.pattern}")
        String artist) {
        ListOutputParser outputParser = new ListOutputParser(new DefaultConversionService());
        Map<String, Object> model = Map.of(ARTIST, artist, FORMAT, outputParser.getFormat());
        ChatResponse response = chatClient.call(ChatUtils.buildPrompt(SONGS_MESSAGE_TEMPLATE, model));
        return outputParser.parse(response.getResult().getOutput().getContent());
    }

    @Operation(summary = "Get top songs by artist as a string",
        description = "Returns a string containing the top 10 songs by the specified artist.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful operation",
            content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE,
                schema = @Schema(implementation = String.class),
                examples = @ExampleObject(value = "Song 1, Song 2, Song 3"))),
        @ApiResponse(responseCode = "400", description = "Invalid input",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/api/ai/songs")
    public String getSongsByArtist(
        @Parameter(description = "Name of the artist", example = "Taylor Swift")
        @RequestParam(value = ARTIST, defaultValue = DEFAULT_ARTIST)
        @NotBlank(message = "{validation.field.notBlank}")
        @Size(min = 2, max = 50, message = "{validation.field.size}")
        @Pattern(regexp = ARTIST_PATTERN, message = "{validation.artist.pattern}")
        String artist) {
        String message = SONGS_MESSAGE_TEMPLATE.replace("{format}", "");
        Map<String, Object> model = Map.of(ARTIST, artist);
        ChatResponse response = chatClient.call(ChatUtils.buildPrompt(message, model));
        return response.getResult().getOutput().getContent();
    }
}
