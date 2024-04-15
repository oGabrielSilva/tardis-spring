package com.noble.tardis.modules.quiz.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.noble.tardis.modules.quiz.entities.Quiz;
import com.noble.tardis.security.entities.User;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    Quiz findBySlug(String slug);

    List<Quiz> findFirst10ByOwnerOrderByCreatedAtDesc(User owner);
}
