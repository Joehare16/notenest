package com.nestenote.notenest.controller;

import com.nestenote.notenest.model.Note;
import com.nestenote.notenest.model.User;
import com.nestenote.notenest.repository.UserRepository;
import com.nestenote.notenest.service.NoteService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.nestenote.notenest.dto.NoteRequestDTO;
import com.nestenote.notenest.dto.NoteResponseDTO;
import com.nestenote.notenest.exception.ValidationException;

import jakarta.validation.Valid;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;


import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/notes")
@CrossOrigin(origins = "*")
public class NoteController {

    private final UserRepository userRepository;

    private final NoteService noteService;

    public NoteController(NoteService noteService, UserRepository userRepository) {
        this.noteService = noteService;
        this.userRepository = userRepository;
    }

    private long getUserIdFromJwt(Jwt jwt) {
        return Long.parseLong(jwt.getSubject());
    }

    @GetMapping
    public List<NoteResponseDTO> getAllNotes(@AuthenticationPrincipal org.springframework.security.core.userdetails.User principal ) {
        String email = principal.getUsername();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ValidationException("User not found"));
        return noteService.getAllUserNotes(user.getId());
    }   

    @PostMapping
    public NoteResponseDTO createNote(@Valid @RequestBody NoteRequestDTO note,@AuthenticationPrincipal org.springframework.security.core.userdetails.User principal ) {
       String email = principal.getUsername();
         User user = userRepository.findByEmail(email)
                 .orElseThrow(() -> new ValidationException("User not found"));
        Long userID = user.getId();
        return noteService.createNote(note, userID);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoteResponseDTO> getNoteById(@PathVariable Long id,@AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {
        String email = principal.getUsername();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ValidationException("User not found"));
        Long userID = user.getId();
        return noteService.getNoteByID(id,userID)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<NoteResponseDTO> updateNote(@PathVariable Long id,@AuthenticationPrincipal org.springframework.security.core.userdetails.User principal, @Valid @RequestBody NoteRequestDTO updatedNote) {
        String email = principal.getUsername();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ValidationException("User not found"));
        Long userID = user.getId();
        return noteService.updateNote(id, updatedNote,userID)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(@PathVariable Long id,@AuthenticationPrincipal org.springframework.security.core.userdetails.User principal) {
        String email = principal.getUsername();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ValidationException("User not found"));
        Long userID = user.getId();
        boolean deleted = noteService.deleteNote(id,userID);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}