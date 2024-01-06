package com.assignment.repository;

import com.assignment.entity.Note;
import com.assignment.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NoteRepository extends JpaRepository<Note,Long> {
    @Query("SELECT new com.assignment.repository.NoteDTO(n.id, n.title, n.content) FROM Note n WHERE n.user.id = :userId")
    List<NoteDTO> findNotesByUserId(@Param("userId") UUID userId);

}
