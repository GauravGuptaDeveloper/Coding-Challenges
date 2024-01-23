package com.learning.ratelimiter.service;

import com.learning.ratelimiter.algorithm.SlidingWindowLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentLinkedDeque;

//@Component
@Slf4j
public class SlidingWindowLogService {

//    @Autowired
    private SlidingWindowLog slidingWindowLog;

    public boolean isRequestAllowed(String ip) {
        LocalDateTime currentTimeStamp = LocalDateTime.now();
        int windowTime = slidingWindowLog.getWindowtime();

        LocalDateTime minusSecondsWindowTime = currentTimeStamp.minusSeconds(windowTime);

        log.info("Need to check between {} and {}", minusSecondsWindowTime, currentTimeStamp);

        ConcurrentLinkedDeque<SlidingWindowLog.SlidingWindow> slidingWindowList = slidingWindowLog.getSlidingWindowList();

        log.info("Before performing sliding operation, window list is {}", slidingWindowList);

        while (!slidingWindowList.isEmpty()) {
            SlidingWindowLog.SlidingWindow first = slidingWindowList.getFirst();
            boolean before = first.getLocalDateTime().isBefore(minusSecondsWindowTime);
            if (!before) {
                break;
            } else {
                slidingWindowList.removeFirst();
            }
        }

        log.info("After performing sliding operation,{} and {} ==" +
                        " window list is {}", slidingWindowLog.getThreshold(),
                slidingWindowList.size(), slidingWindowList);

        if (slidingWindowList.size() > slidingWindowLog.getThreshold()) {
            return false;
        } else {
            slidingWindowList.addLast(new SlidingWindowLog.SlidingWindow(ip, currentTimeStamp));
            return true;
        }
    }

}
