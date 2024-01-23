package com.learning.ratelimiter.filters;

import com.learning.ratelimiter.dto.Token;
import com.learning.ratelimiter.service.TokenBucketService;
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

//@Component
@Slf4j
public class TokenBucketFilter implements Filter {

//    @Autowired
    TokenBucketService tokenBucketService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            String remoteHost = request.getRemoteHost();
            Token token = tokenBucketService.getToken(remoteHost);
            log.info("Assigning token {} to ip {}", token, remoteHost);
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
