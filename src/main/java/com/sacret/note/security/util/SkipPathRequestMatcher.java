package com.sacret.note.security.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.sacret.note.constant.AuthConstants.POST_PATH;

public class SkipPathRequestMatcher implements RequestMatcher {

    private final OrRequestMatcher matcher;
    private final RequestMatcher processingMatcher;

    public SkipPathRequestMatcher(List<String> pathsToSkip, String processingPath) {
        Objects.requireNonNull(pathsToSkip);

        List<RequestMatcher> matchers = pathsToSkip.stream()
                .map(AntPathRequestMatcher::new)
                .collect(Collectors.toList());
        matchers.add(new AntPathRequestMatcher(POST_PATH, HttpMethod.GET.name()));

        matcher = new OrRequestMatcher(matchers);

        processingMatcher = new AntPathRequestMatcher(processingPath);
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        if (matcher.matches(request)) {
            return false;
        }
        return processingMatcher.matches(request);
    }

}