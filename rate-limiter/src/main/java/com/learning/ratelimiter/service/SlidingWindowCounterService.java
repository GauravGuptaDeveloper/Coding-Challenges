package com.learning.ratelimiter.service;

import com.learning.ratelimiter.algorithm.SlidingWindowCounter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;

//@Service
@Slf4j
public class SlidingWindowCounterService {

    @Autowired
    private SlidingWindowCounter slidingWindowCounter;

    public boolean isRequestAllowed(String ip) {

        LocalDateTime currentWindowRecordTimestamp = LocalDateTime.now().withNano(0);

        LocalDateTime objectCurrentRecordWindowTimestamp = slidingWindowCounter.getCurrentRecordTimestamp();

        if (currentWindowRecordTimestamp.isEqual(objectCurrentRecordWindowTimestamp)) {
            if (slidingWindowCounter.getCurrentRecordCount() < slidingWindowCounter.getThreshold()) {
                checkAndSetIfFirstRequest();
                slidingWindowCounter.setCurrentRecordCount(slidingWindowCounter.getCurrentRecordCount() + 1);
                return true;
            } else {
                return false;
            }
        } else {
            if (isWindowExpired(objectCurrentRecordWindowTimestamp, currentWindowRecordTimestamp, slidingWindowCounter.getWindowFrameSize() * 1000)) {
                slidingWindowCounter.setPreviousRecordCount(slidingWindowCounter.getCurrentRecordCount());
                slidingWindowCounter.setCurrentRecordTimestamp(currentWindowRecordTimestamp);
                slidingWindowCounter.setCurrentRecordCount(0);
            } else {
                if (isRateLimited(currentWindowRecordTimestamp, objectCurrentRecordWindowTimestamp,
                        slidingWindowCounter)) {
                    return false;
                } else {
                    checkAndSetIfFirstRequest();
                    slidingWindowCounter
                        .setCurrentRecordCount(slidingWindowCounter.getCurrentRecordCount() + 1);
                }
            }
        }

        return true;
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

//      TODO : we don't kind a need of previousCount since we are using currentObjectCount as previousCount.
//              So we don't need this formula, but still keeping it over here.
//        long previousCount = slidingWindowCounter.getPreviousRecordCount();
//        long limit = (long) Math.ceil(previousCount * previousWindowWeight + currentObjectCount * currentPercentageWeight);

        return value > threshold;
//        return ((long) Math.ceil(previousWindowWeight) * currentObjectCount) >= threshold;
    }

    private void checkAndSetIfFirstRequest() {
        if (slidingWindowCounter.getCurrentRecordCount() == 0) {
            slidingWindowCounter.setCurrentRecordCount(1);
        }
    }

    private boolean isWindowExpired(LocalDateTime objectCurrentWindowTimestamp, LocalDateTime currentWindowTimestamp, long frameSize) {
        return (currentWindowTimestamp.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() - frameSize) >=
                objectCurrentWindowTimestamp.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        // currentTime - windowFrame > previousRecordedTimestamp.
    }

