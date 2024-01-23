package com.learning.ratelimiter;

import com.learning.ratelimiter.algorithm.FixedWindowCounter;
import com.learning.ratelimiter.algorithm.SlidingWindowCounter;
import com.learning.ratelimiter.algorithm.SlidingWindowLog;
import com.learning.ratelimiter.algorithm.TokenBucket;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RateLimiterApplication {

    public static void main(String[] args) {
        SpringApplication.run(RateLimiterApplication.class, args);
    }

    //    @Bean
    public TokenBucket tokenBucket() {
        return new TokenBucket(5);
    }

    //    @Bean
    public FixedWindowCounter fixedWindowCounter() {
        return new FixedWindowCounter(30, 5);
    }

    //    @Bean
    public SlidingWindowLog slidingWindowLog() {
        return new SlidingWindowLog(30, 5);
    }

    @Bean
    public SlidingWindowCounter slidingWindowCounter() {
        return new SlidingWindowCounter(120L, 7L);
    }

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
        jedisConnectionFactory.setHostName("localhost");
        jedisConnectionFactory.setPort(6379);
        return jedisConnectionFactory;
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate() {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());

        return template;
    }
}
