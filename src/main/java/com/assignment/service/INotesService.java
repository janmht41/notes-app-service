package com.assignment.service;

import com.assignment.entity.Share;
import com.assignment.model.NotesRequestModel;
import com.assignment.repository.NoteDTO;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface INotesService {
     List<NoteDTO> getNotesForUser(String bearerToken);

     void saveNote(String bearerToken, NotesRequestModel notesRequestModel);
     void updateNoteById(String bToken,Long noteId, NoteDTO noteDTO);

     NoteDTO getNoteById(String bToken,Long noteId);

     void deleteNote(String ownerToken,Long noteId);

     @Transactional
     Share shareNote(Long noteId, UUID senderUserId, UUID receiverUserId);

     List<NoteDTO> searchNotes(String ownerToken, String text, List<String> fields, int limit);
}
