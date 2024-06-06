package io.github.susimsek.springaisamples.config;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.susimsek.springaisamples.exception.Violation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.predicate.RuntimeHintsPredicates;

class HintsRegistrarTest {

    private HintsRegistrar hintsRegistrar;
    private RuntimeHints runtimeHints;

    @BeforeEach
    void setUp() {
        hintsRegistrar = new HintsRegistrar();
        runtimeHints = new RuntimeHints();
    }

    @Test
    void testRegisterHints() {
        hintsRegistrar.registerHints(runtimeHints, this.getClass().getClassLoader());

        // Check that the Violation class is registered for reflection with its members
        assertThat(RuntimeHintsPredicates.reflection().onType(Violation.class))
            .accepts(runtimeHints);

        // Check that the resource patterns are registered
        assertThat(RuntimeHintsPredicates.resource().forResource("data/*.json"))
            .accepts(runtimeHints);
        assertThat(RuntimeHintsPredicates.resource().forResource("i18n/*.properties"))
            .accepts(runtimeHints);
        assertThat(RuntimeHintsPredicates.resource().forResource("images/*.png"))
            .accepts(runtimeHints);
        assertThat(RuntimeHintsPredicates.resource().forResource("prompts/*.st"))
            .accepts(runtimeHints);
        assertThat(RuntimeHintsPredicates.resource().forResource("*.txt"))
            .accepts(runtimeHints);
    }
}