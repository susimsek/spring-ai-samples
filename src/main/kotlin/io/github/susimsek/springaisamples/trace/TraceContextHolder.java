package io.github.susimsek.springaisamples.trace;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TraceContextHolder {

    private static final ThreadLocal<Trace> traceContext = new ThreadLocal<>();

    public static void setTrace(Trace trace) {
        traceContext.set(trace);
    }

    public static Trace getTrace() {
        return traceContext.get();
    }

    public static void clear() {
        traceContext.remove();
    }
}