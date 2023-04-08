package com.sacret.note.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import com.sacret.note.exception.ErrorCode;
import com.sacret.note.exception.ErrorResponseAuth;
import com.sacret.note.security.exception.AuthMethodNotSupportedException;
import com.sacret.note.security.exception.JwtTokenExpiredException;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class AuthFailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper mapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException e) throws IOException {
        log.debug("Failed to authenticate: " + e.getMessage(), e);

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        if (e instanceof JwtTokenExpiredException) {
            mapper.writeValue(response.getWriter(), ErrorResponseAuth.of("Token has expired",
                    ErrorCode.JWT_TOKEN_EXPIRED, HttpStatus.UNAUTHORIZED));
        } else if (e instanceof AuthMethodNotSupportedException) {
            mapper.writeValue(response.getWriter(), ErrorResponseAuth.of(e.getMessage(),
                    ErrorCode.AUTHENTICATION, HttpStatus.UNAUTHORIZED));
        } else if (e instanceof UsernameNotFoundException) {
            mapper.writeValue(response.getWriter(), ErrorResponseAuth.of(e.getMessage(),
                    ErrorCode.AUTHENTICATION, HttpStatus.UNAUTHORIZED));
        }

        mapper.writeValue(response.getWriter(), ErrorResponseAuth.of(e.getMessage(),
                ErrorCode.AUTHENTICATION, HttpStatus.UNAUTHORIZED));
    }

}
