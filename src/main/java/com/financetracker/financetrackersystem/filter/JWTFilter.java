package com.financetracker.financetrackersystem.filter;

import java.io.IOException;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.financetracker.financetrackersystem.entity.UserEntity;
import com.financetracker.financetrackersystem.repository.UserRepository;
import com.financetracker.financetrackersystem.service.JWTService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

  private final JWTService jwtService;
  private final UserRepository userRepository;

  @Override
  protected void doFilterInternal(
    @NonNull HttpServletRequest request,
    @NonNull HttpServletResponse response,
    @NonNull FilterChain filterChain
  ) throws ServletException, IOException {
    
    String jwtToken = extractJwtFromCookie(request);

    if (jwtToken == null) {
      filterChain.doFilter(request, response);
      return;
    }

    try {
      String username = jwtService.getUsername(jwtToken);
      String id = jwtService.getUserId(jwtToken);

      if (username == null || id == null) {
        filterChain.doFilter(request, response);
        return;
      }

      UserEntity userData = userRepository.findByUsername(username).orElse(null);
      if (userData == null) {
        filterChain.doFilter(request, response);
        return;
      }

      if (SecurityContextHolder.getContext().getAuthentication() == null) {
        UserDetails userDetails = User.builder()
          .username(userData.getUsername())
          .password(userData.getPassword())
          .build();

        UsernamePasswordAuthenticationToken authenticationToken =
          new UsernamePasswordAuthenticationToken(
            userDetails,
            null,
            userDetails.getAuthorities()
          );

        authenticationToken.setDetails(
          new WebAuthenticationDetailsSource().buildDetails(request)
        );
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
      }

    } catch (Exception e) {
      System.out.println("❌ JWT Filter: Authentication Failed - " + e.getMessage());
    }

    filterChain.doFilter(request, response);
  }

  /**
   * Extract JWT token from the HttpOnly cookie
   */
  private String extractJwtFromCookie(HttpServletRequest request) {
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