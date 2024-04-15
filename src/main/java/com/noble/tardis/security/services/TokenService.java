package com.noble.tardis.security.services;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.noble.tardis.security.entities.User;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class TokenService {
    private String issuer;
    private String audience;
    private Algorithm algorithm;

    public TokenService(@Value("${token.key}") String tokenSecret, @Value("${token.issuer}") String issuer,
            @Value("${token.audience}") String audience) {
        this.algorithm = Algorithm.HMAC256(tokenSecret);
        this.issuer = issuer;
        this.audience = audience;
    }

    public String token(User user) {
        try {
            return JWT.create().withIssuer(issuer).withAudience(audience).withSubject(user.getEmail())
                    .withClaim("authorities", user.getAllUserRoles())
                    .withExpiresAt(LocalDateTime.now().plusDays(7).toInstant(ZoneOffset.UTC)).sign(algorithm);
        } catch (JWTCreationException ex) {
            return "";
        }
    }

    public String getSubject(String token) {
        try {
            return decode(token).getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    public DecodedJWT decode(String token) {
        try {
            return JWT.require(algorithm).withIssuer(issuer).build().verify(token);
        } catch (Exception e) {
            return null;
        }
    }

    public String recoveryToken(HttpServletRequest req) {
        String authorization = req.getHeader("Authorization");
        if (authorization == null) {
            var cookies = req.getCookies();
            if (cookies == null || cookies.length < 1)
                return authorization;
            var op = Stream.of(cookies).filter(c -> {
                return c.getName().equals("Authorization");
            }).findFirst();
            if (op.isPresent()) {
                authorization = op.get().getValue();
            } else
                return authorization;
        }
        return authorization.contains(" ") ? authorization.split(" ")[1] : authorization;
    }

}
