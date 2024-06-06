package io.github.susimsek.springaisamples.controller.localization;

import static org.mockito.Mockito.when;

import io.github.susimsek.springaisamples.config.LocaleConfig;
import io.github.susimsek.springaisamples.i18n.ParameterMessageSource;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(LocalizationController.class)
@Import(LocaleConfig.class)
class LocalizationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ParameterMessageSource messageSource;

    @Test
    void getTranslations_ReturnsCorrectTranslations() throws Exception {
        // Mock the behavior of ParameterMessageSource
        Map<String, String> translations = new HashMap<>();
        translations.put("message1", "Test Message 1");
        translations.put("message2", "Test Message 2");
        when(messageSource.getMessagesStartingWith("api-docs", Locale.ENGLISH))
            .thenReturn(translations);

        // Perform the GET request and verify the response
        mockMvc.perform(MockMvcRequestBuilders.get("/api/locales")
                .header("Accept-Language", "en"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.message1").value("Test Message 1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.message2").value("Test Message 2"));
    }
}