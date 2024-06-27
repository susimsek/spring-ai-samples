package io.github.susimsek.springaisamples.model;

import io.github.susimsek.springaisamples.trace.TraceConstants;
import io.github.susimsek.springaisamples.validation.HeaderFormat;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HeaderDTO {

    @HeaderFormat(headerName = TraceConstants.REQUEST_ID_HEADER_NAME,
        min = 8, max = 36, regexp = "^[a-zA-Z0-9-]*$")
    private String requestId;

    @HeaderFormat(headerName = TraceConstants.CORRELATION_ID_HEADER_NAME,
        min = 8, max = 36, regexp = "^[a-zA-Z0-9-]*$")
    private String correlationId;

    public static HeaderDTO fromHttpServletRequest(HttpServletRequest request) {
        return HeaderDTO.builder()
                .requestId(request.getHeader(TraceConstants.REQUEST_ID_HEADER_NAME))
                .correlationId(request.getHeader(TraceConstants.CORRELATION_ID_HEADER_NAME))
                .build();
    }
}