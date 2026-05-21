package hotel_booking.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;

@Service
public class JwtService {

    private final String SECRET = "my-secret-key-my-secret-key-my-secret-key"; // >=32 chars

    private Key getSignKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    // =============================
    public String generateToken(Integer userId, String role) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId)) // 🔥 dùng userId
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // 1 ngày
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // =============================
    public String generateRefreshToken(Integer userId, String role) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 7)) // 7 ngày
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // =============================
    public String extractUserId(String token) {
        return extractAllClaims(token).getSubject();
    }

    // =============================
    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    // =============================
    public Date getExpiration(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
    }

    // =============================
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // =============================
    public boolean isValid(String token) {
        try {
            extractAllClaims(token); // parse là đủ check
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}