package com.noble.tardis.modules.quiz.dto;

import java.util.List;

public record QuizResponseDto(
                String slug,
                String title,
                String description,
                String imageURL,
                List<String> keywords,
                String url) {

}
