package org.smrp.services;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;

import javax.crypto.SecretKey;

import org.smrp.Constants;
import org.smrp.models.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.quarkus.security.UnauthorizedException;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.BadRequestException;


@ApplicationScoped
public class TokenService {

    private final UserService userService;

    public TokenService(UserService userService) {
        this.userService = userService;
    }

    public Claims decodeToken(String tokenStr) {
        try {
            return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(tokenStr)
                .getPayload();
        } catch (Exception e) {
            throw new UnauthorizedException("Invalid Credentials");
        }
    }
    
    public String generateAccessToken(User user) {
        long val = 720L * 60 * 60 * 1000;
        Date expiryDate = new Date(System.currentTimeMillis() + val);
        HashMap<String, Object> claims = new HashMap<>(){
            {
                put("username", user.getUsername());
            }
        };

        // 2. Create and sign the JWT
        String token = Jwts.builder()
                .claims(claims)
                .subject(String.valueOf(user.getId()))
                .expiration(expiryDate) // Sets the 'exp' claim
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
        return token;
    }

    public String generateRefreshToken(User user) {
        long val = 87600L * 60 * 60 * 1000;
        Date expiryDate = new Date(System.currentTimeMillis() + val);
        HashMap<String, Object> claims = new HashMap<>(){
            {
                put("username", user.getUsername());
            }
        };

        // 2. Create and sign the JWT
        String token = Jwts.builder()
                .claims(claims)
                .subject(String.valueOf(user.getId()))
                .expiration(expiryDate) // Sets the 'exp' claim
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
        return token;
    }

    public Uni<User> resolveRefreshToken(String encoded) {
        Claims claims = decodeRefreshToken(encoded);
        Long id = Long.valueOf(claims.getSubject());
        return getUserFromRefreshTokenPayload(id);
    }

    public Uni<HashMap<String, Object>> createAccessTokenFromRefreshToken(String refresh) {
        return resolveRefreshToken(refresh).flatMap(user -> {
            if (user == null) {
                throw new UnauthorizedException("Invalid Credentials");
            }

            String token = generateAccessToken(user);
            String refreshToken = generateRefreshToken(user);
            HashMap<String, Object> result = new HashMap<>();
            result.put("user", user);
            result.put("token", token);
            result.put("refreshToken", refreshToken);
            return Uni.createFrom().item(result);
        });
    }

    private Claims decodeRefreshToken(String tokenStr) {
        try {
            return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(tokenStr)
                .getPayload();
        } catch (Exception e) {
            throw new BadRequestException("Invalid Refresh Token");
        }
    }

    private Uni<User> getUserFromRefreshTokenPayload(Long id) {
        return userService.findById(id);
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Constants.JWT_SECRET.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
