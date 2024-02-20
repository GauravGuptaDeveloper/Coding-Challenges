package com.learning.ratelimiter.algorithm;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SlidingWindowCounter {

    private long windowFrameSize;
    private long threshold;
    private long previousRecordCount;
    private long currentRecordCount;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime currentRecordTimestamp;

    public SlidingWindowCounter(long windowFrameSizeSeconds, long threshold) {
        this.threshold = threshold;
        this.windowFrameSize = windowFrameSizeSeconds;

        currentRecordCount = 0;
        currentRecordTimestamp = LocalDateTime.now().withNano(0);

        previousRecordCount = 0;
    }
}
