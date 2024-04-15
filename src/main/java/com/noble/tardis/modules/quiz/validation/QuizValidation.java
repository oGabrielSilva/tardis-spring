package com.noble.tardis.modules.quiz.validation;

import org.springframework.stereotype.Component;

import java.util.stream.Stream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class QuizValidation {
    private final Pattern keywordsRegex = Pattern.compile("[^a-zA-Z0-9,_\\-@#!%&?*\\s]");

    public boolean isTitleValid(String title) {
        return title != null && title.length() >= 5;
    }

    public boolean isKeywordsValid(String keywords) {
        return keywords != null && keywords.length() >= 1;
    }

    public Stream<String> catchKeywords(String keywords) {
        if (keywords == null || keywords.isEmpty()) {
            return Stream.empty();
        }
        return Stream.of(normalizeKeywords(keywords)
                .replace(",", " ").toLowerCase()
                .split("\\s+")).filter(k -> k.length() >= 1);
    }

    public String normalizeKeywords(String keywords) {
        if (keywords == null) {
            return "";
        }
        Matcher matcher = keywordsRegex.matcher(keywords);
        return matcher.replaceAll("");
    }

    public boolean isQuestTitleValid(String quest) {
        return quest != null && quest.length() >= 5 && quest.length() <= 150;
    }
}
