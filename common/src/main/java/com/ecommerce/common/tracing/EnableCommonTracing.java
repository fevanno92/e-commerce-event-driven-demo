package com.ecommerce.common.tracing;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

/**
 * Enables common tracing utilities for the annotated Spring application.
 * Imports TracingContextHandler and necessary configurations to support
 * distributed trace propagation across asynchronous boundaries.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(TracingContextHandler.class)
public @interface EnableCommonTracing {
}
