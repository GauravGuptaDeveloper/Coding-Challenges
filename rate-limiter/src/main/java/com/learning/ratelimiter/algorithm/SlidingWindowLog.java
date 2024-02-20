package com.learning.ratelimiter.algorithm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentLinkedDeque;

public class SlidingWindowLog {

    private ConcurrentLinkedDeque<SlidingWindow> slidingWindowList;
    private int windowtime;
    private int threshold;

    public SlidingWindowLog(int windowtime, int request) {
        this.windowtime = windowtime;
        this.threshold = request;
        this.slidingWindowList = new ConcurrentLinkedDeque<>();
    }

    public ConcurrentLinkedDeque<SlidingWindow> getSlidingWindowList() {
        return slidingWindowList;
    }

    public int getWindowtime() {
        return windowtime;
    }

    public int getThreshold() {
        return threshold;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SlidingWindow {
        private String requestIP;
        private LocalDateTime localDateTime;
    }

}
