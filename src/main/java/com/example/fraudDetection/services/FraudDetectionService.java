package com.example.fraudDetection.services;

import com.example.fraudDetection.dto.ResponsePayload;
import com.example.fraudDetection.entity.FlaggedUser;
import com.example.fraudDetection.entity.TransactionEvent;
import com.example.fraudDetection.repository.IFlaggedUserRepository;
import com.example.fraudDetection.repository.ITransactionEventRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Log4j2
public class FraudDetectionService implements IFraudDetectionService {

    @Autowired
    private ITransactionEventRepository transactionEventRepository;
    @Autowired
    private IFlaggedUserRepository flaggedUserRepository;
    private static final long FIVE_MINUTES = 5 * 60 * 1000;
    private static final long TWENTY_FOUR_HOURS=24 * 60 * 60 * 1000;
    private static final long TEN_MINUTES = 10 * 60 * 1000;
    private static final int MAX_DISTINCT_SERVICES = 3;
    private static final double TRANSACTION_AMOUNT_MULTIPLIER = 5.0;
    Queue<TransactionEvent> transactionEventQueue= loadQueue();

    @Override
    @Scheduled(fixedRate = 4,timeUnit = TimeUnit.SECONDS)
    public void processTransactions() {
        synchronized(transactionEventQueue) {
            while (!transactionEventQueue.isEmpty() && transactionEventQueue.peek().getTimestamp() <= System.currentTimeMillis()) {
                TransactionEvent nextEvent = transactionEventQueue.poll();

                FlaggedUser flaggedUser = flaggedUserRepository.findByUserId(nextEvent.getUserID());
                if (flaggedUser != null) {
                    log.info(flaggedUser.getFlaggedReason());
                    return;
                }
                if (isDistinctServicesWithinTimeWindow(nextEvent)) {
                    return;
                }
                if (isHighAmountTransactions(nextEvent)) {
                    return;
                }
                if (isPingPongActivity(nextEvent)) {
                    return;
                }
                update(nextEvent);
            }
        }
    }
    @Override
    public ResponsePayload addTransactionEventToQueue(TransactionEvent transactionEvent) {
        try {
            synchronized(transactionEventQueue) {
                transactionEventQueue.add(transactionEvent);
            }
            return ResponsePayload.builder().message("Success").success(true).build();
        }
        catch (Exception exception) {
            return ResponsePayload.builder().message("failed").success(false).build();
        }
    }

    public  Queue<TransactionEvent> loadQueue() {
        Queue<TransactionEvent> transactionEventQueue= new PriorityQueue<TransactionEvent>((a,b)-> Math.toIntExact(a.getTimestamp() - b.getTimestamp()));
        transactionEventQueue.add(TransactionEvent.builder().timestamp(1617906000l).amount(150.00).userID("user1").serviceID("serviceA").build());
        transactionEventQueue.add(TransactionEvent.builder().timestamp(1617906060l).amount(4500.00).userID("user2").serviceID("serviceB").build());
        transactionEventQueue.add(TransactionEvent.builder().timestamp(1617906120l).amount(75.00).userID("user1").serviceID("serviceC").build());
        transactionEventQueue.add(TransactionEvent.builder().timestamp(1617906180l).amount(3000.00).userID("user3").serviceID("serviceA").build());
        transactionEventQueue.add(TransactionEvent.builder().timestamp(1617906240l).amount(200.00).userID("user1").serviceID("serviceB").build());
        transactionEventQueue.add(TransactionEvent.builder().timestamp(1617906300l).amount(900800.00).userID("user2").serviceID("serviceC").build());
        transactionEventQueue.add(TransactionEvent.builder().timestamp(1617906420l).amount(4900.00).userID("user3").serviceID("serviceB").build());
        transactionEventQueue.add(TransactionEvent.builder().timestamp(1617906480l).amount(120.00).userID("user1").serviceID("serviceD").build());
        transactionEventQueue.add(TransactionEvent.builder().timestamp(1618906540l).amount(5000.00).userID("user3").serviceID("serviceC").build());
        transactionEventQueue.add(TransactionEvent.builder().timestamp(1618906550l).amount(5000.00).userID("user3").serviceID("serviceB").build());
        transactionEventQueue.add(TransactionEvent.builder().timestamp(1618906590l).amount(5000.00).userID("user3").serviceID("serviceC").build());
        transactionEventQueue.add(TransactionEvent.builder().timestamp(1618906600l).amount(5000.00).userID("user3").serviceID("serviceB").build());
        transactionEventQueue.add(TransactionEvent.builder().timestamp(1618906620l).amount(5000.00).userID("user3").serviceID("serviceC").build());
        return transactionEventQueue;
    }

