package com.nestenote.notenest.model;

import jakarta.persistence.*;

import java.lang.annotation.Inherited;
import java.time.LocalDateTime;

import javax.annotation.processing.Generated;

@Entity // JPA to map this to a table in the db
@Table(name = "notes")
public class Note {
    @Id // primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto-increment
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createdAt;

    //each note is associated with a user so we can autehnticate
    @Column(name = "user_id", nullable = false)
    private String userID;

    public Note() {
        this.createdAt = LocalDateTime.now();
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getUserId() {
        return userID;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public void setUserId(String userID) {
        this.userID = userID;
    }
}
