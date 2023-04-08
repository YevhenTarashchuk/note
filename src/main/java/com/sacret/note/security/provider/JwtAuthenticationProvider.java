package com.sacret.note.security.provider;

import com.sacret.note.security.util.JwtAuthType;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import com.sacret.note.model.enumeration.TokenType;
import com.sacret.note.security.token.JwtAuthenticationToken;
import com.sacret.note.security.util.JwtUtilService;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private static final Map<JwtAuthType, Function<Authentication, JwtAuthenticationToken>> AUTH_MAP = new EnumMap<>(JwtAuthType.class);

    private final JwtUtilService jwtUtilService;

    @PostConstruct
    public void initAuthMap() {
        AUTH_MAP.put(JwtAuthType.ANONYMOUS, this::buildAnonymousJwtAuthentication);
        AUTH_MAP.put(JwtAuthType.COMMON, this::buildCommonJwtAuthentication);
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        log.debug("Attempting to parse token");
        JwtAuthType authType = ((JwtAuthenticationToken) authentication).getAuthType();
        return AUTH_MAP.get(authType).apply(authentication);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(JwtAuthenticationToken.class);
    }

    private JwtAuthenticationToken buildCommonJwtAuthentication(Authentication authentication) {
        UserDetails userDetails = jwtUtilService.buildUserDetails(authentication, TokenType.ACCESS_TOKEN);
        return new JwtAuthenticationToken(userDetails, userDetails.getAuthorities());
    }

    private JwtAuthenticationToken buildAnonymousJwtAuthentication(Authentication authentication) {
        return new JwtAuthenticationToken(null, JwtAuthType.ANONYMOUS);
    }
}
