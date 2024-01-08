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
import java.util.UUID;

@RestController()
@RequiredArgsConstructor
public class NotesController {
    private final INotesService notesService;
    @GetMapping("/api/v1/notes")
    public ResponseEntity<List<NoteDTO>> getNotes(@RequestHeader(HttpHeaders.AUTHORIZATION) String bearerToken
            ){
     return ResponseEntity.ok(notesService.getNotesForUser(bearerToken));
    }

    @PostMapping("/api/v1/notes")
    public ResponseEntity<Map<String,Object>> saveNote(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String bearerToken,
            @RequestBody NotesRequestModel notesRequestModel
    ){
       notesService.saveNote(bearerToken, notesRequestModel);
        return ResponseEntity.ok(Map.of("success","Note Created"));
    }

    @PutMapping("/api/v1/notes/{id}")
    public ResponseEntity<Map<String,Object>>updateNote(
            @RequestBody NoteDTO noteDTO,
            @PathVariable(value="id")Long id
    ){
        notesService.updateNoteById(id,noteDTO);
        return ResponseEntity.ok(Map.of("success","Note Updated"));
    }

    @GetMapping("api/v1/notes/{id}")
    public ResponseEntity<NoteDTO> getNote(
            @PathVariable(value = "id") Long id
    ) {
        return  ResponseEntity.ok(notesService.getNoteById(id));
    }

    @DeleteMapping( value="api/v1/notes/{id}")
    public ResponseEntity<Map<String,Object>> deleteNote(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String bToken,
            @PathVariable(value = "id") Long id
    ){

        notesService.deleteNote(bToken,id);
        return ResponseEntity.ok(Map.of("success","Note deleted"));
    }

    @PostMapping(value = "/api/v1/notes/{id}/share")
    public ResponseEntity<Map<String, Object>> shareNote(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String bToken,
            @PathVariable(value = "id")Long id,
            @RequestParam(value = "recipientId") String recipientId
    ){
          var userId = notesService.getUserIdFrom(bToken);
          notesService.shareNote(id,userId, UUID.fromString(recipientId) );
         return ResponseEntity.ok(Map.of("success","Note shared"));
    }
/*
]
Todo

    POST /api/notes/:id/share: share a note with another user for the authenticated user.
    GET /api/search?q=:query: search for notes based on keywords for the authenticated user.
 */

}
