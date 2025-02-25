package org.example.jobify.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.example.jobify.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JwtService {

//    @Value("${jwt.secret}") // Load from application.properties
//    private String secretKey;
//
//    @Value("${jwt.expiration}") // Load token expiration time
//    private long jwtExpirationMs;
//
//    // Generate a JWT token for a user
//    public String generateToken(UserDetails userDetails) {
//        return generateToken(new HashMap<>(), userDetails);
//    }
//
//    // Generate a JWT token with extra claims
//    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
//        return Jwts.builder()
//                .setClaims(extraClaims)
//                .setSubject(userDetails.getUsername()) // Email as subject
//                .setIssuedAt(new Date(System.currentTimeMillis()))
//                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
//                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
//                .compact();
//    }
//
//    // Validate JWT token
//    public boolean isTokenValid(String token, UserDetails userDetails) {
//        final String email = extractEmail(token);
//        return (email.equals(userDetails.getUsername())) && !isTokenExpired(token);
//    }
//
//    // Extract email from token
//    public String extractEmail(String token) {
//        return extractClaim(token, Claims::getSubject);
//    }
//
//    // Extract expiration date
//    public Date extractExpiration(String token) {
//        return extractClaim(token, Claims::getExpiration);
//    }
//
//    // Extract a specific claim
//    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
//        final Claims claims = extractAllClaims(token);
//        return claimsResolver.apply(claims);
//    }
//
//    // Extract all claims from JWT
//    private Claims extractAllClaims(String token) {
//        return Jwts.parser()
//                .verifyWith(getSigningKey()) // Use verifyWith() instead of setSigningKey()
//                .build()
//                .parseSignedClaims(token)
//                .getPayload(); // Use getPayload() instead of getBody()
//    }
//
//    // Check if token is expired
//    private boolean isTokenExpired(String token) {
//        return extractExpiration(token).before(new Date());
//    }
//
//    // Get signing key
//    private SecretKey getSigningKey() {
//        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
//        return Keys.hmacShaKeyFor(keyBytes);
//    }


    private final UserService userService;

    @Value("${secret.key.string}")
    private String secretKeyString;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        if (secretKeyString == null || secretKeyString.isEmpty()) {
            throw new IllegalArgumentException("secret.key.string is not configured properly.");
        }

        // Ensure the secret key is valid for HMAC-SHA256
        this.secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes());
        System.out.println("Secret key initialized successfully.");
    }

    public JwtService(@Lazy UserService userService) {
        this.userService = userService;
    }

    public String generateToken(String email) {
        User user = userService.findByEmail(email);
        Map<String, Object> claims = new HashMap<>();

        //Put role in claims
        claims.put("role", user.getRoleName().name());

        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000*60 * 60 * 24*7))
                .and()
                .signWith(getKey())
                .compact();
    }

    private SecretKey getKey() {
        return secretKey;
    }

    public String extractEmail(String token) {
        // extract the username from jwt token
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String userName = extractEmail(token);
        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
