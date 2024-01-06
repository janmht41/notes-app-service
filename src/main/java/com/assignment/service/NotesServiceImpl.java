package com.assignment.service;

import com.assignment.entity.Note;

import com.assignment.entity.User;
import com.assignment.model.NotesRequestModel;
import com.assignment.repository.NoteDTO;
import com.assignment.repository.NoteRepository;
import com.assignment.repository.UserRepository;
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
    @Override
    public List<NoteDTO> getNotes(String bearerToken) {
        return noteRepository.findNotesByUserId(getUserIdFrom(bearerToken));
    }

    private UUID getUserIdFrom(String bearerToken) {
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

    private User getUserFrom(String bearerToken){
        return userRepository.findByUserId(getUserIdFrom(bearerToken)).get();
    }
}
