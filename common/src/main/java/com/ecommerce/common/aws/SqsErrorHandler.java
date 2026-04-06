package com.ecommerce.common.aws;

import java.util.Optional;
import java.util.Set;

import org.springframework.messaging.Message;

import io.awspring.cloud.sqs.listener.SqsHeaders;
import io.awspring.cloud.sqs.listener.errorhandler.ErrorHandler;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.extern.slf4j.Slf4j;

/**
 * Custom SQS error handler that behaves similarly to Kafka's DefaultErrorHandler.
 * It distinguishes between retryable and non-retryable exceptions by inspecting the cause chain.
 */
@Slf4j
public class SqsErrorHandler<T> implements ErrorHandler<T> {

    private final SqsTemplate sqsTemplate;
    private final Set<Class<? extends Throwable>> nonRetryableExceptions;

    public SqsErrorHandler(SqsTemplate sqsTemplate, Set<Class<? extends Throwable>> nonRetryableExceptions) {
        this.sqsTemplate = sqsTemplate;
        this.nonRetryableExceptions = nonRetryableExceptions;
    }

    @Override
    public void handle(Message<T> message, Throwable t) {
        log.error("SQS message processing failed: {}", t.getMessage());
        
        Optional<Throwable> nonRetryableCause = findNonRetryableCause(t);
        
        if (nonRetryableCause.isPresent()) {
            Throwable cause = nonRetryableCause.get();
            log.warn("Non-retryable exception detected: {}. Moving message to DLQ.", cause.getClass().getSimpleName());
            
            String originalQueue = (String) message.getHeaders().get(SqsHeaders.SQS_QUEUE_NAME_HEADER);
            if (originalQueue == null) {
                log.error("Could not determine source queue name from headers. Manual intervention required.");
                throw new RuntimeException("Missing queue name header", t);
            }
            
            String dlqName = originalQueue + "-dlq";
            try {
                sqsTemplate.send(dlqName, message.getPayload());
                log.info("Message successfully moved to DLQ: {}", dlqName);
                // Return normally to effectively acknowledge the message and stop retries
                return;
            } catch (Exception e) {
                log.error("Failed to send message to DLQ: {}. Message will be retried natively by SQS.", dlqName, e);
            }
        } else {
            log.info("Exception is retryable or its cause was not identified as non-retryable. Triggering native SQS retry.");
        }

        // Re-throw the exception to trigger the native SQS Redrive Policy / visibility timeout retry
        if (t instanceof RuntimeException) {
            throw (RuntimeException) t;
        }
        throw new RuntimeException(t);
    }

    private Optional<Throwable> findNonRetryableCause(Throwable t) {
        Throwable current = t;
        while (current != null) {
            for (Class<? extends Throwable> nonRetryable : nonRetryableExceptions) {
                if (nonRetryable.isInstance(current)) {
                    return Optional.of(current);
                }
            }
            current = current.getCause();
        }
        return Optional.empty();
    }
}
