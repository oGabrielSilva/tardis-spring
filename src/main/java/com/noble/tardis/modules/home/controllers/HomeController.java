package com.noble.tardis.modules.home.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.noble.tardis.modules.quiz.repositories.QuizRepository;
import com.noble.tardis.security.entities.User;

@Controller
@RequestMapping("/")
public class HomeController {
    @Autowired
    QuizRepository quizRepository;

    @GetMapping
    public ModelAndView index(@AuthenticationPrincipal User user) {
        var view = new ModelAndView("index");
        if (user != null && user instanceof User) {
            var list = quizRepository.findFirst10ByOwnerOrderByCreatedAtDesc(user);
            System.out.println(list);
            view.addObject("quizzes", list);
        }
        return view;
    }
}
