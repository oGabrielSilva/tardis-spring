package com.noble.tardis.modules.quiz.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
public class Keyword {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private String descriptor;

    private boolean activated = true;

    @CreationTimestamp
    private Instant createdAt;
    @UpdateTimestamp
    private Instant updatedAt;

    public Keyword() {
    }

    public Keyword(String descriptor) {
        this.descriptor = descriptor;
    }

    public Long getId() {
        return id;
    }

    public String getDescriptor() {
        return descriptor;
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

    public void setDescriptor(String descriptor) {
        this.descriptor = descriptor;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

}
