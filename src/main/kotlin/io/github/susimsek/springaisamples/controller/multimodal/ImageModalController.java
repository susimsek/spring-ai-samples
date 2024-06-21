package io.github.susimsek.springaisamples.controller.multimodal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.messages.Media;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "ai", description = "Spring AI related APIS")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/ai/images")
@Validated
public class ImageModalController {

    private final ChatClient chatClient;

    @Operation(summary = "Describe Image", description = "Describe the content of a given image.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully described image",
            content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE,
                schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "403", description = "Forbidden",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "429", description = "Too Many Requests",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/image-describe")
    public ResponseEntity<String> describeImage() throws IOException {
        String responseContent = getImageDescription(
            "/images/macbook.jpg",
            "Can you please explain what you see in the following image?",
            MimeTypeUtils.IMAGE_JPEG
        );
        return ResponseEntity.ok(responseContent);
    }

    @Operation(summary = "Describe Code Image", description = "Provide a description of the code shown in an image.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully described code image",
            content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE,
                schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "429", description = "Too Many Requests",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/code-describe")
    public ResponseEntity<String> describeCode() throws IOException {
        String responseContent = getImageDescription(
            "/images/code.png",
            "The following is a screenshot of some code. "
                + "Can you do your best to provide a description of what this code does?",
            MimeTypeUtils.IMAGE_PNG
        );
        return ResponseEntity.ok(responseContent);
    }

    @Operation(summary = "Translate Image to Code", description = "Translate code shown in an image into text.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully translated image to code",
            content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE,
                schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "403", description = "Forbidden",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "429", description = "Too Many Requests",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/image-to-code")
    public ResponseEntity<String> imageToCode() throws IOException {
        String responseContent = getImageDescription(
            "/images/code.png",
            "The following is a screenshot of some code. Can you translate this from the image into text?",
            MimeTypeUtils.IMAGE_PNG
        );
        return ResponseEntity.ok(responseContent);
    }

    private String getImageDescription(String imagePath, String prompt, MimeType mimeType) throws IOException {
        byte[] imageData = new ClassPathResource(imagePath).getContentAsByteArray();
        UserMessage userMessage = new UserMessage(prompt, List.of(new Media(mimeType, imageData)));
        ChatResponse response = chatClient.call(new Prompt(userMessage));
        return response.getResult().getOutput().getContent();
    }
}