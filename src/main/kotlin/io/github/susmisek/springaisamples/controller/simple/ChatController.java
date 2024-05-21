package io.github.susmisek.springaisamples.controller.simple;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.ChatClient;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "ai", description = "Spring AI Sample Rest Apis")
@Validated
public class ChatController {

    private final ChatClient chatClient;

    @Operation(summary = "Generate chat response",
        description = "Generates a response from the chat client based on the provided message.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful response",
            content = {@Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(implementation = String.class),
                    examples = {@ExampleObject(value = "Here is a response for the provided message.")})}),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class)))})
    @GetMapping("/api/ai/chat/generate")
    public ResponseEntity<String> generate(
        @Parameter(description = "Message", example = "Tell me a dad joke about dogs")
        @RequestParam(value = "message", defaultValue = "Tell me a dad joke about dogs")
        @NotBlank(message = "Message cannot be blank") String message) {
        var response = chatClient.call(message);
        return ResponseEntity.ok(response);
    }
}
