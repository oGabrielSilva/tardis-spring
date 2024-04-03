package com.noble.tardis.modules.authentication.interceptors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.noble.tardis.security.entities.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AddUserToModelInterceptor implements HandlerInterceptor {

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {
        var user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user instanceof User)
            modelAndView.addObject("user", user);
        else
            modelAndView.addObject("user", null);
    }

}
