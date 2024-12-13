package com.tunemate.be.global.jwt;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {
    @Value("${jwt.secret}")
    private String secret;

    private final Key key = Keys.hmacShaKeyFor(Encoders.BASE64.encode(secret.getBytes()).getBytes());

    public String generateToken(Long id, Date expiration) {
        Date now = new Date();

        return Jwts.builder()
                .subject(id.toString())
                .issuedAt(now)
                .expiration(expiration)
                .signWith(key)
                .compact();
    }

    public String getID(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith((SecretKey) key)  // replaced setSigningKey with verifyWith
                    .build()
                    .parseSignedClaims(token);  // replaced parseClaimsJws with parseSignedClaims
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
