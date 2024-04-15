package com.noble.tardis.modules.quiz.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.noble.tardis.modules.quiz.entities.Quest;
import com.noble.tardis.modules.quiz.entities.Quiz;

public interface QuestRepository extends JpaRepository<Quest, Long> {

    List<Quest> findAllByQuiz(Quiz quiz);

}
