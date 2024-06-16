package io.github.susimsek.springaisamples.controller.multimodal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
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
@Tag(name = "ai", description = "Spring AI related APIS")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/ai/chat")
@Validated
public class ChatModalController {

    private final ChatClient chatClient;

    @Operation(summary = "Get a dad joke", description = "Get a dad joke based on the provided topic.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved joke",
            content = @Content(mediaType = "text/plain",
                schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "403", description = "Forbidden",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/dad-jokes")
    public ResponseEntity<String> jokes(
        @RequestParam(value = "topic", defaultValue = "Dogs")
        @NotBlank(message = "{validation.field.notBlank}")
        @Size(min = 2, max = 50, message = "{validation.field.size}")
        String topic) {

        PromptTemplate promptTemplate = new PromptTemplate("Tell me a dad joke about {topic}");
        Prompt prompt = promptTemplate.create(Map.of("topic", topic));
        ChatResponse response = chatClient.call(prompt);
        return ResponseEntity.ok(response.getResult().getOutput().getContent());
    }
}