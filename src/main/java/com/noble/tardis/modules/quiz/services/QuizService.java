package com.noble.tardis.modules.quiz.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.noble.tardis.modules.quiz.dto.QuizResponseDto;
import com.noble.tardis.modules.quiz.entities.Quiz;
import com.noble.tardis.modules.quiz.repositories.QuizRepository;
import com.noble.tardis.storage.StorageService;

import java.time.Instant;

@Service
public class QuizService {
    @Autowired
    StorageService storageService;
    @Autowired
    QuizRepository repository;

    public String uploadImage(MultipartFile avatar, Quiz quiz) {
        try {
            var bucket = storageService.getBucket();
            var storage = bucket.getStorage();
            String bucketName = bucket.getName();
            String fileName = "quiz/" + quiz.getId().toString().concat(".jpeg");
            BlobId blobId = BlobId.of(bucketName, fileName);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
            Blob blob = storage.get(bucketName, fileName);
            Storage.BlobWriteOption precondition = blob == null
                    ? Storage.BlobWriteOption.doesNotExist()
                    : Storage.BlobWriteOption.generationMatch(blob.getGeneration());

            storage.createFrom(blobInfo, avatar.getInputStream(), precondition);
            String url = String.format("https://%s.storage.googleapis.com/%s", bucketName, fileName);
            return url;
        } catch (Exception e) {
            System.err.println("\n\n QuizService ERR: " + e.getMessage());
            e.printStackTrace();
            System.out.println("\n" + quiz.getId());
            System.out.println(Instant.now().toString() + "\n\n");
            return null;
        }
    }

    public final String generateSlug(String title) {
        var testSlug = String.join("-", title.replaceAll("[^a-zA-Z0-9\\s]", " ").split(" "));
        var quizBySlug = repository.findBySlug(testSlug);
        if (quizBySlug == null)
            return testSlug;
        testSlug = testSlug.concat("-" + NanoIdUtils.randomNanoId());
        return testSlug;
    }

    public QuizResponseDto entityToDto(Quiz quiz) {
        var keywords = quiz.getKeywords().stream().map(k -> k.getDescriptor()).toList();
        return new QuizResponseDto(quiz.getSlug(), quiz.getTitle(), quiz.getDescription(), quiz.getImageURL(), keywords,
                "/q/" + quiz.getSlug());
    }

}
