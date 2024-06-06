package io.github.susimsek.springaisamples.config;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;

@ExtendWith(MockitoExtension.class)
class WebConfigTest {

    @Mock
    private ResourceHandlerRegistry resourceHandlerRegistry;

    @InjectMocks
    private WebConfig webConfig;

    @Test
    void addResourceHandlers_ShouldAddResourceHandler() {
        // Arrange
        ResourceHandlerRegistration registration = mock(ResourceHandlerRegistration.class);
        when(resourceHandlerRegistry.addResourceHandler("/**")).thenReturn(registration);

        // Act
        webConfig.addResourceHandlers(resourceHandlerRegistry);

        // Assert
        verify(registration).addResourceLocations("classpath:/static/");
    }

    @Test
    void addViewControllers_ShouldAddRedirectViewController() {
        // Arrange
        ViewControllerRegistry registry = mock(ViewControllerRegistry.class);
        WebConfig webConfig = new WebConfig();

        // Act
        webConfig.addViewControllers(registry);

        // Assert
        verify(registry).addRedirectViewController("/api-docs", "/api-docs/index.html");
    }
}