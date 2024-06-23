package io.github.susimsek.springaisamples.logging.formatter;

import io.github.susimsek.springaisamples.logging.model.HttpLog;
import io.github.susimsek.springaisamples.logging.model.MethodLog;

public interface LogFormatter {
    String format(HttpLog httpLog);

    String format(MethodLog methodLog);
}