package io.github.susmisek.springaisamples.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Violation(
    @Schema(description = "Object name", example = "chatRequest")
    @JsonProperty("object")
    String objectName,
    @Schema(description = "Field name", example = "prompt")
    String field,
    @Schema(description = "Violation rejected value", example = "a")
    Object rejectedValue,
    @Schema(description = "Error Message", example = "size must be between 4 and 50")
    String message
) {
    public Violation(String objectName, String message) {
        this(objectName, null, null, message);
    }

    public Violation(FieldError error) {
        this(
            error.getObjectName().replaceFirst("DTO$", ""),
            error.getField(),
            error.getRejectedValue(),
            error.getDefaultMessage()
        );
    }

    public Violation(ObjectError error) {
        this(
            null,
            error.getObjectName().replaceFirst("DTO$", ""),
            null,
            error.getDefaultMessage()
        );
    }

    public Violation(ConstraintViolation<?> violation) {
        this(
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
}
