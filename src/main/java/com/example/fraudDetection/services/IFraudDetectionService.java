package com.example.fraudDetection.services;

import com.example.fraudDetection.dto.ResponsePayload;
import com.example.fraudDetection.dto.TransactionEvent;

public interface IFraudDetectionService {
    public ResponsePayload addTransactionEventToQueue(TransactionEvent transactionEvent);
}
