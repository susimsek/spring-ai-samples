package io.github.susimsek.springaisamples

import io.github.susimsek.springaisamples.annotation.Generated
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SpringAiSamplesApplication

@Generated
fun main(args: Array<String>) {
    runApplication<SpringAiSamplesApplication>(*args)
}
