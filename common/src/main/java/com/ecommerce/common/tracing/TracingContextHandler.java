package com.ecommerce.common.tracing;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.propagation.Propagator;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

/**
 * Utility for capturing and restoring Micrometer Tracing context.
 * This is used to propagate distributed traces across asynchronous boundaries
 * like the Transactional Outbox pattern.
 */
@Component
@Slf4j
public class TracingContextHandler {

    private final Tracer tracer;
    private final Propagator propagator;
    private final ObjectMapper objectMapper;

    public TracingContextHandler(Tracer tracer, Propagator propagator, ObjectMapper objectMapper) {
        this.tracer = tracer;
        this.propagator = propagator;
        this.objectMapper = objectMapper;
    }

    /**
     * Captures the current trace context and returns it as a JSON string.
     * @return Serialized trace context or null if no context is active.
     */
    public String captureContext() {
        if (tracer.currentSpan() == null) {
            return null;
        }

        Map<String, String> contextMap = new HashMap<>();
        propagator.inject(tracer.currentSpan().context(), contextMap, (carrier, key, value) -> carrier.put(key, value));

        try {
            return objectMapper.writeValueAsString(contextMap);
        } catch (Exception e) {
            log.error("Failed to serialize tracing context", e);
            return null;
        }
    }

    /**
     * Extracts the trace context from a JSON string and returns a Span
     * that uses it as a parent.
     * @param tracingContext Serialized trace context.
     * @return A Span initialized with the parent context.
     */
    public Span restoreContext(String tracingContext) {
        if (tracingContext == null || tracingContext.isBlank()) {
            return tracer.nextSpan().name("outbox-publish-root");
        }

        try {
            Map<String, String> contextMap = objectMapper.readValue(tracingContext, new TypeReference<Map<String, String>>() {});
            Span.Builder builder = propagator.extract(contextMap, (carrier, key) -> carrier.get(key));
            return builder.name("outbox-publish").start();
        } catch (Exception e) {
            log.warn("Failed to deserialize tracing context, creating root span", e);
            return tracer.nextSpan().name("outbox-publish-fallback");
        }
    }
}
