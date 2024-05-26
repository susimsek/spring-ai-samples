package io.github.susmisek.springaisamples.controller.prompt;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "ai", description = "Spring AI Sample Rest Apis")
@RequestMapping("/api/ai/youtube")
@Validated
public class YouTubeController {

    private final ChatClient chatClient;

    @Value("classpath:/prompts/youtube.st")
    private Resource ytPromptResource;

    private static final String GENRE_PATTERN = "^[-a-zA-Z0-9_ ]*$";
    private static final String YOUTUBE_MESSAGE_TEMPLATE = """
            List 10 of the most popular YouTubers in {genre} along with their current subscriber counts.
            If you don't know the answer, just say "I don't know".
        """;

    @Operation(summary = "Find popular YouTubers (Step One)",
        description = "Lists 10 of the most popular YouTubers")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful response",
            content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(implementation = String.class),
                examples = @ExampleObject(value = "John Doe, Jane Smith, Alice Johnson"))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class)))})
    @GetMapping("/popular-step-one")
    public ResponseEntity<String> findPopularYouTubersStepOne(
        @Parameter(description = "The genre of YouTube content", example = "tech")
        @RequestParam(value = "genre", defaultValue = "tech")
        @NotBlank(message = "{validation.field.notBlank}")
        @Size(min = 2, max = 50, message = "{validation.field.size}")
        @Pattern(regexp = GENRE_PATTERN, message = "{validation.genre.pattern}")
        String genre) {
        PromptTemplate promptTemplate = new PromptTemplate(YOUTUBE_MESSAGE_TEMPLATE);
        Prompt prompt = promptTemplate.create(Map.of("genre", genre));
        var response = chatClient.call(prompt).getResult().getOutput().getContent();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Find popular YouTubers",
        description = "Lists 10 of the most popular YouTubers using a prompt template from a resource file.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful response",
            content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(implementation = String.class),
                examples = @ExampleObject(value = "John Doe, Jane Smith, Alice Johnson"))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class)))})
    @GetMapping("/popular")
    public ResponseEntity<String> findPopularYouTubers(
        @Parameter(description = "The genre of YouTube content", example = "tech")
        @RequestParam(value = "genre", defaultValue = "tech")
        @NotBlank(message = "Genre must not be blank")
        @Size(min = 2, max = 50, message = "Genre must be between 2 and 50 characters")
        @Pattern(regexp = GENRE_PATTERN, message = "{validation.genre.pattern}")
        String genre) {
        PromptTemplate promptTemplate = new PromptTemplate(ytPromptResource);
        Prompt prompt = promptTemplate.create(Map.of("genre", genre));
        var response = chatClient.call(prompt).getResult().getOutput().getContent();
        return ResponseEntity.ok(response);
    }
}