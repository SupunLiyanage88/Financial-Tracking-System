package com.financeTracker.financeTrackerSystem.service;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.financetracker.financetrackersystem.service.JWTService;

@ExtendWith(MockitoExtension.class)
class JWTServiceTest {

    @InjectMocks
    private JWTService jwtService;

    @Mock
    private HttpServletRequest request;

    private String token;
    private Map<String, Object> claims;
    private final String username = "testUser";

    @BeforeEach
    void setUp() {
        claims = new HashMap<>();
        claims.put("id", "123");
        claims.put("role", "Admin");
        claims.put("email", "test@example.com");
        token = jwtService.getJWTToken(username, claims);
    }

    @Test
    void testGenerateJWTToken() {
        assertNotNull(token);
        assertTrue(token.length() > 10);
    }

    @Test
    void testGetUsername() {
        String extractedUsername = jwtService.getUsername(token);
        assertEquals(username, extractedUsername);
    }

    @Test
    void testGetUserId() {
        String userId = jwtService.getUserId(token);
        assertEquals("123", userId);
    }

    @Test
    void testGetUserRole() {
        String role = jwtService.getUserRole(token);
        assertEquals("Admin", role);
    }

    @Test
    void testGetEmail() {
        String email = jwtService.getEmail(token);
        assertEquals("test@example.com", email);
    }

    @Test
    void testGetFieldFromToken() {
        assertEquals("Admin", jwtService.getFieldFromToken(token, "role"));
    }

    @Test
    void testInvalidTokenReturnsNull() {
        assertNull(jwtService.getUsername("invalidToken"));
    }

    @Test
    void testExtractJwtFromCookie() {
        Cookie jwtCookie = new Cookie("jwt", token);
        Cookie[] cookies = {jwtCookie};
        when(request.getCookies()).thenReturn(cookies);
        
        String extractedToken = jwtService.extractJwtFromCookie(request);
        assertEquals(token, extractedToken);
    }

    @Test
    void testExtractJwtFromCookie_NoJwtCookie() {
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("session", "abc123")});
        assertNull(jwtService.extractJwtFromCookie(request));
    }

    @Test
    void testExtractJwtFromCookie_NoCookies() {
        when(request.getCookies()).thenReturn(null);
        assertNull(jwtService.extractJwtFromCookie(request));
    }
}

