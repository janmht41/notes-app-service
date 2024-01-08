package com.assignment.service;

import com.assignment.entity.Note;

import com.assignment.entity.Share;
import com.assignment.entity.User;
import com.assignment.model.NotesRequestModel;
import com.assignment.repository.NoteDTO;
import com.assignment.repository.NoteRepository;
import com.assignment.repository.ShareRepository;
import com.assignment.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;


import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotesServiceImpl implements INotesService{
    private final JwtService jwtService;
    private final NoteRepository noteRepository;
    private final UserRepository userRepository;
    private final ShareRepository shareRepository;
    @Override
    public List<NoteDTO> getNotesForUser(String bearerToken) {
        var allByUser = noteRepository.findAllByUser(getUserFrom(bearerToken));
        return allByUser;
    }

    @Override
    public UUID getUserIdFrom(String bearerToken) {
        var requestToken = bearerToken.substring(7);
        var userId = jwtService.extractUserName(requestToken);
        log.info("In Application " +userId);
        return UUID.fromString(userId);
    }

    @Override
    @Transactional
    public void saveNote(String bearerToken, NotesRequestModel notesRequestModel) {
      var newNote = Note.builder()
              .title(notesRequestModel.getTitle())
              .content(notesRequestModel.getContent())
              .user(getUserFrom(bearerToken))
              .build();

        noteRepository.save(newNote);
    }

    @Override
    @Transactional
    public void updateNoteById(Long noteId, NoteDTO noteDTO) {
            var note = noteRepository.findById(noteId).get();
            var newNote = Note.builder()
                    .title(noteDTO.title())
                    .content(noteDTO.content())
                    .user(note.getUser())
                    .build();
            noteRepository.save(newNote);
    }

    @Override
    public NoteDTO getNoteById(Long noteId) {
        var note =   noteRepository.findById(noteId).get();
        return new NoteDTO(noteId,note.getTitle(),note.getContent());
    }

    @Override
    @Transactional
    public void deleteNote(String ownerBtoken,Long noteId) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new EntityNotFoundException("Note not found with id: " + noteId));
        var user = getUserFrom(ownerBtoken);
        var userId = getUserFrom(ownerBtoken).getUserId();

        if(userId != note.getUser().getUserId()) {
            log.error("User cannot delete a shared note");
          throw new RuntimeException("Invalid delete");
        }
        shareRepository.deleteByNoteID(user);
        noteRepository.deleteById(noteId);

    }

    @Transactional
    @Override
    public Share shareNote(Long noteId, UUID senderUserId, UUID receiverUserId) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Note not found with id: " + noteId));

        User senderUser = userRepository.findByUserId(senderUserId)
                .orElseThrow(() -> new RuntimeException("Sender user not found with id: " + senderUserId));

        User receiverUser = userRepository.findByUserId(receiverUserId)
                .orElseThrow(() -> new RuntimeException("Receiver user not found with id: " + receiverUserId));

        // Check if the note is already shared with the receiver user
        List<Share> existingShares = shareRepository.findByReceiverUserAndNote(receiverUser, note);
        if (!existingShares.isEmpty()) {
            throw new RuntimeException("Note is already shared with the receiver user");
        }

        var share  = Share.builder()
                .note(note)
                .senderUser(senderUser)
                .receiverUser(receiverUser)
                .build();

        return shareRepository.save(share);

    }

    @Transactional
    @Override
    public void unshareNote(Long shareId) {
        Share share = shareRepository.findById(shareId)
                .orElseThrow(() -> new RuntimeException("Share not found with id: " + shareId));
        shareRepository.delete(share);
    }

    private User getUserFrom(String bearerToken){
        return userRepository.findByUserId(getUserIdFrom(bearerToken)).get();
    }
}
