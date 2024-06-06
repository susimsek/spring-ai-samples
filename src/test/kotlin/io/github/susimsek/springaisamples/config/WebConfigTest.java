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
import org.springframework.web.servlet.config.annotation.ViewControllerRegistration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;

@ExtendWith(MockitoExtension.class)
class WebConfigTest {

    @Mock
    private ResourceHandlerRegistry resourceHandlerRegistry;

    @Mock
    private ViewControllerRegistry viewControllerRegistry;

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
    void addViewControllers_ShouldAddViewController() {
        // Arrange
        ViewControllerRegistration registration = mock(ViewControllerRegistration.class);
        when(viewControllerRegistry.addViewController("/api-docs")).thenReturn(registration);

        // Act
        webConfig.addViewControllers(viewControllerRegistry);

        // Assert
        verify(registration).setViewName("forward:/index.html");
    }
}