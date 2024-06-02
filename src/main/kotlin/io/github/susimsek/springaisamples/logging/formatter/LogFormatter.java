package io.github.susimsek.springaisamples.logging.formatter;

import io.github.susimsek.springaisamples.logging.model.HttpLog;

public interface LogFormatter {
    String format(HttpLog httpLog);
}