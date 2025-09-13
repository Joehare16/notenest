package com.nestenote.notenest.repository;

import com.nestenote.notenest.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findByUser_Id(Long userID);
}
