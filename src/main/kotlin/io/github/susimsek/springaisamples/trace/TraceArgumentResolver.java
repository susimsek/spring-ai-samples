package io.github.susimsek.springaisamples.trace;

import static io.github.susimsek.springaisamples.trace.TraceConstants.SPAN_ID;
import static io.github.susimsek.springaisamples.trace.TraceConstants.TRACE_ID;

import org.slf4j.MDC;
import org.springframework.core.MethodParameter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class TraceArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(TraceContext.class)
            && parameter.getParameterType().equals(Trace.class);
    }

    @Override
    public Object resolveArgument(@NonNull MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {
        String requestId = webRequest.getHeader(TraceConstants.REQUEST_ID_HEADER_NAME);
        String correlationId = webRequest.getHeader(TraceConstants.CORRELATION_ID_HEADER_NAME);
        return Trace.builder()
            .traceId(MDC.get(TRACE_ID))
            .spanId(MDC.get(SPAN_ID))
            .requestId(requestId)
            .correlationId(correlationId)
            .build();
    }
}