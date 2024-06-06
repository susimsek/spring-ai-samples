package io.github.susimsek.springaisamples.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.StringSchema;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.MessageSource;
import org.springframework.web.method.HandlerMethod;

@ExtendWith(MockitoExtension.class)
class OpenApiConfigTest {

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private OpenApiConfig openApiConfig;

    @Test
    void customizeOpenApi_ShouldSetApiInfo() {
        // Arrange
        OpenAPI openAPI = new OpenAPI();
        openAPI.setInfo(new Info());

        when(messageSource.getMessage(eq("api-docs.title"), isNull(), any(Locale.class))).thenReturn("Sample API Title");
        when(messageSource.getMessage(eq("api-docs.description"), isNull(), any(Locale.class))).thenReturn("Sample API Description");
        when(messageSource.getMessage(eq("api-docs.contact.name"), isNull(), any(Locale.class))).thenReturn("John Doe");
        when(messageSource.getMessage(eq("api-docs.contact.email"), isNull(), any(Locale.class))).thenReturn("john@example.com");
        when(messageSource.getMessage(eq("api-docs.contact.url"), isNull(), any(Locale.class))).thenReturn("https://example.com");
        when(messageSource.getMessage(eq("api-docs.license.name"), isNull(), any(Locale.class))).thenReturn("Apache 2.0");
        when(messageSource.getMessage(eq("api-docs.license.url"), isNull(), any(Locale.class))).thenReturn("https://www.apache.org/licenses/LICENSE-2.0");

        // Act
        OpenApiCustomizer customizer = openApiConfig.customizeOpenApi(messageSource);
        customizer.customise(openAPI);

        // Assert
        assertEquals("Sample API Title", openAPI.getInfo().getTitle());
        assertEquals("Sample API Description", openAPI.getInfo().getDescription());

        Contact contact = openAPI.getInfo().getContact();
        assertEquals("John Doe", contact.getName());
        assertEquals("john@example.com", contact.getEmail());
        assertEquals("https://example.com", contact.getUrl());

        assertEquals("Apache 2.0", openAPI.getInfo().getLicense().getName());
        assertEquals("https://www.apache.org/licenses/LICENSE-2.0", openAPI.getInfo().getLicense().getUrl());
    }

    @Test
    void operationCustomizer_ShouldAddAcceptLanguageHeader() {
        // Arrange
        Operation operation = new Operation();
        HandlerMethod handlerMethod = mock(HandlerMethod.class);

        // Act
        OperationCustomizer customizer = openApiConfig.operationCustomizer();
        customizer.customize(operation, handlerMethod);

        // Assert
        assertEquals(1, operation.getParameters().size());
        assertEquals("header", operation.getParameters().get(0).getIn());
        assertEquals("Accept-Language", operation.getParameters().get(0).getName());
        assertEquals("Language preference", operation.getParameters().get(0).getDescription());
        assertInstanceOf(StringSchema.class, operation.getParameters().get(0).getSchema());
    }
}