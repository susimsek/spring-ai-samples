package io.github.susmisek.springaisamples.utils;

import java.util.Map;
import lombok.experimental.UtilityClass;
import org.springframework.ai.chat.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.parser.BeanOutputParser;
import org.springframework.ai.parser.MapOutputParser;

@UtilityClass
@SuppressWarnings("checkstyle:MethodTypeParameterName")
public class ChatUtils {

    public static final String FORMAT = "format";

    public Prompt buildPrompt(String template, Map<String, Object> model) {
        return new PromptTemplate(template, model).create();
    }

    public <T> T parseResponse(Generation generation, BeanOutputParser<T> outputParser) {
        return outputParser.parse(generation.getOutput().getContent());
    }

    public Map<String, Object> parseResponse(Generation generation, MapOutputParser outputParser) {
        return outputParser.parse(generation.getOutput().getContent());
    }
}