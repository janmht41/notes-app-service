package com.assignment.service;

import com.assignment.entity.Note;
import com.assignment.model.AuthenticationRequest;
import com.assignment.model.NotesRequestModel;
import com.assignment.repository.NoteDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface INotesService {
    public List<NoteDTO> getNotes(String bearerToken);

    public void saveNote(String bearerToken, NotesRequestModel notesRequestModel);

}
