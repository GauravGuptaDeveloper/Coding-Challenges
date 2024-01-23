package com.learning.ratelimiter.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.ratelimiter.algorithm.SlidingWindowCounter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
@Slf4j
public class SlidingWindowCounterRedisService {

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    private SlidingWindowCounter slidingWindowCounter;

    @Autowired
    private ObjectMapper objectMapper;

    public boolean isRequestAllowed(String ip) {

        boolean isAllowed = true;

        String cachedValue = redisTemplate.opsForValue().get(ip);

        if (cachedValue != null) {
            try {
                slidingWindowCounter = objectMapper.readValue(cachedValue, SlidingWindowCounter.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        LocalDateTime currentWindowRecordTimestamp = LocalDateTime.now().withNano(0);

        LocalDateTime objectCurrentRecordWindowTimestamp = slidingWindowCounter.getCurrentRecordTimestamp();

        if (currentWindowRecordTimestamp.isEqual(objectCurrentRecordWindowTimestamp)) {
            if (slidingWindowCounter.getCurrentRecordCount() < slidingWindowCounter.getThreshold()) {
                checkAndSetIfFirstRequest();
                slidingWindowCounter.setCurrentRecordCount(slidingWindowCounter.getCurrentRecordCount() + 1);
            } else {
                isAllowed = false;
            }
        } else {
            if (isWindowExpired(objectCurrentRecordWindowTimestamp, currentWindowRecordTimestamp, slidingWindowCounter.getWindowFrameSize() * 1000)) {
                slidingWindowCounter.setPreviousRecordCount(slidingWindowCounter.getCurrentRecordCount());
                slidingWindowCounter.setCurrentRecordTimestamp(currentWindowRecordTimestamp);

                slidingWindowCounter.setCurrentRecordCount(1);
            } else {
                if (isRateLimited(currentWindowRecordTimestamp, objectCurrentRecordWindowTimestamp,
                        slidingWindowCounter)) {
                    isAllowed = false;
                } else {
                    checkAndSetIfFirstRequest();
                    slidingWindowCounter
                            .setCurrentRecordCount(slidingWindowCounter.getCurrentRecordCount() + 1);
                }
            }
        }

        try {
            redisTemplate.opsForValue().set(ip, objectMapper.writeValueAsString(slidingWindowCounter));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return isAllowed;
    }

    private void checkAndSetIfFirstRequest() {
        if (slidingWindowCounter.getCurrentRecordCount() == 0) {
            slidingWindowCounter.setCurrentRecordCount(1);
        }
    }

    private boolean isRateLimited(LocalDateTime currentWindowRecordTimestamp,
                                  LocalDateTime objectCurrentRecordWindowTimestamp,
                                  SlidingWindowCounter slidingWindowCounter) {

        long currentObjectCount = slidingWindowCounter.getCurrentRecordCount();
        long threshold = slidingWindowCounter.getThreshold();
        long windowFrameSize = slidingWindowCounter.getWindowFrameSize();
        long currentTimestampEpoch = currentWindowRecordTimestamp.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long objectRecordEpoch = objectCurrentRecordWindowTimestamp.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        float currentPercentageWeight = (float) (currentTimestampEpoch - objectRecordEpoch) / (windowFrameSize * 1000);

        float previousWindowWeight = 1 - currentPercentageWeight;

        log.info("CurrentWindowTimestamp is {} and objectRecordTimeStamp is {}",
                currentWindowRecordTimestamp, objectCurrentRecordWindowTimestamp);

        log.info("PERCENTAGE IS {} and {} -- {}, {}",
                previousWindowWeight, currentPercentageWeight, slidingWindowCounter.getPreviousRecordCount(), currentObjectCount);

        long value = (long) Math.ceil(previousWindowWeight * slidingWindowCounter.getPreviousRecordCount())
                + currentObjectCount;

        log.info("Hence value is {}", value);

        return value > threshold;
//        return ((long) Math.ceil(previousWindowWeight) * currentObjectCount) >= threshold;
    }

    private boolean isWindowExpired(LocalDateTime objectCurrentWindowTimestamp, LocalDateTime currentWindowTimestamp, long frameSize) {
        return (currentWindowTimestamp.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() - frameSize) >=
                objectCurrentWindowTimestamp.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

}
