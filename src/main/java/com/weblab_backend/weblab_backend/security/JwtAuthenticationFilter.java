package com.weblab_backend.weblab_backend.security;

import com.weblab_backend.weblab_backend.exceptions.MissingTokenException;
import com.weblab_backend.weblab_backend.service.JWTService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
  private final HandlerExceptionResolver handlerExceptionResolver;
  private final JWTService jwtService;
  private final UserDetailsService userDetailsService;
  private final List<String> permitAllUrls = Arrays.asList("/auth/register", "/auth/login", "/h2-console/**");

  public JwtAuthenticationFilter(HandlerExceptionResolver handlerExceptionResolver, JWTService jwtService,
      UserDetailsService userDetailsService) {
    this.handlerExceptionResolver = handlerExceptionResolver;
    this.jwtService = jwtService;
    this.userDetailsService = userDetailsService;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    try {
      if (isPermitAllUrl(request)) {
        filterChain.doFilter(request, response);
        return;
      }

      String jwtToken = extractJwtFromRequest(request);
      if (jwtToken == null) {
        throw new MissingTokenException("JWT token is missing");
      }

      authenticateUser(jwtToken, request);
      filterChain.doFilter(request, response);
    } catch (Exception e) {
      handlerExceptionResolver.resolveException(request, response, null, e);
    }
  }

  private void authenticateUser(String jwtToken, HttpServletRequest request) {
    String email = jwtService.extractUsername(jwtToken);
    if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      UserDetails userDetails = userDetailsService.loadUserByUsername(email);
      if (jwtService.isTokenValid(jwtToken, userDetails) && !jwtService.isTokenBlacklisted(jwtToken)) {
        UsernamePasswordAuthenticationToken authToken = createAuthenticationToken(userDetails, request);
        SecurityContextHolder.getContext().setAuthentication(authToken);
      }
    }
  }

  private UsernamePasswordAuthenticationToken createAuthenticationToken(UserDetails userDetails,
      HttpServletRequest request) {
    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
        userDetails, null, userDetails.getAuthorities());
    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
    return authToken;
  }

  private String extractJwtFromRequest(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }
    return null;
  }

  private boolean isPermitAllUrl(HttpServletRequest request) {
    return permitAllUrls.stream()
        .anyMatch(url -> new AntPathMatcher().match(url, request.getServletPath()));
  }
}