    private Boolean isDistinctServicesWithinTimeWindow(TransactionEvent event) {
        long fiveMinutesAgo = event.getTimestamp() - FIVE_MINUTES;
        List<TransactionEvent> recentTransactions = transactionEventRepository.findAllByTimestampAfterAndUserID(fiveMinutesAgo, event.getUserID());
        Set<String> set= getServiceSet(recentTransactions);
        set.add(event.getServiceID());
        for (TransactionEvent transactionEvent:recentTransactions) {
            set.add(transactionEvent.getServiceID());
        }
        if (set.size() > MAX_DISTINCT_SERVICES) {
            FlaggedUser flaggedUser= FlaggedUser.builder().userId(event.getUserID()).flaggedReason("Alert: User " +
                    event.getUserID() + " conducted transactions in more than 3 distinct services within 5 minutes.")
                    .build();
            flaggedUserRepository.save(flaggedUser);
            log.info(flaggedUser.getFlaggedReason());
            return true;
        }
        return false;
    }

    private Boolean isHighAmountTransactions(TransactionEvent event) {
        long twentyFourHoursAgo = event.getTimestamp() - TWENTY_FOUR_HOURS;
        List<TransactionEvent> last24HoursTransactions = transactionEventRepository.findAllByTimestampAfterAndUserID(twentyFourHoursAgo,event.getUserID());
        Double averageAmount = last24HoursTransactions.stream()
                .mapToDouble(TransactionEvent::getAmount)
                .average()
                .orElse(0);
        if (averageAmount!=0&& event.getAmount() > averageAmount * TRANSACTION_AMOUNT_MULTIPLIER) {
            FlaggedUser flaggedUser= FlaggedUser.builder().userId(event.getUserID()).flaggedReason("Alert: User " +
                    event.getUserID() + " conducted a transaction 5x above their average amount in the last 24 hours.")
                    .build();
            flaggedUserRepository.save(flaggedUser);
            log.info(flaggedUser.getFlaggedReason());
            return true;
        }
        return false;
    }

    private Boolean isPingPongActivity(TransactionEvent event) {
        long tenMinutesAgo = event.getTimestamp() - TEN_MINUTES;
        List<TransactionEvent> lastTenMinutesTransactions = transactionEventRepository.findAllByTimestampAfterAndUserID(
                tenMinutesAgo, event.getUserID());
        if (lastTenMinutesTransactions != null && lastTenMinutesTransactions.size() > 2) {
            Set<String> serviceSet = getServiceSet(lastTenMinutesTransactions);
            if (serviceSet.size() == 2 && isPingPongPattern(lastTenMinutesTransactions, serviceSet)) {
                return handlePingPongActivity(serviceSet,event);
            }
        }
        return false;
    }

    private Set<String> getServiceSet(List<TransactionEvent> transactions) {
        Set<String> serviceSet = new HashSet<>();
        for (TransactionEvent transaction : transactions) {
            serviceSet.add(transaction.getServiceID());
        }
        return serviceSet;
    }
    private boolean isPingPongPattern(List<TransactionEvent> transactions, Set<String> serviceSet) {
        Iterator<String> iterator = serviceSet.iterator();
        String service1 = iterator.next();
        String service2 = iterator.next();
        for (int counter = 0; counter < transactions.size() - 1; counter++) {
            TransactionEvent transaction1 = transactions.get(counter);
            TransactionEvent transaction2 = transactions.get(counter + 1);
            if (!(transaction1.getServiceID().equals(service1) && transaction2.getServiceID().equals(service2)) &&
                    !(transaction1.getServiceID().equals(service2) && transaction2.getServiceID().equals(service1))) {
                return false;
            }
        }
        return true;
    }
    private boolean handlePingPongActivity(Set<String> serviceSet,TransactionEvent event) {
        Iterator<String> iterator = serviceSet.iterator();
        String service1 = iterator.next();
        String service2 = iterator.next();
        FlaggedUser flaggedUser = FlaggedUser.builder()
                .userId(event.getUserID())
                .flaggedReason("Alert: User " + event.getUserID() +
                        " is conducting ping-pong activity between services " + service1 +
                        " and " + service2)
                .build();
        flaggedUserRepository.save(flaggedUser);
        log.info(flaggedUser.getFlaggedReason());
        return true;
    }
    private void update(TransactionEvent event) {
        transactionEventRepository.save(event);
    }
}
