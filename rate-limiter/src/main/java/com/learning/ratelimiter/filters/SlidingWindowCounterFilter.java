package com.learning.ratelimiter.filters;

import com.learning.ratelimiter.service.SlidingWindowCounterRedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
public class SlidingWindowCounterFilter implements Filter {

//    @Autowired
//    SlidingWindowCounterService slidingWindowCountService;

    @Autowired
    SlidingWindowCounterRedisService slidingWindowCountService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            String remoteHost = request.getRemoteHost();
            boolean isRequestAllowed = slidingWindowCountService.isRequestAllowed(remoteHost);
            if (!isRequestAllowed) {
                throw new RuntimeException("Too many Requests");
            }
            log.info("Is Request allowed? {} to ip {}", isRequestAllowed, remoteHost);
            chain.doFilter(request, response);
        } catch (RuntimeException ex) {
            log.error("Caught runtime exception : {}", ex.getMessage());
            HttpServletResponse httpServletResponse = (HttpServletResponse) response;

            httpServletResponse.setContentType("text/html");
            httpServletResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            httpServletResponse.getWriter().write(ex.getMessage());
        }
    }


}
