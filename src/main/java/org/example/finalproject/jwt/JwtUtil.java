package org.example.finalproject.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    private final String SECRET = "N2YxMzM2NjYtMjE0ZS00Y2Y4LWI1MDktYmU1YjY4YjhjOTk5";

    // 2 saat
    private final long ACCESS_EXPIRATION = 1000 * 60 * 5;
    // 30 gün
    private final long REFRESH_EXPIRATION = 1000 * 60 * 7;
    //1000L * 60 * 60 * 24 * 30;

    public String generateAccessToken(String email) {
        return generateToken(email, ACCESS_EXPIRATION);
    }

    public String generateRefreshToken(String email) {
        return generateToken(email, REFRESH_EXPIRATION);
    }

    private String generateToken(String email, long expirationMillis) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();
    }

    public String extractEmail(String token) {
        return getClaims(token).getSubject();
    }

    public boolean isValid(String token) {
        try {
            return !isExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isExpired(String token) {
        Date expiration = getClaims(token).getExpiration();
        return expiration.before(new Date());
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody();
    }
}
