package com.nestenote.notenest.service;

import com.nestenote.notenest.model.Note;
import com.nestenote.notenest.repository.NoteRepository;
import com.nestenote.notenest.dto.NoteRequestDTO;
import com.nestenote.notenest.dto.NoteResponseDTO;
import com.nestenote.notenest.exception.NoteNotFoundException;
import com.nestenote.notenest.exception.ValidationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service // Marks this class as a Spring-managed service bean
public class NoteService {

    private final NoteRepository noteRepository;

    // Constructor injection of repository
    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    // converts user inputs restricted by DTOs to a full database entity
    private Note mapToEntity(NoteRequestDTO dto,String userID) {
        Note note = new Note();
        note.setTitle(dto.getTitle());
        note.setContent(dto.getContent());
        note.setUserId(userID);
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
    public List<NoteResponseDTO> getAllUserNotes(String userID) {
        return noteRepository.findByUserID(userID)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Get one note by ID
    public Optional<NoteResponseDTO> getNoteByID(Long id, String userID) {
        Note note = noteRepository.findById(id)
                 .filter(n -> n.getUserId().equals(userID))
                .orElseThrow(() -> new NoteNotFoundException(id));
                
        return Optional.of(mapToDTO(note));
    }

    // Create a new note
    public NoteResponseDTO createNote(NoteRequestDTO dto,String userID) {
        if(dto.getTitle() == null || dto.getTitle().isEmpty()) {
            throw new ValidationException("Title cannot be empty");
        }
        // creates a full note object from dto
        Note note = mapToEntity(dto, userID);
        // saves full note into entity
        Note saved = noteRepository.save(note);
        // returns the safe DTO note version
        return mapToDTO(saved);
    }

    // Update an existing note
    public Optional<NoteResponseDTO> updateNote(Long id, NoteRequestDTO dto, String userID) {
        return noteRepository.findById(id)
        .filter(note -> note.getUserId().equals(userID))
        .map(note -> {
            note.setTitle(dto.getTitle());
            note.setContent(dto.getContent());
            Note updated = noteRepository.save(note);
            return mapToDTO(updated);
        });
    }

    // Delete a note by ID
    public boolean deleteNote(Long id,String userID) {
        return noteRepository.findById(id).map(note -> {
            if(!note.getUserId().equals(userID)) {
                throw new NoteNotFoundException(id);
            }
            noteRepository.delete(note);
            return true;
        }).orElse(false);
    }
}
