package com.assignment.service;

import com.assignment.entity.Note;
import com.assignment.model.AuthenticationRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface INotesService {
    public List<Note> getNotes(String bearerToken);

    public void saveNote(String bearerToken, Note note);

//    public void saveNote(AuthenticationRequest authenticationRequest);
}
