package com.example.fraudDetection.services;

import com.example.fraudDetection.dto.TransactionEvent;

import java.util.HashMap;
import java.util.Map;


public  class UserTransactionData {
    private Map<String, Long> serviceTimestampMap = new HashMap<>();
    private long totalAmount = 0;
    private int transactionCount = 0;
    private static final long FIVE_MINUTES = 5 * 60 * 1000;

    public void update(TransactionEvent event) {
        serviceTimestampMap.put(event.getServiceID(), event.getTimestamp());
        totalAmount += event.getAmount();
        transactionCount++;

        // Remove old entries from the map
        long windowStart = event.getTimestamp() - FIVE_MINUTES;
        serviceTimestampMap.entrySet().removeIf(entry -> entry.getValue() < windowStart);
    }

    public int getDistinctServicesCount() {
        return serviceTimestampMap.size();
    }

    public double getAverageTransactionAmount() {
        return (transactionCount == 0) ? 0 : totalAmount / (double) transactionCount;
    }

    public boolean isPingPongActivity(long timeThreshold) {
        if (serviceTimestampMap.size() < 2) {
            return false;
        }

        long latestTimestamp = serviceTimestampMap.values().stream().max(Long::compareTo).orElse(0L);
        return serviceTimestampMap.values().stream().anyMatch(timestamp ->
                latestTimestamp - timestamp < timeThreshold);
    }
}