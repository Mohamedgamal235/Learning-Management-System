//package com.project.userManagement.Service;
import com.lms.Learning_Managment_System.Service;
import org.springframework.beans.factory.annotation.Value;
import io.jsonwebtoken.*;
import io.jsonwebtoken.Jwts;
import java.util.Date;
import org.springframework.stereotype.Service;
import static io.jsonwebtoken.Jwts.*;
@Service
public class jwt {
    @Value("${jwt.secret}")
    private String secret; //in application.properties
    @Value("${jwt.expiration}")
    private long expiration;

    // Generate a JWT token
    public String generateToken(String email, String role) {
        return builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }
    /*
    public String getRoleFromToken(String token) {
        try {
            String tokenWithoutBearer = token.startsWith("Bearer ") ? token.substring(7) : token;
            JwtParser parser = Jwts.parserBuilder() // if it does not give error use this
                    .setSigningKey(secret)
                    .build();
            Jws<Claims> claimsJws = parser.parseClaimsJws(tokenWithoutBearer);
            return claimsJws.getBody().get("role", String.class);
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }
*/
    public String getRoleFromToken(String token) {
        try {
            String tokenWithoutBearer = token.startsWith("Bearer ") ? token.substring(7) : token;
            JwtParser parser = (JwtParser) Jwts.parser().setSigningKey(secret);
            Jws<Claims> claimsJws = parser.parseClaimsJws(tokenWithoutBearer);
            return claimsJws.getBody().get("role", String.class);
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }

}
