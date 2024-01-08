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
     void updateNoteById(Long noteId, NoteDTO noteDTO);

     NoteDTO getNoteById(Long noteId);

     void deleteNote(String ownerBtoken,Long noteId);

     @Transactional
     Share shareNote(Long noteId, UUID senderUserId, UUID receiverUserId);

     @Transactional
     void unshareNote(Long shareId);

     UUID getUserIdFrom(String bearerToken);
}
