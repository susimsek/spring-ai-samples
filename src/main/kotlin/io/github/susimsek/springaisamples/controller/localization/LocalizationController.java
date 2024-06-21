package io.github.susimsek.springaisamples.controller.localization;

import io.github.susimsek.springaisamples.i18n.ParameterMessageSource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Locale;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "localization", description = "Localization related APIs")
@RequestMapping("/api/locales")
@RequiredArgsConstructor
public class LocalizationController {

    private final ParameterMessageSource messageSource;

    @Cacheable(value = "translationsCache", key = "#root.method.name + '_' "
        + "+ T(org.springframework.context.i18n.LocaleContextHolder).getLocale().toString()")
    @Operation(summary = "{api-docs.api.localization.summary}",
        description = "{api-docs.api.localization.description}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved localization information",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "429", description = "Too Many Requests",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping
    public ResponseEntity<Map<String, String>> getTranslations() {
        Locale locale = LocaleContextHolder.getLocale();
        Map<String, String> messages = messageSource.getMessagesStartingWith("api-docs", locale);
        return ResponseEntity.ok(messages);
    }
}