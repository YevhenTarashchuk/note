package com.sacret.note.security.util;

import com.sacret.note.security.CustomUserDetails;
import com.sacret.note.security.token.JwtToken;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import com.sacret.note.model.enumeration.Role;
import com.sacret.note.model.enumeration.TokenType;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static com.sacret.note.constant.ExceptionConstant.INVALID_TOKEN;
import static com.sacret.note.constant.AuthConstants.ROLE_ATTRIBUTE;
import static com.sacret.note.constant.AuthConstants.TOKEN_TYPE_CLAIM;


@Service
@RequiredArgsConstructor
@Slf4j
public class JwtUtilService {

    @Value("${security.jwt.secretKey}")
    private String secretKey;
    @Value("${security.jwt.tokenExpirationTime}")
    private int tokenExpirationTime;
    @Value("${security.jwt.refreshTokenExpTime}")
    private int refreshTokenExpTime;

    public UserDetails buildUserDetails(Authentication authentication, TokenType expectedTokenType) {
        Claims claims = parseToken((String) authentication.getCredentials());

        String userId = claims.getId();
        TokenType tokenType = TokenType.valueOf((String) claims.get(TOKEN_TYPE_CLAIM));
        Role role = Role.valueOf((String) claims.get(ROLE_ATTRIBUTE));

        if (!expectedTokenType.equals(tokenType)) {
            throw new BadCredentialsException(INVALID_TOKEN);
        }

        return new CustomUserDetails(userId, role);
    }

    public JwtToken generateToken(CustomUserDetails userDetails) {
        Claims claims = Jwts.claims()
                .setId(String.valueOf(userDetails.getUserId()));
        claims.put(TOKEN_TYPE_CLAIM, TokenType.ACCESS_TOKEN);
        claims.put(ROLE_ATTRIBUTE, userDetails.getRole());

        String token = createToken(claims);
        return new JwtToken(token);
    }

    public JwtToken generateRefreshToken(CustomUserDetails userDetails) {
        Claims claims = Jwts.claims()
                .setId(String.valueOf(userDetails.getUserId()));
        claims.put(TOKEN_TYPE_CLAIM, TokenType.REFRESH_TOKEN);
        claims.put(ROLE_ATTRIBUTE, userDetails.getRole());

        String token = createRefreshToken(claims);
        return new JwtToken(token);
    }

    private String createToken(Claims claims) {
        Instant expirationDateTime = Instant.now().plus(tokenExpirationTime, ChronoUnit.MINUTES);
        long expirationDateTimeL = expirationDateTime.toEpochMilli();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(expirationDateTimeL))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    private String createRefreshToken(Claims claims) {
        Instant expirationDateTime = Instant.now().plus(refreshTokenExpTime, ChronoUnit.MINUTES);
        long expirationDateTimeL = expirationDateTime.toEpochMilli();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(expirationDateTimeL))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (UnsupportedJwtException | MalformedJwtException | IllegalArgumentException | SignatureException |
                 ExpiredJwtException ex) {
            log.error(INVALID_TOKEN, ex);
            throw new BadCredentialsException(INVALID_TOKEN, ex);
        }
    }
}
