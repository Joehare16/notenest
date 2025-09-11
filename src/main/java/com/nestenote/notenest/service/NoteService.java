package com.nestenote.notenest.service;

import com.nestenote.notenest.model.Note;
import com.nestenote.notenest.repository.NoteRepository;
import com.nestenote.notenest.repository.UserRepository;
import com.nestenote.notenest.model.User;
import com.nestenote.notenest.dto.NoteRequestDTO;
import com.nestenote.notenest.dto.NoteResponseDTO;
import com.nestenote.notenest.exception.NoteNotFoundException;
import com.nestenote.notenest.exception.ValidationException;

import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service // Marks this class as a Spring-managed service bean
public class NoteService {

    private final NoteRepository noteRepository;
    private final UserRepository userRepository;

    // Constructor injection of repository
    public NoteService(NoteRepository noteRepository,UserRepository userRepository) {
        this.noteRepository = noteRepository;
        this.userRepository = userRepository;
    }

    // converts user inputs restricted by DTOs to a full database entity
    private Note mapToEntity(NoteRequestDTO dto, User user) {
        Note note = new Note();
        note.setTitle(dto.getTitle());
        note.setContent(dto.getContent());
        note.setUser(user);
        note.setCreatedAt(LocalDateTime.now());
        return note;
    }

    // converts a full database entity into a DTO we can send to user in POST
    private NoteResponseDTO mapToDTO(Note note) {
        return new NoteResponseDTO(
                note.getId(),
                note.getTitle(),
                note.getContent(),
                note.getCreatedAt());

    }

    // Get all notes
    public List<NoteResponseDTO> getAllUserNotes(Long userID) {
        return noteRepository.findByUser_Id(userID)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Get one note by ID
    public Optional<NoteResponseDTO> getNoteByID(Long id, Long userID) {
        Note note = noteRepository.findById(id)
                .filter(n -> n.getUser().getId().equals(userID))
                .orElseThrow(() -> new NoteNotFoundException(id));
                
        return Optional.of(mapToDTO(note));
    }

    // Create a new note
    public NoteResponseDTO createNote(NoteRequestDTO dto,Long userID) {
        if(dto.getTitle() == null || dto.getTitle().isEmpty()) {
            throw new ValidationException("Title cannot be empty");
        }
        User user = userRepository.findById(userID)
            .orElseThrow(() -> new ValidationException("User not found"));
        // creates a full note object from dto
        Note note = mapToEntity(dto, user);
        // saves full note into entity
        Note saved = noteRepository.save(note);
        // returns the safe DTO note version
        return mapToDTO(saved);
    }

    // Update an existing note
    public Optional<NoteResponseDTO> updateNote(Long id, NoteRequestDTO dto, Long userID) {
        return noteRepository.findById(id)
        .filter(note -> note.getUser().getId().equals(userID))
        .map(note -> {
            note.setTitle(dto.getTitle());
            note.setContent(dto.getContent());
            Note updated = noteRepository.save(note);
            return mapToDTO(updated);
        });
    }

    // Delete a note by ID
    public boolean deleteNote(Long id,Long userID) {
        return noteRepository.findById(id).map(note -> {
            if(!note.getUser().getId().equals(userID)) {
                throw new NoteNotFoundException(id);
            }
            noteRepository.delete(note);
            return true;
        }).orElse(false);
    }
}
