package com.noble.tardis.security.filters;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.noble.tardis.security.repositories.UserRepository;
import com.noble.tardis.security.services.TokenService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SecurityFilter extends OncePerRequestFilter {
    @Autowired
    TokenService tokenService;
    @Autowired
    UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        var token = tokenService.recoveryToken(request);
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }
        var email = tokenService.getSubject(token);
        if (email == null) {
            filterChain.doFilter(request, response);
            return;
        }
        UserDetails user = userRepository.findByEmail(email);
        if (user == null) {
            filterChain.doFilter(request, response);
            return;
        }
        var auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        return path.startsWith("/public") || path.startsWith("/api/authentication/sign-up")
                || path.startsWith("/api/authentication/session");
    }

}
