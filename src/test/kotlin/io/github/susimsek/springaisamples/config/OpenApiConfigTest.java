package io.github.susimsek.springaisamples.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import java.util.Arrays;
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
    void testCustomizeOpenApi() {
        when(messageSource.getMessage(anyString(), eq(null), any(Locale.class)))
            .thenAnswer(invocation -> invocation.getArgument(0) + ".localized");
        OpenApiCustomizer customizer = openApiConfig.openApiCustomizer(messageSource);
        OpenAPI openApi = new OpenAPI().info(new Info());
        customizer.customise(openApi);

        Info info = openApi.getInfo();
        assertEquals("api-docs.title.localized", info.getTitle());
        assertEquals("api-docs.description.localized", info.getDescription());

        Contact contact = info.getContact();
        assertNotNull(contact);
        assertEquals("api-docs.contact.name.localized", contact.getName());
        assertEquals("api-docs.contact.email.localized", contact.getEmail());
        assertEquals("api-docs.contact.url.localized", contact.getUrl());

        License license = info.getLicense();
        assertNotNull(license);
        assertEquals("api-docs.license.name.localized", license.getName());
        assertEquals("api-docs.license.url.localized", license.getUrl());
    }

    @Test
    void testOperationCustomizer() {
        OperationCustomizer customizer = openApiConfig.operationCustomizer();
        Operation operation = new Operation();
        HandlerMethod handlerMethod = mock(HandlerMethod.class);

        customizer.customize(operation, handlerMethod);

        Parameter parameter = operation.getParameters().stream()
            .filter(p -> "Accept-Language".equals(p.getName()))
            .findFirst()
            .orElse(null);

        assertNotNull(parameter);
        assertEquals("header", parameter.getIn());
        assertEquals("Language preference", parameter.getDescription());
        assertEquals(Arrays.asList("en", "tr"), ((StringSchema) parameter.getSchema()).getEnum());
    }
}