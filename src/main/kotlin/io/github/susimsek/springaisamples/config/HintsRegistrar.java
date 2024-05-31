package io.github.susimsek.springaisamples.config;

import io.github.susimsek.springaisamples.exception.Violation;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeHint;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;

@Configuration(proxyBeanMethods = false)
@ImportRuntimeHints(HintsRegistrar.class)
public class HintsRegistrar implements RuntimeHintsRegistrar {
    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        hints.reflection().registerType(Violation.class, TypeHint.Builder::withMembers);
        hints.resources().registerPattern("data/*.json");
        hints.resources().registerPattern("images/*.png");
        hints.resources().registerPattern("prompts/*.st");
        hints.resources().registerPattern("*.txt");
    }
}