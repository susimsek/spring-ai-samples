package io.github.susimsek.springaisamples.i18n;

import jakarta.validation.ConstraintTarget;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.MessageInterpolator;
import jakarta.validation.Payload;
import jakarta.validation.metadata.ConstraintDescriptor;
import jakarta.validation.metadata.ValidateUnwrappedValue;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Represents the context for a message interpolation.
 * This class implements {@link MessageInterpolator.Context} and provides
 * additional attributes for custom message interpolation.
 */
public record MessageContext(String messageTemplate,
                             Map<String, Object> attributes,
                             ConstraintDescriptor<?> constraintDescriptor,
                             Object validatedValue) implements MessageInterpolator.Context {

    /**
     * Constructs a new MessageContext.
     *
     * @param messageTemplate    the message template
     * @param attributes         the attributes for message interpolation
     * @param validatedValue     the validated value
     */
    public MessageContext(
        String messageTemplate,
        Map<String, Object> attributes,
        Object validatedValue) {
        this(
            messageTemplate,
            attributes,
            new DynamicConstraintDescriptor(attributes, messageTemplate),
            validatedValue);
    }

    /**
     * Gets the constraint descriptor.
     *
     * @return the constraint descriptor
     */
    @Override
    public ConstraintDescriptor<?> getConstraintDescriptor() {
        return this.constraintDescriptor;
    }

    /**
     * Gets the validated value.
     *
     * @return the validated value
     */
    @Override
    public Object getValidatedValue() {
        return validatedValue;
    }

    /**
     * Unwraps the context to the specified type.
     *
     * @param type the class to unwrap to
     * @param <T>  the type to unwrap to
     * @return the unwrapped instance
     * @throws IllegalArgumentException if the type is not supported
     */
    @Override
    public <T> T unwrap(Class<T> type) {
        if (type.isInstance(this)) {
            return type.cast(this);
        }
        throw new IllegalArgumentException("Type " + type + " not supported");
    }

    /**
     * Represents a dynamic constraint descriptor for message interpolation.
     * This class implements {@link ConstraintDescriptor} to provide dynamic attributes.
     */
    public record DynamicConstraintDescriptor(Map<String, Object> attributes, String messageTemplate)
        implements ConstraintDescriptor<Annotation> {

        /**
         * Gets the annotation associated with the constraint.
         *
         * @return the annotation (always null in this implementation)
         */
        @Override
        public Annotation getAnnotation() {
            return null;
        }

        /**
         * Gets the message template for the constraint.
         *
         * @return the message template
         */
        @Override
        public String getMessageTemplate() {
            return messageTemplate;
        }

        /**
         * Gets the groups the constraint is applied to.
         *
         * @return the groups (always an empty set in this implementation)
         */
        @Override
        public Set<Class<?>> getGroups() {
            return Collections.emptySet();
        }

        /**
         * Gets the payload for the constraint.
         *
         * @return the payload (always an empty set in this implementation)
         */
        @Override
        public Set<Class<? extends Payload>> getPayload() {
            return Collections.emptySet();
        }

        /**
         * Gets the validation target for the constraint.
         *
         * @return the validation target (always null in this implementation)
         */
        @Override
        public ConstraintTarget getValidationAppliesTo() {
            return null;
        }

        /**
         * Gets the constraint validator classes for the constraint.
         *
         * @return the constraint validator classes (always an empty list in this implementation)
         */
        @Override
        public List<Class<? extends ConstraintValidator<Annotation, ?>>> getConstraintValidatorClasses() {
            return Collections.emptyList();
        }

        /**
         * Gets the attributes for the constraint.
         *
         * @return the attributes
         */
        @Override
        public Map<String, Object> getAttributes() {
            return Optional.ofNullable(attributes)
                .orElse(Collections.emptyMap());
        }

        /**
         * Gets the composing constraints for the constraint.
         *
         * @return the composing constraints (always an empty set in this implementation)
         */
        @Override
        public Set<ConstraintDescriptor<?>> getComposingConstraints() {
            return Collections.emptySet();
        }

        /**
         * Determines if the constraint should be reported as a single violation.
         *
         * @return false (always false in this implementation)
         */
        @Override
        public boolean isReportAsSingleViolation() {
            return false;
        }

        /**
         * Gets the value unwrapping setting for the constraint.
         *
         * @return the value unwrapping setting (always null in this implementation)
         */
        @Override
        public ValidateUnwrappedValue getValueUnwrapping() {
            return null;
        }

        /**
         * Unwraps the constraint descriptor to the specified type.
         *
         * @param type the class to unwrap to
         * @param <U>  the type to unwrap to
         * @return the unwrapped instance
         * @throws IllegalArgumentException if the type is not supported
         */
        @Override
        public <U> U unwrap(Class<U> type) {
            if (type.isInstance(this)) {
                return type.cast(this);
            }
            throw new IllegalArgumentException("Type " + type + " not supported");
        }
    }
}