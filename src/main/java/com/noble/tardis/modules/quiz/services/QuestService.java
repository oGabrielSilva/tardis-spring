package com.noble.tardis.modules.quiz.services;

import org.springframework.stereotype.Service;

import com.noble.tardis.modules.quiz.dto.QuestResponseDto;
import com.noble.tardis.modules.quiz.entities.Quest;

@Service
public class QuestService {
    public QuestResponseDto entityToDto(Quest quest) {
        return new QuestResponseDto(quest.getId(), quest.getQuest(), quest.getImageURL(), quest.getA(), quest.getB(),
                quest.getC(), quest.getD(), quest.getE());
    }
}
