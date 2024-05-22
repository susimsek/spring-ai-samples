package io.github.susmisek.springaisamples.controller.rag;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
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
public class FaqController {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    @Value("classpath:/prompts/rag-prompt-template.st")
    private Resource ragPromptTemplate;

    @GetMapping("/faq")
    @Operation(summary = "Get FAQ answer", description = "Get the answer to a frequently asked question.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful operation",
            content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE,
                schema = @Schema(implementation = String.class),
                examples = @ExampleObject(
                    value = "The Olympic Games Paris 2024 will take place in France from 26 July to 11 August."
                ))),
        @ApiResponse(responseCode = "400", description = "Invalid input",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<String> faq(
        @Parameter(description = "The question you want to ask.",
            example = "How can I buy tickets for the Olympic Games Paris 2024", required = true)
        @NotBlank(message = "Question cannot be blank")
        @Size(min = 5, max = 255, message = "Question must be between 5 and 255 characters")
        @RequestParam(value = "message",
        defaultValue = "How can I buy tickets for the Olympic Games Paris 2024") String message) {
        List<String> contentList = searchSimilarDocuments(message);
        String response = generateResponse(message, contentList);
        return ResponseEntity.ok(response);
    }

    private List<String> searchSimilarDocuments(String message) {
        return vectorStore.similaritySearch(SearchRequest.query(message).withTopK(2))
            .stream()
            .map(Document::getContent)
            .toList();
    }

    private String generateResponse(String message, List<String> contentList) {
        PromptTemplate promptTemplate = new PromptTemplate(ragPromptTemplate);
        Map<String, Object> promptParameters = new HashMap<>();
        promptParameters.put("input", message);
        promptParameters.put("documents", String.join("\n", contentList));
        Prompt prompt = promptTemplate.create(promptParameters);

        return chatClient.call(prompt).getResult().getOutput().getContent();
    }

}