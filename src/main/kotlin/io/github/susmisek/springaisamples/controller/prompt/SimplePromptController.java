package io.github.susmisek.springaisamples.controller.prompt;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "ai", description = "Spring AI Sample Rest Apis")
public class SimplePromptController {

    private final ChatClient chatClient;

    @Operation(summary = "Get information about Java",
        description = "Returns information about how long the Java programming language has been around.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful response",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = String.class))})})
    @GetMapping("/api/ai/simple-prompt")
    public ResponseEntity<String> simple() {
        String response = chatClient.call(
                new Prompt("How long has The Java Programming language been around?"))
            .getResult().getOutput().getContent();
        return ResponseEntity.ok(response);
    }
}