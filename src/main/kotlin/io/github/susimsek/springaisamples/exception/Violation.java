package io.github.susimsek.springaisamples.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.susimsek.springaisamples.utils.StringUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path;
import java.io.Serializable;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Violation(
    @Schema(description = "Code.", example = "not_blank")
    @JsonProperty
    String code,

    @Schema(description = "Object name", example = "chatRequest")
    @JsonProperty("object")
    String objectName,

    @Schema(description = "Field name", example = "prompt")
    @JsonProperty
    String field,

    @Schema(description = "Violation rejected value", example = "a")
    @JsonProperty
    Object rejectedValue,

    @Schema(description = "Error Message", example = "size must be between 4 and 50")
    @JsonProperty
    String message
) implements Serializable {
    public Violation(String objectName, String message) {
        this(null, objectName, null, null, message);
    }

    public Violation(FieldError error) {
        this(
            getCode(error.getCode()),
            error.getObjectName().replaceFirst("DTO$", ""),
            error.getField(),
            error.getRejectedValue(),
            error.getDefaultMessage()
        );
    }

    public Violation(ObjectError error) {
        this(
            getCode(error.getCode()),
            null,
            error.getObjectName().replaceFirst("DTO$", ""),
            null,
            error.getDefaultMessage()
        );
    }

    public Violation(ConstraintViolation<?> violation) {
        this(
            getCode(violation.getConstraintDescriptor()
                .getAnnotation().annotationType().getSimpleName()),
            null,
            getField(violation.getPropertyPath()),
            violation.getInvalidValue(),
            violation.getMessage()
        );
    }

    private static String getField(Path propertyPath) {
        String fieldName = null;
        for (Path.Node node : propertyPath) {
            fieldName = node.getName();
        }
        return fieldName;
    }

    private static String getCode(String annotationName) {
        return StringUtils.toSnakeCase(annotationName);
    }
}
