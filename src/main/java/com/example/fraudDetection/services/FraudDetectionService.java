package com.example.fraudDetection.services;

import com.example.fraudDetection.dto.ResponsePayload;
import com.example.fraudDetection.dto.TransactionEvent;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Log4j2
public class FraudDetectionService implements IFraudDetectionService {

    private static final long FIVE_MINUTES = 5 * 60 * 1000;
    private static final long TEN_MINUTES = 10 * 60 * 1000;
    private static final int MAX_DISTINCT_SERVICES = 3;
    private static final double TRANSACTION_AMOUNT_MULTIPLIER = 5.0;
//    Queue<TransactionEvent> transactionEventQueue= new PriorityQueue<TransactionEvent>((a,b)-> Math.toIntExact(b.getTimestamp() - a.getTimestamp()));
     Queue<TransactionEvent> transactionEventQueue=   loadQueue();




    Map<String, UserTransactionData> userTransactionDataMap = new HashMap<>();

    private Set<String> flaggedUsers = new HashSet<>();

    private void flagUser(String userID) {
        flaggedUsers.add(userID);
    }


    public Set<String> getFlaggedUsers() {
        return flaggedUsers;
    }

    @Scheduled(fixedRate = 4,timeUnit = TimeUnit.SECONDS)
    public void processTransactions()
    {
        while (!transactionEventQueue.isEmpty() && transactionEventQueue.peek().getTimestamp() <= System.currentTimeMillis()) {

            TransactionEvent nextEvent = transactionEventQueue.poll();
            log.info(nextEvent.toString());
            UserTransactionData userData = userTransactionDataMap.computeIfAbsent(nextEvent.getUserID(), k -> new UserTransactionData());

            // Update user's transaction data
            userData.update(nextEvent);

            // Check for fraudulent patterns
            if (userData.getDistinctServicesCount() > MAX_DISTINCT_SERVICES) {
                flagUser(nextEvent.getUserID());
                log.info ("Alert: User " + nextEvent.getUserID() + " conducted transactions in more than 3 distinct services within a 5-minute window.");
            }

            if (nextEvent.getAmount() > userData.getAverageTransactionAmount() * TRANSACTION_AMOUNT_MULTIPLIER) {
                flagUser(nextEvent.getUserID());
                log.info("Alert: User " + nextEvent.getUserID() + " made a transaction 5x above the average amount in the last 24 hours.");
            }

            if (userData.isPingPongActivity(TEN_MINUTES)) {
                flagUser(nextEvent.getUserID());
                log.info("Alert: User " + nextEvent.getUserID() + " involved in ping-pong activity within 10 minutes.");
            }
        }

    }

    public ResponsePayload addTransactionEventToQueue(TransactionEvent transactionEvent) {
        try {
            transactionEventQueue.add(transactionEvent);
            return ResponsePayload.builder().message("Success").success(true).build();
        }
        catch (Exception exception)
        {
            return ResponsePayload.builder().message("failed").success(false).build();
        }
    }


    public  Queue<TransactionEvent> loadQueue() {
            Queue<TransactionEvent> transactionEventQueue= new PriorityQueue<TransactionEvent>((a,b)-> Math.toIntExact(b.getTimestamp() - a.getTimestamp()));
            transactionEventQueue.add(TransactionEvent.builder().timestamp(1617906000l).amount(150.00).userID("user1").serviceID("serviceA").build());
            transactionEventQueue.add(TransactionEvent.builder().timestamp(1617906060l).amount(4500.00).userID("user2").serviceID("serviceB").build());
            transactionEventQueue.add(TransactionEvent.builder().timestamp(1617906120l).amount(75.00).userID("user1").serviceID("serviceC").build());
            transactionEventQueue.add(TransactionEvent.builder().timestamp(1617906180l).amount(3000.00).userID("user3").serviceID("serviceA").build());
            transactionEventQueue.add(TransactionEvent.builder().timestamp(1617906240l).amount(200.00).userID("user1").serviceID("serviceB").build());
            transactionEventQueue.add(TransactionEvent.builder().timestamp(1617906300l).amount(4800.00).userID("user2").serviceID("serviceC").build());
            transactionEventQueue.add(TransactionEvent.builder().timestamp(1617906420l).amount(4900.00).userID("user3").serviceID("serviceB").build());
            transactionEventQueue.add(TransactionEvent.builder().timestamp(1617906480l).amount(120.00).userID("user1").serviceID("serviceD").build());

             return transactionEventQueue;

    }


}
