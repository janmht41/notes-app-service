package com.assignment.service;

import com.assignment.entity.Note;

import com.assignment.model.AuthenticationRequest;
import com.assignment.repository.NoteRepository;
import com.assignment.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotesServiceImpl implements INotesService{
    private final JwtService jwtService;
    private final NoteRepository noteRepository;
    private final UserRepository userRepository;
    @Override
    public List<Note> getNotes(String bearerToken) {
        var email = getEmailFrom(bearerToken);
        return noteRepository.findAllByEmail(email);
    }

    private String getEmailFrom(String bearerToken) {
        var requestToken = bearerToken.substring(7);
        var email = jwtService.extractUserName(requestToken);
        return email;
    }

    @Override
    public void saveNote(String bearerToken, Note note) {
        var email = getEmailFrom(bearerToken);

      var newNote = Note.builder()
              .title(note.getTitle())
              .email(email)
              .content(note.getContent())
              .build();

        noteRepository.save(newNote);
    }
}
