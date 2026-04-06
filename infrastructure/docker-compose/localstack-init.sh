#!/bin/bash
echo "Initializing LocalStack SNS/SQS..."

# 1. Create SNS Topics
awslocal sns create-topic --name order-events
awslocal sns create-topic --name payment-events
awslocal sns create-topic --name stock-events

# 2. Create SQS Queues
awslocal sqs create-queue --queue-name order-payment-queue
awslocal sqs create-queue --queue-name order-stock-queue
awslocal sqs create-queue --queue-name payment-order-queue
awslocal sqs create-queue --queue-name stock-order-queue

# 3. Subscribe Queues to Topics
# order-service listens to payment-events and stock-events
awslocal sns subscribe --topic-arn arn:aws:sns:us-east-1:000000000000:payment-events --protocol sqs --notification-endpoint arn:aws:sqs:us-east-1:000000000000:order-payment-queue
awslocal sns subscribe --topic-arn arn:aws:sns:us-east-1:000000000000:stock-events --protocol sqs --notification-endpoint arn:aws:sqs:us-east-1:000000000000:order-stock-queue

# payment-service listens to order-events
awslocal sns subscribe --topic-arn arn:aws:sns:us-east-1:000000000000:order-events --protocol sqs --notification-endpoint arn:aws:sqs:us-east-1:000000000000:payment-order-queue

# stock-service listens to order-events
awslocal sns subscribe --topic-arn arn:aws:sns:us-east-1:000000000000:order-events --protocol sqs --notification-endpoint arn:aws:sqs:us-east-1:000000000000:stock-order-queue

echo "LocalStack initialization completed."
