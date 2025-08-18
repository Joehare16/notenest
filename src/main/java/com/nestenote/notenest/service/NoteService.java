package com.nestenote.notenest.service;

import com.nestenote.notenest.model.Note;
import com.nestenote.notenest.repository.NoteRepository;
import com.nestenote.notenest.dto.NoteRequestDTO;
import com.nestenote.notenest.dto.NoteResponseDTO;
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
    private Note mapToEntity(NoteRequestDTO dto) {
        Note note = new Note();
        note.setTitle(dto.getTitle());
        note.setContent(dto.getContent());
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
    public List<NoteResponseDTO> getAllNotes() {
        return noteRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Get one note by ID
    public Optional<NoteResponseDTO> getNoteById(Long id) {
        return noteRepository.findById(id)
                .map(this::mapToDTO);
    }

    // Create a new note
    public NoteResponseDTO createNote(NoteRequestDTO dto) {
        // creates a full note object from dto
        Note note = mapToEntity(dto);
        // saves full note into entity
        Note saved = noteRepository.save(note);
        // returns the safe DTO note version
        return mapToDTO(saved);
    }

    // Update an existing note
    public Optional<NoteResponseDTO> updateNote(Long id, NoteRequestDTO dto) {
        return noteRepository.findById(id).map(note -> {
            note.setTitle(dto.getTitle());
            note.setContent(dto.getContent());
            Note updated = noteRepository.save(note);
            return mapToDTO(updated);
        });
    }

    // Delete a note by ID
    public boolean deleteNote(Long id) {
        return noteRepository.findById(id).map(note -> {
            noteRepository.delete(note);
            return true;
        }).orElse(false);
    }
}
