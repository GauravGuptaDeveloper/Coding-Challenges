package com.learning.ratelimiter.service;

import com.learning.ratelimiter.algorithm.TokenBucket;
import com.learning.ratelimiter.dto.Token;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class TokenBucketService {

//    @Autowired
    TokenBucket tokenBucket;

    public Token getToken(String ip) {
        /*
        check if ip exist?
            if exists then check for count of token.
        Else
            register this ip and insert one token in bucket.
         */
        tokenBucket.registerIpBucket(ip);
        ConcurrentHashMap<String, BlockingQueue<Token>> ipBasedTokenBucket = tokenBucket.getIpBasedTokenBucket();
        if (ipBasedTokenBucket.containsKey(ip)) {
            BlockingQueue<Token> tokens = ipBasedTokenBucket.get(ip);
            if (!tokens.isEmpty()) {
                return tokens.poll();
            } else {
                throw new RuntimeException("Please try again. No Tokens found for this IP, " +
                        "Rate Limit is reached! Dropping this request");
            }
        } else {
            // never happening case.
            throw new RuntimeException("IP is not registered please check");
        }
    }
}
