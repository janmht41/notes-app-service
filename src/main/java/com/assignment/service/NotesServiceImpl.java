package com.assignment.service;

import com.assignment.entity.Note;

import com.assignment.entity.User;
import com.assignment.model.NotesRequestModel;
import com.assignment.repository.NoteRepository;
import com.assignment.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotesServiceImpl implements INotesService{
    private final JwtService jwtService;
    private final NoteRepository noteRepository;
    private final UserRepository userRepository;
    @Override
    public List<Note> getNotes(String bearerToken) {
        System.out.println("here bitch");
        return noteRepository.findNotesByUserId(getUserIdFrom(bearerToken));
    }

    private UUID getUserIdFrom(String bearerToken) {
        var requestToken = bearerToken.substring(7);
        var userId = jwtService.extractUserName(requestToken);
        System.out.println("In controller:" +UUID.fromString(userId));
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
