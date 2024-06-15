package io.github.susimsek.springaisamples.openapi;

import io.github.susimsek.springaisamples.openapi.annotation.SignatureRequired;
import io.github.susimsek.springaisamples.security.SignatureConstants;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.parameters.Parameter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PathPatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@RequiredArgsConstructor
@Slf4j
public class LocalizedOpenApiCustomizer implements OpenApiCustomizer {

    private static final Pattern MESSAGE_PATTERN = Pattern.compile("\\{(.+?)}");
    private final MessageSource messageSource;
    private final OpenApiProperties openApiProperties;
    private final RequestMappingHandlerMapping requestMappingHandlerMapping;

    @Override
    public void customise(OpenAPI openApi) {
        var locale = LocaleContextHolder.getLocale();
        customiseOpenApiInfo(openApi, locale);
        customiseOperations(openApi);
        sortTags(openApi);
    }

    private void customiseOpenApiInfo(OpenAPI openApi, Locale locale) {
        openApi.getInfo()
            .title(getMessage("api-docs.title", locale))
            .description(getMessage("api-docs.description", locale))
            .contact(new io.swagger.v3.oas.models.info.Contact()
                .name(getMessage("api-docs.contact.name", locale))
                .email(getMessage("api-docs.contact.email", locale))
                .url(getMessage("api-docs.contact.url", locale)))
            .license(new io.swagger.v3.oas.models.info.License()
                .name(getMessage("api-docs.license.name", locale))
                .url(getMessage("api-docs.license.url", locale)));
    }

    private void customiseOperations(OpenAPI openApi) {
        if (openApi.getPaths() != null) {
            openApi.getPaths().forEach((path, pathItem) ->
                pathItem.readOperations().forEach(operation -> {
                    localiseMessage(operation::getSummary, operation::setSummary);
                    localiseMessage(operation::getDescription, operation::setDescription);
                    if (hasSignatureRequiredAnnotation(path)) {
                        addJwsSignatureHeaderParameter(operation);
                    }
                })
            );
        }
    }

    private boolean hasSignatureRequiredAnnotation(String path) {
        return requestMappingHandlerMapping.getHandlerMethods().entrySet().stream()
            .flatMap(entry -> {
                RequestMappingInfo mappingInfo = entry.getKey();
                HandlerMethod handlerMethod = entry.getValue();

                Set<String> patterns = Optional.ofNullable(mappingInfo.getPathPatternsCondition())
                    .map(PathPatternsRequestCondition::getPatternValues)
                    .orElse(Set.of());

                return patterns.stream().map(pattern -> Map.entry(pattern, handlerMethod));
            })
            .anyMatch(entry -> entry.getKey().equals(path)
                && (entry.getValue().hasMethodAnnotation(SignatureRequired.class)
                || entry.getValue().getBeanType().isAnnotationPresent(SignatureRequired.class)));
    }


    private void addJwsSignatureHeaderParameter(Operation operation) {
        Parameter jwsSignatureHeader = new Parameter()
            .in(ParameterIn.HEADER.toString())
            .name(SignatureConstants.JWS_SIGNATURE_HEADER_NAME)
            .required(true)
            .description("JWS Signature Header")
            .example("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
            .schema(new io.swagger.v3.oas.models.media.StringSchema());

        operation.addParametersItem(jwsSignatureHeader);
    }

    private void localiseMessage(Supplier<String> getter, Consumer<String> setter) {
        String message = getter.get();
        if (message != null) {
            Matcher matcher = MESSAGE_PATTERN.matcher(message);
            if (matcher.matches()) {
                String messageKey = matcher.group(1);
                setter.accept(getMessage(messageKey));
            }
        }
    }

    private String getMessage(String key) {
        return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    }

    private String getMessage(String key, Locale locale) {
        return messageSource.getMessage(key, null, locale);
    }

    private void sortTags(OpenAPI openApi) {
        if (openApi.getTags() != null) {
            List<String> priorityTags = openApiProperties.getPriorityTags();
            openApi.getTags().sort((tag1, tag2) -> {
                int index1 = priorityTags.indexOf(tag1.getName());
                int index2 = priorityTags.indexOf(tag2.getName());

                if (index1 == -1 && index2 == -1) {
                    return tag1.getName().compareTo(tag2.getName());
                }
                if (index1 == -1) {
                    return 1;
                }
                if (index2 == -1) {
                    return -1;
                }
                return Integer.compare(index1, index2);
            });
        }
    }
}