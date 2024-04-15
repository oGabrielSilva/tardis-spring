package com.noble.tardis.modules.quiz.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.noble.tardis.exception.BadRequest;
import com.noble.tardis.exception.InternalServerError;
import com.noble.tardis.exception.NotFound;
import com.noble.tardis.exception.Unauthorized;
import com.noble.tardis.modules.quiz.dto.QuestRequestDto;
import com.noble.tardis.modules.quiz.dto.QuestResponseDto;
import com.noble.tardis.modules.quiz.dto.QuizRequestDto;
import com.noble.tardis.modules.quiz.dto.QuizResponseDto;
import com.noble.tardis.modules.quiz.entities.Keyword;
import com.noble.tardis.modules.quiz.entities.Quest;
import com.noble.tardis.modules.quiz.entities.Quiz;
import com.noble.tardis.modules.quiz.repositories.KeywordRepository;
import com.noble.tardis.modules.quiz.repositories.QuestRepository;
import com.noble.tardis.modules.quiz.repositories.QuizRepository;
import com.noble.tardis.modules.quiz.services.QuestService;
import com.noble.tardis.modules.quiz.services.QuizService;
import com.noble.tardis.modules.quiz.validation.QuizValidation;
import com.noble.tardis.security.entities.User;
import com.noble.tardis.storage.StorageService;

@RestController
@RequestMapping("/api/quiz")
public class QuizRestController {
    @Autowired
    QuizValidation validation;
    @Autowired
    KeywordRepository keywordRepository;
    @Autowired
    QuizService quizService;
    @Autowired
    QuizRepository quizRepository;
    @Autowired
    QuestRepository questRepository;
    @Autowired
    QuestService questService;
    @Autowired
    StorageService storage;

    @ResponseStatus(code = HttpStatus.CREATED)
    @PostMapping(consumes = { "multipart/form-data" })
    public QuizResponseDto create(@ModelAttribute QuizRequestDto payload, @RequestPart("image") MultipartFile image,
            @AuthenticationPrincipal User user) {
        if (image.isEmpty())
            throw new BadRequest("Imagem não recebida");
        if (!image.getContentType().contains("image"))
            throw new BadRequest("Arquivo recebido não é uma imagem válida");
        if (!validation.isTitleValid(payload.title()))
            throw new BadRequest("Título não é considerado válido");
        if (!validation.isKeywordsValid(payload.keywords()))
            throw new BadRequest("Erro ao validar as palavras-chave. Estão inválidas");

        var slug = quizService.generateSlug(payload.title());
        var keywords = validation.catchKeywords(payload.keywords()).map(key -> keywordRepository.findByDescriptor(key)
                .orElseGet(() -> keywordRepository.save(new Keyword(key))));

        var quiz = new Quiz(slug, payload.title(), payload.description(), keywords, user);
        quizRepository.save(quiz);

        var url = quizService.uploadImage(image, quiz);
        quiz.setImageURL(url + "?nid=" + NanoIdUtils.randomNanoId());
        quizRepository.save(quiz);

        return quizService.entityToDto(quiz);
    }

    @ResponseStatus(code = HttpStatus.CREATED)
    @PostMapping(consumes = { "multipart/form-data" }, value = "/{quizSlug}/quest")
    public QuestResponseDto pushQuest(@ModelAttribute QuestRequestDto payload,
            @RequestPart(required = false, value = "image") MultipartFile image,
            @AuthenticationPrincipal User user, @PathVariable("quizSlug") String quizSlug) {
        var isImageValid = image != null && !image.isEmpty() && image.getContentType().contains("image");
        if (isImageValid && image.getSize() > 1024000l)
            throw new BadRequest("Imagem muito grande");
        if (!validation.isQuestTitleValid(payload.quest()))
            throw new BadRequest("A pergunta precisa conter entre 5 e 150 caracteres");
        if (payload.a() == null || payload.a().isBlank())
            throw new BadRequest("Está faltando a resposta correta ou ele está inválida");
        if (payload.b() == null || payload.b().isBlank())
            throw new BadRequest("É necessário pelo menos duas alternativas");
        var quiz = quizRepository.findBySlug(quizSlug);
        if (quiz == null)
            throw new NotFound("Quiz não encontrado");
        if (!quiz.getOwner().getId().equals(user.getId()))
            throw new Unauthorized("Usuário não tem permissão para adicionar perguntas ao quiz");
        var quest = questRepository.save(
                new Quest(payload.quest(), "", payload.a(), payload.b(), payload.c(), payload.d(), payload.e(), quiz,
                        user));
        if (isImageValid) {
            var imageURL = storage.uploadTo(image,
                    "quizzes/" + String.valueOf(quiz.getId()) + "/" + String.valueOf(quest.getId()));
            quest.setImageURL(imageURL);
            questRepository.save(quest);
        }
        return questService.entityToDto(quest);
    }

    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @DeleteMapping("/quest/{questId}")
    public void deleteQuest(@PathVariable long questId, @AuthenticationPrincipal User user) {
        var questOpt = questRepository.findById(questId);
        if (questOpt.isEmpty())
            throw new NotFound("Pergunta não encontrada");
        var quest = questOpt.get();
        if (!quest.getCreatedBy().getId().equals(user.getId()))
            throw new Unauthorized("Você não possui permissão para modificar esse recurso");
        if (quest.getImageURL() != null && quest.getImageURL().contains(".storage.googleapis.")) {
            var imageDeleted = storage
                    .delete("quizzes/" + String.valueOf(quest.getQuiz().getId()) + "/" + String.valueOf(quest.getId()));
            if (!imageDeleted)
                throw new InternalServerError("Não foi possível deletar a imagem do recurso");
        }
        questRepository.delete(quest);
    }
}
