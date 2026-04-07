#!/bin/bash
echo "Initializing LocalStack SNS/SQS FIFO..."

# 1. Create SNS FIFO Topics
awslocal sns create-topic --name order-events.fifo --attributes "FifoTopic=true,ContentBasedDeduplication=true"
awslocal sns create-topic --name payment-events.fifo --attributes "FifoTopic=true,ContentBasedDeduplication=true"
awslocal sns create-topic --name stock-events.fifo --attributes "FifoTopic=true,ContentBasedDeduplication=true"

# 2. Create SQS FIFO Dead Letter Queues (DLQs)
awslocal sqs create-queue --queue-name order-payment-queue-dlq.fifo --attributes "FifoQueue=true"
awslocal sqs create-queue --queue-name order-stock-queue-dlq.fifo --attributes "FifoQueue=true"
awslocal sqs create-queue --queue-name payment-order-queue-dlq.fifo --attributes "FifoQueue=true"
awslocal sqs create-queue --queue-name stock-order-queue-dlq.fifo --attributes "FifoQueue=true"

# 3. Create SQS FIFO Main Queues with Redrive Policy
# maxReceiveCount=3: move to DLQ after 3 failures
REDRIVE_POLICY_ORDER_PAYMENT='{"deadLetterTargetArn":"arn:aws:sqs:us-east-1:000000000000:order-payment-queue-dlq.fifo","maxReceiveCount":"3"}'
REDRIVE_POLICY_ORDER_STOCK='{"deadLetterTargetArn":"arn:aws:sqs:us-east-1:000000000000:order-stock-queue-dlq.fifo","maxReceiveCount":"3"}'
REDRIVE_POLICY_PAYMENT_ORDER='{"deadLetterTargetArn":"arn:aws:sqs:us-east-1:000000000000:payment-order-queue-dlq.fifo","maxReceiveCount":"3"}'
REDRIVE_POLICY_STOCK_ORDER='{"deadLetterTargetArn":"arn:aws:sqs:us-east-1:000000000000:stock-order-queue-dlq.fifo","maxReceiveCount":"3"}'

awslocal sqs create-queue --queue-name order-payment-queue.fifo \
  --attributes "FifoQueue=true,ContentBasedDeduplication=true,RedrivePolicy=$REDRIVE_POLICY_ORDER_PAYMENT"

awslocal sqs create-queue --queue-name order-stock-queue.fifo \
  --attributes "FifoQueue=true,ContentBasedDeduplication=true,RedrivePolicy=$REDRIVE_POLICY_ORDER_STOCK"

awslocal sqs create-queue --queue-name payment-order-queue.fifo \
  --attributes "FifoQueue=true,ContentBasedDeduplication=true,RedrivePolicy=$REDRIVE_POLICY_PAYMENT_ORDER"

awslocal sqs create-queue --queue-name stock-order-queue.fifo \
  --attributes "FifoQueue=true,ContentBasedDeduplication=true,RedrivePolicy=$REDRIVE_POLICY_STOCK_ORDER"

# 4. Subscribe FIFO Queues to FIFO Topics
# order-service listens to payment-events and stock-events
awslocal sns subscribe --topic-arn arn:aws:sns:us-east-1:000000000000:payment-events.fifo --protocol sqs --notification-endpoint arn:aws:sqs:us-east-1:000000000000:order-payment-queue.fifo
awslocal sns subscribe --topic-arn arn:aws:sns:us-east-1:000000000000:stock-events.fifo --protocol sqs --notification-endpoint arn:aws:sqs:us-east-1:000000000000:order-stock-queue.fifo

# payment-service listens to order-events
awslocal sns subscribe --topic-arn arn:aws:sns:us-east-1:000000000000:order-events.fifo --protocol sqs --notification-endpoint arn:aws:sqs:us-east-1:000000000000:payment-order-queue.fifo

# stock-service listens to order-events
awslocal sns subscribe --topic-arn arn:aws:sns:us-east-1:000000000000:order-events.fifo --protocol sqs --notification-endpoint arn:aws:sqs:us-east-1:000000000000:stock-order-queue.fifo

echo "LocalStack initialization completed (FIFO Mode)."
