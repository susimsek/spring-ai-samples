package io.github.susimsek.springaisamples.controller.prompt;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "ai", description = "Spring AI Sample Rest Apis")
@SecurityRequirement(name = "bearerAuth")
public class SimplePromptController {

    private final ChatClient chatClient;

    @Operation(summary = "Get information about Java",
        description = "Returns information about how long the Java programming language has been around.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful response",
            content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(implementation = String.class),
                examples = @ExampleObject(value = "Java has been around since 1995."))),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "403", description = "Forbidden",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class)))})
    @GetMapping("/api/ai/simple-prompt")
    public ResponseEntity<String> simple() {
        String response = chatClient.call(
                new Prompt("How long has The Java Programming language been around?"))
            .getResult().getOutput().getContent();
        return ResponseEntity.ok(response);
    }
}