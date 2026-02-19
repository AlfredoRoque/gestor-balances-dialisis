package com.gestor_balance_dialisis.gestor_balance_dialisis.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Utility class for generating and validating JWT tokens.
 * This class provides methods to create a JWT token for a given username and to extract the username from a token.
 * The tokens are signed using the HS256 algorithm and have a configurable expiration time.
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String SECRET;

    /**
     * Generates a JWT token for the given username.
     *
     * @param username The username for which the token is generated.
     * @return A JWT token containing the username as the subject, with an expiration time of 30 minutes.
     */
    public String generateToken(String username,Long userId) {
        return Jwts.builder()
                .setSubject(username)
                .setId(String.valueOf(userId))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 80000)) // 80 seconds expiration time
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extracts the username from the given JWT token.
     *
     * @param token The JWT token from which the username is extracted.
     * @return The username contained in the token's subject.
     */
    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
