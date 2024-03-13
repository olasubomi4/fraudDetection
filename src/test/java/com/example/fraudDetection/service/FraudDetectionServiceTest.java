//package com.example.fraudDetection.service;
//
//
//import com.example.fraudDetection.dto.ResponsePayload;
//import com.example.fraudDetection.dto.TransactionEvent;
//import com.example.fraudDetection.services.IFraudDetectionService;
//import org.junit.jupiter.api.Test;
//import org.junit.runner.RunWith;
//import org.mockito.InjectMocks;
//import org.mockito.junit.MockitoJUnitRunner;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertTrue;
//
//@RunWith(MockitoJUnitRunner.class)
//
//public class FraudDetectionServiceTest {
//
//    @InjectMocks
//    private IFraudDetectionService fraudDetectionService;
//
//    @Test
//    void addTransactionEventToQueue_Success() {
//        TransactionEvent transactionEvent = TransactionEvent.builder()
//                .timestamp(System.currentTimeMillis())
//                .amount(100.00)
//                .userID("testUser")
//                .serviceID("testService")
//                .build();
//
//        ResponsePayload responsePayload = fraudDetectionService.addTransactionEventToQueue(transactionEvent);
//
//        assertTrue(responsePayload.getSuccess());
//        assertEquals("Success", responsePayload.getMessage());
//    }
//
//    @Test
//    void processTransactions_CheckFlaggedUsers() {
//        TransactionEvent flaggedEvent = TransactionEvent.builder()
//                .timestamp(System.currentTimeMillis())
//                .amount(5000.00)
//                .userID("flaggedUser")
//                .serviceID("testService")
//                .build();
//
//        fraudDetectionService.addTransactionEventToQueue(flaggedEvent);
//        fraudDetectionService.processTransactions();
//
//        assertTrue(fraudDetectionService.getFlaggedUsers().contains("flaggedUser"));
//    }
//
//    @Test
//    void processTransactions_NoFlaggedUsers() {
//        TransactionEvent nonFlaggedEvent = TransactionEvent.builder()
//                .timestamp(System.currentTimeMillis())
//                .amount(100.00)
//                .userID("nonFlaggedUser")
//                .serviceID("testService")
//                .build();
//
//        fraudDetectionService.addTransactionEventToQueue(nonFlaggedEvent);
//        fraudDetectionService.processTransactions();
//
//        assertFalse(fraudDetectionService.getFlaggedUsers().contains("nonFlaggedUser"));
//    }
//
//
//}
