
package com.opportunity360.security;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.expiration}")
    private long expiration;

    // Generate the signing key
    private SecretKey getSigningKey() {

        byte[] keyBytes = Decoders.BASE64.decode(secret);

        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Generate JWT token
    public String generateToken(String email) {

        Date now = new Date();

        Date expiryDate = new Date(
            now.getTime() + expiration
        );

        return Jwts.builder()
                .subject(email)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    // Extract email from token
    public String extractEmail(String token) {

        return extractAllClaims(token).getSubject();
    }

    // Validate token
    public boolean isTokenValid(String token, String email) {

        try {

            Claims claims = extractAllClaims(token);

            String tokenEmail = claims.getSubject();

            return tokenEmail.equals(email)
                    && claims.getExpiration().after(new Date());

        } catch (Exception e) {

            return false;
        }
    }

    // Extract all token claims
    private Claims extractAllClaims(String token) {

        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
