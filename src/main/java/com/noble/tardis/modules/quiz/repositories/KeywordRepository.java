package com.noble.tardis.modules.quiz.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.noble.tardis.modules.quiz.entities.Keyword;
import java.util.Optional;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {
    Optional<Keyword> findByDescriptor(String descriptor);
}
