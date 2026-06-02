package gob.imss.mx.catalogs.config;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * Utilidad para generar y validar JSON Web Tokens (JWT).
 *
 * Esta clase encapsula la firma, la detección de expiración y la extracción de
 * claims del token firmado usado en la aplicación.
 */
@Component
public class JwtUtil {

    private final SecretKey SECRETA = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256);
    private final long EXPIRATION_TIME = 1000 * 60 * 60 * 8; // 8 Horas

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // EXTRAER ROLES: Recupera la lista de roles guardados en el token
    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        return extractClaim(token, claims -> claims.get("roles", List.class));
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(SECRETA)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // MODIFICADO: Ahora recibe la colección de roles autorizados
    public String generateToken(String username, List<String> roles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roles); // Guardamos los roles en el cuerpo del JWT
        
        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRETA)
                .compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private Boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

}
