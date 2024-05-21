package io.github.susmisek.springaisamples.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import jakarta.validation.Path;
import jakarta.validation.metadata.ConstraintDescriptor;
import java.util.Iterator;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

class ViolationTest {

    @Test
    void testFullConstructor() {
        String objectName = "testObject";
        String field = "testField";
        Object rejectedValue = "testRejectedValue";
        String message = "testMessage";

        Violation violation = new Violation(objectName, field, rejectedValue, message);

        assertEquals(objectName, violation.objectName());
        assertEquals(field, violation.field());
        assertEquals(rejectedValue, violation.rejectedValue());
        assertEquals(message, violation.message());
    }

    @Test
    void testObjectNameAndMessageConstructor() {
        String objectName = "testObject";
        String message = "testMessage";

        Violation violation = new Violation(objectName, message);

        assertEquals(objectName, violation.objectName());
        assertNull(violation.field());
        assertNull(violation.rejectedValue());
        assertEquals(message, violation.message());
    }

    @Test
    void testFieldErrorConstructor() {
        String objectName = "testObject";
        String field = "testField";
        Object rejectedValue = "testRejectedValue";
        String defaultMessage = "testDefaultMessage";

        FieldError fieldError = new FieldError(objectName, field, rejectedValue, false, null, null, defaultMessage);

        Violation violation = new Violation(fieldError);

        assertEquals(objectName, violation.objectName());
        assertEquals(field, violation.field());
        assertEquals(rejectedValue, violation.rejectedValue());
        assertEquals(defaultMessage, violation.message());
    }

    @Test
    void testObjectErrorConstructor() {
        String objectName = "testObject";
        String defaultMessage = "testDefaultMessage";

        ObjectError objectError = new ObjectError(objectName, defaultMessage);

        Violation violation = new Violation(objectError);

        assertNull(violation.objectName());
        assertEquals(objectName, violation.field()); // field is set as objectName in ObjectError constructor
        assertNull(violation.rejectedValue());
        assertEquals(defaultMessage, violation.message());
    }

    @Test
    void testConstraintViolationConstructor() {
        String propertyPath = "testProperty";
        Object invalidValue = "testInvalidValue";
        String message = "testMessage";

        ConstraintViolation<Object> constraintViolation =
            new TestConstraintViolation<>(propertyPath, invalidValue, message);

        Violation violation = new Violation(constraintViolation);

        assertNull(violation.objectName());
        assertEquals(propertyPath,
            violation.field()); // field is set as propertyPath in ConstraintViolation constructor
        assertEquals(invalidValue, violation.rejectedValue());
        assertEquals(message, violation.message());
    }

    // Dummy ConstraintViolation implementation for testing purposes
    private static class TestConstraintViolation<T> implements ConstraintViolation<T> {
        private final String propertyPath;
        private final Object invalidValue;
        private final String message;

        public TestConstraintViolation(String propertyPath, Object invalidValue, String message) {
            this.propertyPath = propertyPath;
            this.invalidValue = invalidValue;
            this.message = message;
        }

        @Override
        public String getMessage() {
            return message;
        }

        @Override
        public String getMessageTemplate() {
            return null;
        }

        @Override
        public T getRootBean() {
            return null;
        }

        @Override
        public Class<T> getRootBeanClass() {
            return null;
        }

        @Override
        public Object getLeafBean() {
            return null;
        }

        @Override
        public Object[] getExecutableParameters() {
            return new Object[0];
        }

        @Override
        public Object getExecutableReturnValue() {
            return null;
        }

        @Override
        public Path getPropertyPath() {
            return new TestPath(propertyPath);
        }

        @Override
        public Object getInvalidValue() {
            return invalidValue;
        }

        @Override
        public ConstraintDescriptor<?> getConstraintDescriptor() {
            return null;
        }

        @Override
        public <U> U unwrap(Class<U> type) {
            return null;
        }
    }

    // Dummy Path implementation for testing purposes
    private record TestPath(String propertyPath) implements Path {

        @Override
        public Iterator<Node> iterator() {
            return null;
        }

        @Override
        public String toString() {
            return propertyPath;
        }
    }
}
