package com.assignment.controller;


import com.assignment.model.NotesRequestModel;
import com.assignment.repository.NoteDTO;
import com.assignment.service.INotesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController()
@RequiredArgsConstructor
public class NotesController {
    private final INotesService notesService;
    @GetMapping("/api/v1/notes")
    public ResponseEntity<List<NoteDTO>> getNotes(@RequestHeader(HttpHeaders.AUTHORIZATION) String bearerToken
            ){
     return ResponseEntity.ok(notesService.getNotes(bearerToken));
    }

    @PostMapping("/api/v1/notes")
    public ResponseEntity<Map<String,Object>> saveNote(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String bearerToken,
            @RequestBody NotesRequestModel notesRequestModel
    ){
       notesService.saveNote(bearerToken, notesRequestModel);
        return ResponseEntity.ok(Map.of("success","Note Created"));
    }

}
