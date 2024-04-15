package com.noble.tardis.modules.quiz.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.noble.tardis.security.entities.User;

@Entity
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private String slug;

    private String title;
    private String description;

    @ManyToMany
    private List<Keyword> keywords = List.of();

    @Column(name = "image_url")
    private String imageURL;

    private boolean activated = true;

    @CreationTimestamp
    private Instant createdAt;
    @UpdateTimestamp
    private Instant updatedAt;

    @ManyToOne
    private User owner;

    public Quiz() {
    }

    public Quiz(String slug, String title, String description, List<Keyword> keywords, User owner) {
        this.owner = owner;
        this.slug = slug;
        this.title = title;
        this.description = description;
        this.keywords = keywords;
    }

    public Quiz(String slug, String title, String description, Stream<Keyword> keywords, User owner) {
        this.owner = owner;
        this.slug = slug;
        this.title = title;
        this.description = description;
        this.keywords = keywords.collect(Collectors.toList());
    }

    public Quiz(String slug, String title, String description, List<Keyword> keywords, User owner, String imageURL) {
        this.owner = owner;
        this.slug = slug;
        this.title = title;
        this.description = description;
        this.keywords = keywords;
        this.imageURL = imageURL;
    }

    public Long getId() {
        return id;
    }

    public String getSlug() {
        return slug;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImageURL() {
        return imageURL;
    }

    public boolean isActivated() {
        return activated;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public List<Keyword> getKeywords() {
        return keywords;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

}
