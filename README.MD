# **Fraud Detection System Prototype**

## Overview

This project is a prototype implementation of a real-time fraud detection system for a distributed fintech ecosystem. The system analyzes transaction events to identify potential fraudulent patterns, such as rapid transactions across different services, unusually high transaction amounts, and irregular sequences of transactions.

## Algorithm Description

The fraud detection system analyzes transaction events in real-time, considering several fraudulent patterns:

Distinct Services Within Time Window: Flags a user if they conduct transactions in more than three distinct services within a five-minute window.
High Amount Transactions: Identifies transactions that exceed five times the user's average transaction amount in the last 24 hours.
Ping-Pong Activity: Detects a sequence of transactions bouncing back and forth between two services within a 10-minute period.
The implementation uses Java and employs a priority queue to simulate real-time transaction processing efficiently.

## Setup and Execution Instructions
Running the Prototype
Clone the repository and navigate to the project directory [https://github.com/olasubomi4/fraudDetection](https://github.com/olasubomi4/fraudDetection)
Compile and build the project using Maven or any Java IDE.
Run the main application class to start the fraud detection system.

## Simulation of Real-Time Data Processing

Modify the loadQueue() method in the code to load a test dataset representing a stream of transaction events. Additionally, an endpoint is provided at "api/v1/fraud-detection" `"curl --location 'http://localhost:8081/api/v1/fraud-detection' \
--header 'Content-Type: application/json' \
--data '{
"timestamp":1657909360,
"amount":1000000.00,
"userID":"user5",
"serviceID":"serviceA"
}'"` where transaction events can be inserted.
Adjust the timestamps and transaction details as required to simulate real-world scenarios.
Run the application to observe the fraud detection alerts generated based on the provided logic.

## Expected Results

The prototype should generate alerts and flagged users based on the predefined fraud detection rules when run against the test dataset. Sample expected results are provided in the expected_results.txt file of test dataset "test_dataset.csv".

## Handling Out-of-Order Events

To handle out-of-order events, the system maintains a priority queue of transaction events sorted by timestamp. As new events arrive, the system continuously checks the timestamps of events in the queue against the current time to ensure chronological order.


