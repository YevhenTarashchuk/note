package com.sacret.note.security.provider;

import com.sacret.note.constant.ExceptionConstant;
import com.sacret.note.exception.NotFoundException;
import com.sacret.note.model.response.UserDetailsResponse;
import com.sacret.note.security.CustomUserDetails;
import com.sacret.note.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Objects;


@Slf4j
@Service
@RequiredArgsConstructor
public class LoginAuthenticationProvider implements AuthenticationProvider {

    private final BCryptPasswordEncoder encoder;
    private final UserService userService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        log.debug("Attempting to verify user credentials");

        Assert.notNull(authentication, "No authentication data provided");

        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) authentication;

        String login = (String) authenticationToken.getPrincipal();
        String password = (String) authenticationToken.getCredentials();

        UserDetailsResponse response;
        try {
            response = userService.getUserDetails(login);
        } catch (NotFoundException e) {
            throw new AuthenticationServiceException(ExceptionConstant.INVALID_CREDENTIALS);
        }
        validateUser(password, response);

        CustomUserDetails userDetails = new CustomUserDetails(response);

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                response.getUserId(),
                null,
                null
        );

        token.setDetails(userDetails);

        return token;
    }

    public void validateUser(String password, UserDetailsResponse user) {
        if (Objects.isNull(password) || !encoder.matches(password, user.getPassword())) {
            throw new AuthenticationServiceException(ExceptionConstant.INVALID_CREDENTIALS);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
