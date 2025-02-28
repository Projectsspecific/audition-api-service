package com.audition.configuration;

import org.springframework.stereotype.Component;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import org.springframework.beans.factory.annotation.Autowired;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

@Component
public class ResponseHeaderInjector {

    // TODO Inject openTelemetry trace and span Ids in the response headers.
    @Autowired
    private Tracer tracer;

    public void injectHeaders(HttpServletResponse response) {
        Span currentSpan = Span.current();
        String traceId = currentSpan.getSpanContext().getTraceId();
        String spanId = currentSpan.getSpanContext().getSpanId();

        HttpServletResponseWrapper responseWrapper = new HttpServletResponseWrapper(response);
        responseWrapper.setHeader("trace-id", traceId);
        responseWrapper.setHeader("span-id", spanId);
    }
}
