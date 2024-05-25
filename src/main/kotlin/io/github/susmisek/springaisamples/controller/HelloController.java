package io.github.susmisek.springaisamples.controller;

import io.github.susmisek.springaisamples.i18n.NamedParameterMessageSource;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HelloController {


    private final NamedParameterMessageSource messageSource;

    @GetMapping("/hello")
    public String sayHello(@RequestParam String name) {
        messageSource.addNamedParameter("name", name);
        return messageSource.getMessage("hello.message", name);
    }
}
