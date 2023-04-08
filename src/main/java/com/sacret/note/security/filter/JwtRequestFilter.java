package com.sacret.note.security.filter;

import com.sacret.note.security.CustomUserDetails;
import com.sacret.note.security.token.JwtAuthenticationToken;
import com.sacret.note.security.util.JwtAuthType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.RequestMatcher;
import com.sacret.note.constant.AuthConstants;
import com.sacret.note.security.util.TokenExtractor;

import java.io.IOException;

import static com.sacret.note.constant.AuthConstants.HEADER_PARAM_JWT_TOKEN;


@Slf4j
public class JwtRequestFilter extends AbstractAuthenticationProcessingFilter {

    private final AuthenticationFailureHandler failureHandler;
    private final TokenExtractor tokenExtractor;

    public JwtRequestFilter(AuthenticationFailureHandler failureHandler, TokenExtractor tokenExtractor, RequestMatcher matcher) {
        super(matcher);
        this.failureHandler = failureHandler;
        this.tokenExtractor = tokenExtractor;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        log.debug("Attempting to authenticate in JWT processing filter");
        JwtAuthType authType = JwtAuthType.detectJwtAuthType(request);
        String jwt = tokenExtractor.extract(request.getHeader(HEADER_PARAM_JWT_TOKEN), authType);
        return getAuthenticationManager().authenticate(new JwtAuthenticationToken(jwt, authType));
    }

    @Override
    protected void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication authResult
    ) throws IOException, ServletException {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authResult);
        SecurityContextHolder.setContext(context);
        JwtAuthenticationToken token = (JwtAuthenticationToken) authResult;

        if (token.isAuthenticated()) {
            CustomUserDetails details = (CustomUserDetails) token.getPrincipal();
            request.setAttribute(AuthConstants.USER_ID_ATTRIBUTE, details.getUserId());
        }

        chain.doFilter(request, response);
    }

    @Override
    protected void unsuccessfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException failed
    ) throws IOException, ServletException {
        log.debug("UN - Successful JWT Authentication: {}", failed.getMessage());
        failed.printStackTrace();
        SecurityContextHolder.clearContext();
        failureHandler.onAuthenticationFailure(request, response, failed);
    }
}
