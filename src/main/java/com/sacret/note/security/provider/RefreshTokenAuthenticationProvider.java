package com.sacret.note.security.provider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import com.sacret.note.model.enumeration.TokenType;
import com.sacret.note.security.token.RefreshTokenAuthenticationToken;
import com.sacret.note.security.util.JwtUtilService;

@Slf4j
@Component
@RequiredArgsConstructor
public class RefreshTokenAuthenticationProvider implements AuthenticationProvider {

    private final JwtUtilService jwtUtilService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        log.debug("Attempting to parse token");
        UserDetails userDetails = jwtUtilService.buildUserDetails(authentication, TokenType.REFRESH_TOKEN);
        return new RefreshTokenAuthenticationToken(userDetails, userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(RefreshTokenAuthenticationToken.class);
    }

}
