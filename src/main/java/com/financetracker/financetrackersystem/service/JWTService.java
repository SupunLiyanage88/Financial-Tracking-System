package com.financetracker.financetrackersystem.service;

import java.util.Date;
import java.util.Map;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class JWTService {

    private final SecretKey secretKey;


    //Generate JWTs
    public JWTService(){
        try {
            SecretKey k = KeyGenerator.getInstance("HmacSHA256").generateKey();
            secretKey = Keys.hmacShaKeyFor(k.getEncoded());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

  public String getJWTToken(String username, Map<String, Object> claims) {
    return Jwts.builder()
      .claims(claims)
      .subject(username)
      .issuedAt(new Date(System.currentTimeMillis()))
      .expiration(
        new java.util.Date(System.currentTimeMillis() + 1000 * 60 * 60)
      )
      .signWith(secretKey)
      .compact();
  }


    
    //Get Username
    public String getUsername(String token){
        Claims data = getTokenData(token);
        if(data==null) return null;
        return data.getSubject();   
    }

    //Get ID
    public String getUserId(String token){
        Claims data = getTokenData(token);
        if(data == null) return null;
        return data.get("id", String.class);
    }

    //Get Role
    public String getUserRole(String token){
        Claims data = getTokenData(token);
        if(data == null) return null;
        return data.get("role", String.class);
    }

    // Get Email
    public String getEmail(String token) {
        Claims data = getTokenData(token);
        if (data == null) return null;
        return data.get("email", String.class);
    }

    public Object getFieldFromToken(String token, String key){
        Claims data = getTokenData(token);
        if(data==null) return null;
        return data.get(key);
    }

    private Claims getTokenData(String token){
        try {
            return Jwts
            .parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();

        } catch (Exception e) {
            return null;
        }
    }


    public String extractJwtFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwt".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

}
