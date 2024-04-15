package com.cgi.example.loadtest.memory;

import lombok.Getter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Getter
public class MemoryUsageMetric {

    private final LocalDateTime collectionTime;
    private final BigInteger memoryUsedInBytes;

    public MemoryUsageMetric(String memoryUsedInBytes) {
        this.collectionTime = LocalDateTime.now();
        this.memoryUsedInBytes = new BigDecimal(memoryUsedInBytes).toBigInteger();
    }

    public BigInteger getMemoryUsedInBytes() {
        return memoryUsedInBytes;
    }

    @Override
    public String toString() {
        return "MemoryUsageMetric{" +
                "collectionTime=" + collectionTime +
                ", memoryUsedInBytes=" + memoryUsedInBytes +
                '}';
    }

    public LocalDateTime getCollectionTime() {
        return collectionTime;
    }
}
