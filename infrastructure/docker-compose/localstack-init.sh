#!/bin/bash
echo "Initializing LocalStack SNS/SQS..."

# 1. Create SNS Topics
awslocal sns create-topic --name order-events
awslocal sns create-topic --name payment-events
awslocal sns create-topic --name stock-events

# 2. Create SQS Dead Letter Queues (DLQs)
awslocal sqs create-queue --queue-name order-payment-queue-dlq
awslocal sqs create-queue --queue-name order-stock-queue-dlq
awslocal sqs create-queue --queue-name payment-order-queue-dlq
awslocal sqs create-queue --queue-name stock-order-queue-dlq

# 3. Create SQS Main Queues with Redrive Policy
# maxReceiveCount=3: move to DLQ after 3 failures
REDRIVE_POLICY_ORDER_PAYMENT='{"deadLetterTargetArn":"arn:aws:sqs:us-east-1:000000000000:order-payment-queue-dlq","maxReceiveCount":"3"}'
REDRIVE_POLICY_ORDER_STOCK='{"deadLetterTargetArn":"arn:aws:sqs:us-east-1:000000000000:order-stock-queue-dlq","maxReceiveCount":"3"}'
REDRIVE_POLICY_PAYMENT_ORDER='{"deadLetterTargetArn":"arn:aws:sqs:us-east-1:000000000000:payment-order-queue-dlq","maxReceiveCount":"3"}'
REDRIVE_POLICY_STOCK_ORDER='{"deadLetterTargetArn":"arn:aws:sqs:us-east-1:000000000000:stock-order-queue-dlq","maxReceiveCount":"3"}'

awslocal sqs create-queue --queue-name order-payment-queue --attributes "RedrivePolicy=$REDRIVE_POLICY_ORDER_PAYMENT"
awslocal sqs create-queue --queue-name order-stock-queue --attributes "RedrivePolicy=$REDRIVE_POLICY_ORDER_STOCK"
awslocal sqs create-queue --queue-name payment-order-queue --attributes "RedrivePolicy=$REDRIVE_POLICY_PAYMENT_ORDER"
awslocal sqs create-queue --queue-name stock-order-queue --attributes "RedrivePolicy=$REDRIVE_POLICY_STOCK_ORDER"

# 4. Subscribe Queues to Topics
# order-service listens to payment-events and stock-events
awslocal sns subscribe --topic-arn arn:aws:sns:us-east-1:000000000000:payment-events --protocol sqs --notification-endpoint arn:aws:sqs:us-east-1:000000000000:order-payment-queue
awslocal sns subscribe --topic-arn arn:aws:sns:us-east-1:000000000000:stock-events --protocol sqs --notification-endpoint arn:aws:sqs:us-east-1:000000000000:order-stock-queue

# payment-service listens to order-events
awslocal sns subscribe --topic-arn arn:aws:sns:us-east-1:000000000000:order-events --protocol sqs --notification-endpoint arn:aws:sqs:us-east-1:000000000000:payment-order-queue

# stock-service listens to order-events
awslocal sns subscribe --topic-arn arn:aws:sns:us-east-1:000000000000:order-events --protocol sqs --notification-endpoint arn:aws:sqs:us-east-1:000000000000:stock-order-queue

echo "LocalStack initialization completed."
