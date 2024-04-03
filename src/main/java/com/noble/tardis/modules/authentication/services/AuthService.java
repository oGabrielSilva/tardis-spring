package com.noble.tardis.modules.authentication.services;

import org.springframework.stereotype.Service;

import jakarta.servlet.http.Cookie;

@Service
public class AuthService {
    public Cookie generateAuthCookie(String token) {
        var cookie = new Cookie("Authorization", token);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24 * 2);
        cookie.setHttpOnly(true);
        cookie.setAttribute("SameSite", "Strict");
        return cookie;
    }
}
