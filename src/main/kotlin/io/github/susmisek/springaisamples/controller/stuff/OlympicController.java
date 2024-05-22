package io.github.susmisek.springaisamples.controller.stuff;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
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
@RequestMapping("/api/ai/olympics")
@RequiredArgsConstructor
@Tag(name = "ai", description = "Spring AI Sample Rest Apis")
@Validated
public class OlympicController {

    private final ChatClient chatClient;
    @Value("classpath:/docs/olympic-sports.txt")
    private Resource docsToStuffResource;
    @Value("classpath:/prompts/olympic-sports.st")
    private Resource olympicSportsResource;

    @Operation(summary = "Get 2024 Olympic Sports",
        description = "Returns information about the sports included in the 2024 Summer Olympics.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful operation",
            content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE,
                schema = @Schema(implementation = String.class),
                examples = @ExampleObject(value = "Athletics, Swimming, Gymnastics"))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/2024")
    public ResponseEntity<String> get2024OlympicSports(
        @Parameter(description = "Message to be sent to the chat client",
            example = "What sports are being included in the 2024 Summer Olympics?")
        @RequestParam(value = "message", defaultValue = "What sports are being included in the 2024 Summer Olympics?")
        @NotBlank(message = "Message cannot be blank")
        @Size(min = 2, max = 100, message = "Message size must be between 2 and 100 characters")
        String message,
        @Parameter(description = "Flag to indicate if additional context should be included",
            example = "false", in = ParameterIn.QUERY)
        @RequestParam(value = "stuffit", defaultValue = "false")
        @NotNull(message = "Stuffit cannot be null") boolean stuffit
    ) {
        PromptTemplate promptTemplate = new PromptTemplate(olympicSportsResource);
        Map<String, Object> map = new HashMap<>();
        map.put("question", message);
        map.put("context", stuffit ? docsToStuffResource : "");
        Prompt prompt = promptTemplate.create(map);
        ChatResponse response = chatClient.call(prompt);
        return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body(
            response.getResult().getOutput().getContent());
    }
}
