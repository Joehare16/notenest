package com.nestenote.notenest.controller;

import com.nestenote.notenest.model.Note;
import com.nestenote.notenest.service.NoteService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.nestenote.notenest.dto.NoteRequestDTO;
import com.nestenote.notenest.dto.NoteResponseDTO;

import jakarta.validation.Valid;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;


import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/notes")
@CrossOrigin(origins = "*")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping
    public List<NoteResponseDTO> getAllNotes(@AuthenticationPrincipal Jwt jwt) {
        String userID = jwt.getSubject();
        return noteService.getAllUserNotes(userID);
    }

    @PostMapping
    public NoteResponseDTO createNote(@Valid @RequestBody NoteRequestDTO note,@AuthenticationPrincipal Jwt jwt) {
        String userID = jwt.getSubject();
        return noteService.createNote(note, userID);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoteResponseDTO> getNoteById(@PathVariable Long id,@AuthenticationPrincipal Jwt jwt) {
        String userID = jwt.getSubject();
        return noteService.getNoteByID(id,userID)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<NoteResponseDTO> updateNote(@PathVariable Long id,@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody NoteRequestDTO updatedNote) {
        String userID = jwt.getSubject();
        return noteService.updateNote(id, updatedNote,userID)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(@PathVariable Long id,@AuthenticationPrincipal Jwt jwt) {
        String userID = jwt.getSubject();
        boolean deleted = noteService.deleteNote(id,userID);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}