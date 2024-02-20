package com.learning.ratelimiter.algorithm;

import com.learning.ratelimiter.dto.Token;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class TokenBucket {

    ConcurrentHashMap<String, BlockingQueue<Token>> ipBasedTokenBucket = new ConcurrentHashMap<>();
    int threshold;

    public TokenBucket(int n) {
        this.threshold = n;
    }

    public boolean registerIpBucket(String ip) {
        if (ipBasedTokenBucket.containsKey(ip)) {
            log.debug("Already registered ip {}", ip);
            return false;
        } else {
            // for the first time, we will set threshold of queue and insert tokens.
            BlockingQueue<Token> value = new ArrayBlockingQueue<>(threshold);
            for (int idx = 0; idx < threshold; idx++) {
                value.add(new Token(UUID.randomUUID().toString(), LocalDateTime.now()));
            }

            ipBasedTokenBucket.put(ip, value);
            return true;
        }
    }

    public void deregisterIpFromBucket(String ip) {
        ipBasedTokenBucket.remove(ip);
    }

    @Scheduled(cron = "*/30 * * * * ?")
    public void insertTokenPerIp() {
        if (ipBasedTokenBucket.isEmpty()) {
            log.info("IpBased Token Bucket is empty");
            return;
        }
        for (Map.Entry<String, BlockingQueue<Token>> entry : ipBasedTokenBucket.entrySet()) {
            Queue<Token> value = entry.getValue();
            if (value.size() == threshold) {
                //.. Discard the token.
                log.info("Bucket is full! Therefore, no need to add more tokens." +
                        "Printing ip {} ipBasedTokenBucket {}", entry.getKey(), ipBasedTokenBucket);
            } else {
            // TODO : no need to add this loop right now!

//                while (value.size() != threshold) {
                    value.add(new Token(UUID.randomUUID().toString(), LocalDateTime.now()));
//                }
            }
        }
        log.info("IPBasedTokenBucket is {}", ipBasedTokenBucket);
    }

    public ConcurrentHashMap<String, BlockingQueue<Token>> getIpBasedTokenBucket() {
        return ipBasedTokenBucket;
    }
}
