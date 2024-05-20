package io.github.susmisek.springaisamples.controller.prompt;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "ai", description = "Spring AI Sample Rest Apis")
public class DadJokeController {

    private final ChatClient chatClient;

    @Operation(summary = "Get a Dad Joke",
        description = "Returns a dad joke when a request is made to this endpoint.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful response",
            content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class),
                examples = @ExampleObject(value = "Why don't skeletons fight each other? They don't have the guts."))),
        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)})
    @GetMapping("/api/ai/jokes")
    public ResponseEntity<String> jokes() {
        var system = new SystemMessage(
            "You primary function is to tell Dad Jokes. If someone asks you for any other type of joke please tell them you only know Dad Jokes");
        var user = new UserMessage("Tell me a very serious joke about the earth");
        Prompt prompt = new Prompt(List.of(system, user));
        var joke = chatClient.call(prompt).getResult().getOutput().getContent();
        return ResponseEntity.ok(joke);
    }
}