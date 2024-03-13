package com.example.fraudDetection.controller;

//import com.example.fraudDetection.dto.TransactionEvent;
import com.example.fraudDetection.entity.TransactionEvent;
import com.example.fraudDetection.services.IFraudDetectionService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("api/v1/fraud-detection")
public class FraudDetectionController {

    @Autowired
    IFraudDetectionService iFraudDetectionService;

    @PostMapping
    public ResponseEntity<?> addTransactionEventToQueue(@RequestBody @Valid TransactionEvent transactionEvent) {
       return ResponseEntity.ok(iFraudDetectionService.addTransactionEventToQueue(transactionEvent));
    }
}
