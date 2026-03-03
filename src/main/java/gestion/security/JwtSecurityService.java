package gestion.security;

import java.security.Key;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtSecurityService {

  @Value("${jwt.secret}")
  private String secretBase64;

  @Value("${jwt.expiration-ms:86400000}")
  private long expirationMs;

  private Key signingKey() {
    byte[] keyBytes = Decoders.BASE64.decode(secretBase64);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  public String generateToken(String subjectEmail, Collection<? extends GrantedAuthority> auth) {
    Map<String, Object> claims = Map.of(
        "roles", auth.stream().map(GrantedAuthority::getAuthority).toList()
    );

    return Jwts.builder()
        .setClaims(claims)
        .setSubject(subjectEmail)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
        .signWith(signingKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  public String extractSubject(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(signingKey())
        .build()
        .parseClaimsJws(token)
        .getBody()
        .getSubject();
  }

  public boolean isTokenValid(String token) {
    try {
      Jwts.parserBuilder()
          .setSigningKey(signingKey())
          .build()
          .parseClaimsJws(token);
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}

