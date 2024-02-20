package com.learning.ratelimiter.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class LimiterController {

    @GetMapping("limited")
    public String limitedApi() {
        return "Limited! don't over use me.";
    }

    @GetMapping("unlimited")
    public String unlimitedApi() {
        return "Unlimited! Let's Go!";
    }
}
