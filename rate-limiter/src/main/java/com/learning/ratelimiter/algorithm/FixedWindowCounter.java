package com.learning.ratelimiter.algorithm;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

@Data
@Slf4j
public final class FixedWindowCounter {
    private int windowFrameSize;
    private int requests;

    private AtomicLong atomicLong;
//    private BlockingQueue<String> blockingQueue;

    ExecutorService executor = Executors.newFixedThreadPool(1);

    public FixedWindowCounter(int windowFrameSize, int requests) {
        this.windowFrameSize = windowFrameSize;
        this.requests = requests;

        this.atomicLong = new AtomicLong(0);
//        this.blockingQueue = new ArrayBlockingQueue<>(requests);

        executor.execute(this::emptyBlockingQueueAfterTInterval);
    }

    private void emptyBlockingQueueAfterTInterval() {
        while (true) {
            log.info("{} Starting at {} of blocking queue size as {}",
                    Thread.currentThread().getName(), LocalDateTime.now(),
//                    this.blockingQueue.size()
                    this.atomicLong.get()
            );
//            this.blockingQueue.clear();
            this.atomicLong.getAndSet(0);
            try {
                log.info("{} Sleeping for {}", Thread.currentThread().getName(), windowFrameSize * 1000L);
                Thread.sleep(windowFrameSize * 1000L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void putInQueue(String ip) {
        String timestamp = LocalDateTime.now().toString();
        try {
//            this.blockingQueue.add(timestamp);
            this.atomicLong.getAndIncrement();
//            log.info("Queue is {}", this.blockingQueue);
            log.info("Atomic long value is {}", this.atomicLong);
        } catch (IllegalStateException ex) {
            log.info("Dropping request due to full capacity {} at {}", ip, timestamp);
        }
    }

    public void pop() {
        try {
//            this.blockingQueue.take();
//            log.info("One request is complete! Dropping from queue. and now request size is {}",
//                    this.blockingQueue.size());
            this.atomicLong.getAndDecrement();
            log.info("Decementing the request which is complete!, {}", atomicLong.get());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
