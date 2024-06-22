package io.github.susimsek.springaisamples.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.susimsek.springaisamples.logging.config.LoggingProperties;
import io.github.susimsek.springaisamples.logging.filter.LoggingFilter;
import io.github.susimsek.springaisamples.logging.formatter.JsonLogFormatter;
import io.github.susimsek.springaisamples.logging.formatter.LogFormatter;
import io.github.susimsek.springaisamples.logging.handler.HttpLoggingHandler;
import io.github.susimsek.springaisamples.logging.handler.LoggingHandler;
import io.github.susimsek.springaisamples.logging.strategy.DefaultObfuscationStrategy;
import io.github.susimsek.springaisamples.logging.strategy.NoOpObfuscationStrategy;
import io.github.susimsek.springaisamples.logging.strategy.ObfuscationStrategy;
import io.github.susimsek.springaisamples.logging.utils.Obfuscator;
import io.github.susimsek.springaisamples.logging.wrapper.HttpLoggingWrapper;
import jakarta.servlet.Filter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.ObjectProvider;

class LoggingConfigTest {

    private LoggingConfig loggingConfig;

    @BeforeEach
    void setUp() {
        loggingConfig = new LoggingConfig();
    }

    @Test
    void testHttpLoggingWrapperBeanCreation() {
        HttpLoggingHandler httpLoggingHandlerMock = mock(HttpLoggingHandler.class);
        HttpLoggingWrapper httpLoggingWrapper = loggingConfig.httpLoggingWrapper(httpLoggingHandlerMock);
        assertNotNull(httpLoggingWrapper);
        assertEquals(HttpLoggingWrapper.class, httpLoggingWrapper.getClass());
    }

    @Test
    void testHttpLoggingHandlerBeanCreation() {
        LoggingProperties loggingPropertiesMock = mock(LoggingProperties.class);
        LogFormatter logFormatterMock = mock(LogFormatter.class);
        Obfuscator obfuscatorMock = mock(Obfuscator.class);
        var loggingHandler = loggingConfig.loggingHandler(loggingPropertiesMock, logFormatterMock, obfuscatorMock, null);
        assertNotNull(loggingHandler);
        assertEquals(HttpLoggingHandler.class, loggingHandler.getClass());
    }

    @Test
    void testLogFormatterBeanCreation() {
        @SuppressWarnings("unchecked")
        ObjectProvider<ObjectMapper> objectMapperProviderMock = mock(ObjectProvider.class);
        LogFormatter logFormatter = loggingConfig.logFormatter(objectMapperProviderMock);
        assertNotNull(logFormatter);
        assertEquals(JsonLogFormatter.class, logFormatter.getClass());
    }

    @Test
    void testObfuscationStrategyBeanCreationWhenEnabled() {
        // Mock LoggingProperties
        LoggingProperties loggingPropertiesMock = mock(LoggingProperties.class);

        // Mock HttpLoggingProperties
        LoggingProperties.Http httpMock = mock(LoggingProperties.Http.class);
        when(loggingPropertiesMock.getHttp()).thenReturn(httpMock);

        // Mock Obfuscate
        LoggingProperties.Obfuscate obfuscateMock = mock(LoggingProperties.Obfuscate.class);
        when(httpMock.getObfuscate()).thenReturn(obfuscateMock);

        // Mock isEnabled() method of Obfuscate
        when(obfuscateMock.isEnabled()).thenReturn(true);

        // Mock ObjectMapper
        ObjectMapper objectMapperMock = mock(ObjectMapper.class);

        // Invoke the method under test
        ObfuscationStrategy obfuscationStrategy = loggingConfig.obfuscationStrategy(loggingPropertiesMock, objectMapperMock);

        // Verify that the correct strategy is returned
        assertNotNull(obfuscationStrategy);
        assertEquals(DefaultObfuscationStrategy.class, obfuscationStrategy.getClass());
    }

    @Test
    void testObfuscationStrategyBeanCreationWhenDisabled() {
        // Mock LoggingProperties
        LoggingProperties loggingPropertiesMock = mock(LoggingProperties.class);

        // Mock HttpLoggingProperties
        LoggingProperties.Http httpMock = mock(LoggingProperties.Http.class);
        when(loggingPropertiesMock.getHttp()).thenReturn(httpMock);

        // Mock Obfuscate
        LoggingProperties.Obfuscate obfuscateMock = mock(LoggingProperties.Obfuscate.class);
        when(httpMock.getObfuscate()).thenReturn(obfuscateMock);

        // Mock isEnabled() method of Obfuscate
        when(obfuscateMock.isEnabled()).thenReturn(false);

        // Mock ObjectMapper
        ObjectMapper objectMapperMock = mock(ObjectMapper.class);

        // Invoke the method under test
        ObfuscationStrategy obfuscationStrategy = loggingConfig.obfuscationStrategy(loggingPropertiesMock, objectMapperMock);

        // Verify that the correct strategy is returned
        assertNotNull(obfuscationStrategy);
        assertEquals(NoOpObfuscationStrategy.class, obfuscationStrategy.getClass());
    }

    @Test
    void testObfuscatorBeanCreation() {
        ObfuscationStrategy obfuscationStrategyMock = mock(ObfuscationStrategy.class);
        Obfuscator obfuscator = loggingConfig.obfuscator(obfuscationStrategyMock);
        assertNotNull(obfuscator);
        assertEquals(Obfuscator.class, obfuscator.getClass());
    }

    @Test
    void testLoggingFilter_ShouldReturnInstanceOfLoggingFilter() {
        // Arrange
        LoggingHandler loggingHandler = Mockito.mock(LoggingHandler.class);

        // Act
        Filter filter = loggingConfig.loggingFilter(loggingHandler);

        // Assert
        assertNotNull(filter);
        assertInstanceOf(LoggingFilter.class, filter);
    }


}