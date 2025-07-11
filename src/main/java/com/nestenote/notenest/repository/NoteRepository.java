package com.nestenote.notenest.repository;

import com.nestenote.notenest.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteRepository extends JpaRepository<Note, Long> {

}
