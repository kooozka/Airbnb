package com.rental.auth.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtFilter extends OncePerRequestFilter {
    private final JwtService jwtService;

    private final String AUTHORIZATION_HEADER_KEY;
    private final String BEARER_HEADER_KEY;
    private final int JWT_BEGIN_INDEX;

    @Autowired
    public JwtFilter(JwtService jwtService) {
        this.jwtService=jwtService;

        this.AUTHORIZATION_HEADER_KEY="Authorization";
        this.BEARER_HEADER_KEY="Bearer ";
        this.JWT_BEGIN_INDEX=7;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader(AUTHORIZATION_HEADER_KEY);
        String token = null;
        String userEmail = null;

        if(authHeader != null && authHeader.startsWith(BEARER_HEADER_KEY)){
            token = authHeader.substring(JWT_BEGIN_INDEX);
            userEmail = jwtService.extractEmail(token);
        }

        if (!jwtService.validateToken(token, userEmail)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Invalid or expired token\"}");
            return;
        }

        if(userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null){

            if(jwtService.validateToken(token, userEmail)){
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userEmail,
                                null,
                                Collections.singletonList(new SimpleGrantedAuthority("USER")));
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}
