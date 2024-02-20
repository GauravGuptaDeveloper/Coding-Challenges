package com.learning.ratelimiter.service;

import com.learning.ratelimiter.algorithm.FixedWindowCounter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FixedWindowCounterService {

//    @Autowired
    FixedWindowCounter fixedWindowCounter;

    public boolean isRequestAllowed() {
        return fixedWindowCounter.getAtomicLong().get() < fixedWindowCounter.getRequests();
    }

    public void completeRequest() {
        fixedWindowCounter.pop();
    }

    public void addRequest(String remoteHost) {
        fixedWindowCounter.putInQueue(remoteHost);
    }
}
