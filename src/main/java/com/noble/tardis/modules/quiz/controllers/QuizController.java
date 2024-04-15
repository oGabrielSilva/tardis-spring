package com.noble.tardis.modules.quiz.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import com.noble.tardis.modules.quiz.repositories.QuestRepository;
import com.noble.tardis.modules.quiz.repositories.QuizRepository;
import com.noble.tardis.modules.quiz.services.QuestService;
import com.noble.tardis.security.entities.User;

@Controller
public class QuizController {
    @Autowired
    QuizRepository quizRepository;
    @Autowired
    QuestRepository questRepository;
    @Autowired
    QuestService questService;

    @GetMapping("/quiz")
    public String index() {
        return "quiz";
    }

    @GetMapping("/q/{slug}")
    public ModelAndView quiz(@PathVariable("slug") String slug, @AuthenticationPrincipal User user) {
        var quiz = quizRepository.findBySlug(slug);
        if (quiz == null)
            return new ModelAndView("404");
        if (user != null && quiz.getOwner().getId().equals(user.getId())) {
            var model = new ModelAndView("quiz-edit", Map.of("quiz", quiz, "keywords",
                    String.join(" ", quiz.getKeywords().stream().map(k -> k.getDescriptor()).toList())));
            var quests = questRepository.findAllByQuiz(quiz);
            if (quests != null) {
                model.addObject("quests", quests.stream().map(q -> questService.entityToDto(q)).toList());
            } else
                model.addObject("quests", List.of());
            return model;
        }
        return new ModelAndView("redirect:/p/" + quiz.getSlug());
    }

}
