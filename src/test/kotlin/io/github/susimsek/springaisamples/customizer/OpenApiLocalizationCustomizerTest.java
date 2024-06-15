package io.github.susimsek.springaisamples.customizer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import io.github.susimsek.springaisamples.openapi.LocalizedOpenApiCustomizer;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Info;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

@ExtendWith(MockitoExtension.class)
class OpenApiLocalizationCustomizerTest {

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private LocalizedOpenApiCustomizer customizer;

    @BeforeEach
    void setUp() {
        LocaleContextHolder.setLocale(Locale.ENGLISH);
    }

    @Test
    void testCustomizeOpenApiInfo() {
        OpenAPI openApi = new OpenAPI();
        openApi.info(new Info());

        when(messageSource.getMessage("api-docs.title", null, Locale.ENGLISH))
            .thenReturn("API Documentation Title");
        when(messageSource.getMessage("api-docs.description", null, Locale.ENGLISH))
            .thenReturn("API Documentation Description");
        when(messageSource.getMessage("api-docs.contact.name", null, Locale.ENGLISH))
            .thenReturn("Contact Name");
        when(messageSource.getMessage("api-docs.contact.email", null, Locale.ENGLISH))
            .thenReturn("contact@example.com");
        when(messageSource.getMessage("api-docs.contact.url", null, Locale.ENGLISH))
            .thenReturn("https://contact.example.com");
        when(messageSource.getMessage("api-docs.license.name", null, Locale.ENGLISH))
            .thenReturn("License Name");
        when(messageSource.getMessage("api-docs.license.url", null, Locale.ENGLISH))
            .thenReturn("https://license.example.com");

        customizer.customise(openApi);

        Info info = openApi.getInfo();
        assertEquals("API Documentation Title", info.getTitle());
        assertEquals("API Documentation Description", info.getDescription());
        assertEquals("Contact Name", info.getContact().getName());
        assertEquals("contact@example.com", info.getContact().getEmail());
        assertEquals("https://contact.example.com", info.getContact().getUrl());
        assertEquals("License Name", info.getLicense().getName());
        assertEquals("https://license.example.com", info.getLicense().getUrl());
    }

    @Test
    void testCustomizeOperations() {
        OpenAPI openApi = new OpenAPI();
        Paths paths = new Paths();
        PathItem pathItem = new PathItem();
        Operation operation = new Operation();
        operation.setSummary("{operation.summary}");
        operation.setDescription("{operation.description}");
        pathItem.setGet(operation);
        paths.addPathItem("/test", pathItem);
        openApi.setPaths(paths);
        openApi.info(new Info());

        when(messageSource.getMessage("api-docs.title", null, Locale.ENGLISH))
            .thenReturn("API Documentation Title");
        when(messageSource.getMessage("api-docs.description", null, Locale.ENGLISH))
            .thenReturn("API Documentation Description");
        when(messageSource.getMessage("api-docs.contact.name", null, Locale.ENGLISH))
            .thenReturn("Contact Name");
        when(messageSource.getMessage("api-docs.contact.email", null, Locale.ENGLISH))
            .thenReturn("contact@example.com");
        when(messageSource.getMessage("api-docs.contact.url", null, Locale.ENGLISH))
            .thenReturn("https://contact.example.com");
        when(messageSource.getMessage("api-docs.license.name", null, Locale.ENGLISH))
            .thenReturn("License Name");
        when(messageSource.getMessage("api-docs.license.url", null, Locale.ENGLISH))
            .thenReturn("https://license.example.com");
        when(messageSource.getMessage("operation.summary", null, Locale.ENGLISH))
            .thenReturn("Operation Summary");
        when(messageSource.getMessage("operation.description", null, Locale.ENGLISH))
            .thenReturn("Operation Description");

        customizer.customise(openApi);

        assertEquals("Operation Summary", operation.getSummary());
        assertEquals("Operation Description", operation.getDescription());
    }

    @Test
    void testLocalizeMessageWithNoPattern() {
        OpenAPI openApi = new OpenAPI();
        Paths paths = new Paths();
        PathItem pathItem = new PathItem();
        Operation operation = new Operation();
        operation.setSummary("Static summary");
        pathItem.setGet(operation);
        paths.addPathItem("/test", pathItem);
        openApi.setPaths(paths);
        openApi.info(new Info());

        customizer.customise(openApi);

        assertEquals("Static summary", operation.getSummary());
    }

    @Test
    void testGetMessage() {
        // We can't test the private method directly, so we are testing indirectly through the public methods
        OpenAPI openApi = new OpenAPI();
        openApi.info(new Info());

        when(messageSource.getMessage("api-docs.title", null, Locale.ENGLISH))
            .thenReturn("API Documentation Title");

        customizer.customise(openApi);

        assertEquals("API Documentation Title", openApi.getInfo().getTitle());
    }
}