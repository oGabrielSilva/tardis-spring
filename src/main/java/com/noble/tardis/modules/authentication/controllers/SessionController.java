package com.noble.tardis.modules.authentication.controllers;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.noble.tardis.security.entities.User;

import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/session")
public class SessionController {
    @GetMapping
    public String session(@AuthenticationPrincipal User user, HttpServletResponse res) {
        if (user != null) {
            return "redirect:/";
        }
        return "session";
    }
}
