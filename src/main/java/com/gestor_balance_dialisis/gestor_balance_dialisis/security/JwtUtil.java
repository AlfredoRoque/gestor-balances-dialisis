package com.gestor_balance_dialisis.gestor_balance_dialisis.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

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
    public String generateToken(String username,Long userId,String timeZone) {
        return Jwts.builder()
                .setSubject(username)
                .claim("zone", timeZone)
                .setId(String.valueOf(userId))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30)) // Token expires in 30 minutes
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

    /**
     * Extracts a specific claim from the given JWT token using a claims resolver function.
     *
     * @param token          The JWT token from which the claim is extracted.
     * @param claimsResolver A function that takes the claims and returns the desired claim value.
     * @param <T>            The type of the claim value to be returned.
     * @return The value of the specified claim extracted from the token.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extracts all claims from the given JWT token.
     *
     * @param token The JWT token from which the claims are extracted.
     * @return A Claims object containing all the claims present in the token.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Retrieves the signing key used for validating JWT tokens.
     *
     * @return A Key object representing the signing key derived from the secret.
     */
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }
}
