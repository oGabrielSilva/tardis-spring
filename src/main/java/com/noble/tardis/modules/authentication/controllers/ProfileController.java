package com.noble.tardis.modules.authentication.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import com.noble.tardis.security.entities.User;
import com.noble.tardis.security.services.UserService;

@Controller
public class ProfileController {
    @Autowired
    UserService userService;

    @GetMapping("/u/{username}")
    public ModelAndView getMethodName(@PathVariable("username") String username,
            @AuthenticationPrincipal User loggedUser) {
        var user = userService.findOneByUsername(username);
        var model = new ModelAndView();
        if (user == null) {
            model.setViewName("profile-404");
            return model;
        }
        if (loggedUser != null && loggedUser.getUsername().equals(user.getUsername())) {
            model.setViewName("profile-edit");
            model.addObject("owner", user);
            return model;
        }
        model.setViewName("profile");
        model.addObject("owner", user);
        return model;
    }

}