    /*
    public static void main(String[] args) {
        LocalDateTime d1 = LocalDateTime.now().withHour(9).withMinute(39).withSecond(04).withNano(0);
        LocalDateTime d2 = LocalDateTime.now().withHour(9).withMinute(38).withSecond(22).withNano(0);
        System.out.println(d1);
        System.out.println(d2);
        isRateLimited(d1, d2, 4, 5, 30_000);
    }

    public boolean isRequestAllowedV1(String ip) {

        LocalDateTime currentWindowTimestamp = LocalDateTime.now().withSecond(0).withNano(0);
        LocalDateTime previousWindowTimestamp = currentWindowTimestamp.minusSeconds(slidingWindowCounter.getWindowFrameSize());

        long currentRequestCount = 1; // for readability purpose.

        LocalDateTime objectPreviousWindowTimestamp = slidingWindowCounter.getPreviousWindowTimestamp();
        LocalDateTime objectCurrentWindowTimestamp = slidingWindowCounter.getCurrentWindowTimestamp();
        long objectPreviousCount = slidingWindowCounter.getPreviousCount();
        long objectCurrentCount = slidingWindowCounter.getCurrentCount();

        if (objectPreviousWindowTimestamp == null) {
            // initial case
            slidingWindowCounter.setPreviousWindowTimestamp(previousWindowTimestamp);
            slidingWindowCounter.setCurrentWindowTimestamp(currentWindowTimestamp);
            slidingWindowCounter.setPreviousCount(0);
            slidingWindowCounter.setCurrentCount(currentRequestCount);

            log.info("Initial Object {}", slidingWindowCounter);

            return true;
        }

        // this will always occur due to milliseconds. and
        // TODO : objectCurrentWindowTimestamp <= currentWindowTimestamp, "this is case condition" else below
        // percentage will be calculated for each request.
        if (!currentWindowTimestamp.isEqual(objectCurrentWindowTimestamp)) {
            if (previousWindowTimestamp.isEqual(objectCurrentWindowTimestamp)) {
                // you are at limit reached or greater!
                slidingWindowCounter.setPreviousWindowTimestamp(previousWindowTimestamp);
                slidingWindowCounter.setPreviousCount(objectCurrentCount);
            }
            // this else and else if are same, but I made it two so to learn about conditions.
            else if (previousWindowTimestamp.isAfter(objectCurrentWindowTimestamp)) {
                slidingWindowCounter.setPreviousCount(0);
                slidingWindowCounter.setPreviousWindowTimestamp(previousWindowTimestamp);
            } else {
                slidingWindowCounter.setPreviousCount(0);
                slidingWindowCounter.setPreviousWindowTimestamp(previousWindowTimestamp);
            }
            slidingWindowCounter.setCurrentCount(0);
            slidingWindowCounter.setCurrentWindowTimestamp(currentWindowTimestamp);
        }

        log.info("Sliding Window counter at {} is {}", currentWindowTimestamp, slidingWindowCounter);

        // this part will always run no matter what.
        long request = findRequestUsingWindowWeight(currentWindowTimestamp, objectCurrentWindowTimestamp, objectPreviousCount, objectCurrentCount);

        if (request > slidingWindowCounter.getThreshold()) {
            return false;
        }

        slidingWindowCounter.setCurrentCount(slidingWindowCounter.getCurrentCount() + currentRequestCount);
        return true;
    }

    private Long findRequestUsingWindowWeight(LocalDateTime currentWindowTimestamp, LocalDateTime objectCurrentWindowTimestamp, long objectPreviousCount, long objectCurrentCount) {
        long lowerBoundOfCurrentTimeStamp = Math.floorMod(objectCurrentWindowTimestamp.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(), slidingWindowCounter.getWindowFrameSize() * 1000);
        long midTimeStamp = currentWindowTimestamp.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() - lowerBoundOfCurrentTimeStamp;

        float currentWindowWeight = Float.parseFloat(decimalFormat.format(
                (float) (currentWindowTimestamp.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() - midTimeStamp) / (slidingWindowCounter.getWindowFrameSize() * 1000)
        ));

        float previousWindowWeight = 1 - currentWindowWeight;

        // agar abhi ka timestamp window ke currenttimestamp se match nhi karta, toh uss case mein currentWindowWeight*objectCurrentCount hamesha zero hoga
        // and uska matlab hai ki hum previous ka hi weight nikal rhe hai, and usse comparison kar rhe hai.

        log.info("Current weight is {}, previous weight is {} and value of current is {} and previous is {}",
                currentWindowWeight, previousWindowWeight, currentWindowWeight * objectCurrentCount, previousWindowWeight * objectPreviousCount);

        return (long) Math.ceil(currentWindowWeight * objectCurrentCount + previousWindowWeight * objectPreviousCount);
    }
     */
}
