package com.sacret.note.security.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpMethod;

import java.util.Objects;
import java.util.function.Predicate;

import static com.sacret.note.constant.AuthConstants.HEADER_PARAM_JWT_TOKEN;
import static com.sacret.note.constant.AuthConstants.POST_PATH;

public enum JwtAuthType {

    ANONYMOUS(httpServletRequest -> {
        Predicate<HttpServletRequest> headerPredicate = request ->
                Objects.isNull(request.getHeader(HEADER_PARAM_JWT_TOKEN));
        Predicate<HttpServletRequest> pathPredicate = request ->
                Objects.equals(request.getRequestURI(), POST_PATH);
        Predicate<HttpServletRequest> methodPredicate = request ->
                Objects.equals(request.getMethod(), HttpMethod.POST.name());
        Predicate<HttpServletRequest> requestPredicate = headerPredicate.and(pathPredicate).and(methodPredicate);
        return requestPredicate.test(httpServletRequest);
    }),
    COMMON(request -> true);

    private final Predicate<HttpServletRequest> requestDetector;

    JwtAuthType(Predicate<HttpServletRequest> requestDetector) {
        this.requestDetector = requestDetector;
    }

    public static JwtAuthType detectJwtAuthType(HttpServletRequest request) {
        for (JwtAuthType jwtAuthType : values()) {
            if (jwtAuthType.requestDetector.test(request)) {
                return jwtAuthType;
            }
        }
        return null;
    }
}
