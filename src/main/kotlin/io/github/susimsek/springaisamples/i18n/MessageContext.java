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
import java.util.Set;

public record MessageContext(Map<String, Object> attributes,
                             ConstraintDescriptor<?> constraintDescriptor,
                             Object validatedValue) implements MessageInterpolator.Context {

    @Override
    public ConstraintDescriptor<?> getConstraintDescriptor() {
        return constraintDescriptor != null
            ? constraintDescriptor
            : new DynamicConstraintDescriptor(attributes);
    }

    @Override
    public Object getValidatedValue() {
        return validatedValue;
    }

    @Override
    public <T> T unwrap(Class<T> type) {
        return null;
    }

    public record DynamicConstraintDescriptor(Map<String, Object> attributes)
        implements ConstraintDescriptor<Annotation> {

        @Override
        public Annotation getAnnotation() {
            return null;
        }

        @Override
        public String getMessageTemplate() {
            return null;
        }

        @Override
        public Set<Class<?>> getGroups() {
            return Collections.emptySet();
        }

        @Override
        public Set<Class<? extends Payload>> getPayload() {
            return Collections.emptySet();
        }

        @Override
        public ConstraintTarget getValidationAppliesTo() {
            return null;
        }

        @Override
        public List<Class<? extends ConstraintValidator<Annotation, ?>>> getConstraintValidatorClasses() {
            return Collections.emptyList();
        }

        @Override
        public Map<String, Object> getAttributes() {
            return attributes != null
                ? attributes
                : Collections.emptyMap();
        }

        @Override
        public Set<ConstraintDescriptor<?>> getComposingConstraints() {
            return Collections.emptySet();
        }

        @Override
        public boolean isReportAsSingleViolation() {
            return false;
        }

        @Override
        public ValidateUnwrappedValue getValueUnwrapping() {
            return null;
        }

        @Override
        public <U> U unwrap(Class<U> type) {
            return null;
        }
    }
}