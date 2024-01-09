package com.assignment.service;

import com.assignment.entity.Note;

import com.assignment.entity.Share;
import com.assignment.entity.User;
import com.assignment.exception.InvalidSearchFieldException;
import com.assignment.exception.NoteNotFoundException;
import com.assignment.exception.PermissionException;
import com.assignment.model.NotesRequestModel;
import com.assignment.repository.NoteDTO;
import com.assignment.repository.NoteRepository;
import com.assignment.repository.ShareRepository;
import com.assignment.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;


import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotesServiceImpl implements INotesService{
    private final JwtService jwtService;
    private final NoteRepository noteRepository;
    private final UserRepository userRepository;
    private final ShareRepository shareRepository;

    private static final List<String> SEARCHABLE_FIELDS = Arrays.asList("title","content");
    @Override
    public List<NoteDTO> getNotesForUser(String bearerToken) {
        return noteRepository.findAllByUser(getUserFrom(bearerToken));
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
    public void updateNoteById(String bToken,Long noteId, NoteDTO noteDTO) {
            var note = noteRepository.findById(noteId).orElseThrow();
            validateOwnership(getUserIdFrom(bToken),note,"update");
            var newNote = Note.builder()
                    .title(noteDTO.title())
                    .content(noteDTO.content())
                    .user(note.getUser())
                    .build();
            noteRepository.save(newNote);
    }

    @Override
    public NoteDTO getNoteById(String bToken, Long noteId) {
        var note =   noteRepository.findById(noteId)
                .orElseThrow(() -> new NoteNotFoundException("Note not found with id: " + noteId));
        validateOwnership(getUserIdFrom(bToken),note,"fetch");
        return new NoteDTO(noteId,note.getTitle(),note.getContent());
    }

    @Override
    @Transactional
    public void deleteNote(String ownerToken,Long noteId) throws NoteNotFoundException {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new NoteNotFoundException("Note not found with id: " + noteId));
        var noteOwner = getUserFrom(ownerToken);
        var userId = getUserFrom(ownerToken).getUserId();
        validateOwnership(userId, note, "delete");
       if(isNoteShared(noteOwner, note)) shareRepository.deleteAllSharedBy(noteId,noteOwner);
       noteRepository.deleteById(noteId);
    }

    @Transactional
    @Override
    public Share shareNote(Long noteId, UUID senderUserId, UUID receiverUserId) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new NoteNotFoundException("Note not found with id: " + noteId));

        User senderUser = userRepository.findByUserId(senderUserId)
                .orElseThrow(() -> new RuntimeException("Sender user not found with id: " + senderUserId));

        validateOwnership(senderUserId,note,"share");

        User receiverUser = userRepository.findByUserId(receiverUserId)
                .orElseThrow(() -> new RuntimeException("Receiver user not found with id: " + receiverUserId));


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

    /**
     * @param text   text to search for
     * @param limit  maximum number of elements to search for
     * @param fields name of all the fields to search on
     */
    @Override
    public List<NoteDTO> searchNotes(String bToken, String text, List<String> fields, int limit) {

        List<String> fieldsToSearchBy = fields.isEmpty() ? SEARCHABLE_FIELDS : fields;

        boolean containsInvalidField = fieldsToSearchBy
                .stream()
                .anyMatch(f -> !SEARCHABLE_FIELDS.contains(f));

        if(containsInvalidField) {
            throw new InvalidSearchFieldException("Not a valid searchable field");
        }
        List<NoteDTO> searchableNotes = getNotesForUser(bToken);

        return noteRepository.searchBy(
                text, limit, fieldsToSearchBy.toArray(new String[0]))
                .stream()
                .map(note->new NoteDTO(note.getId(),note.getTitle(),note.getContent()))
                .filter(searchableNotes::contains)
                .collect(Collectors.toList());

    }

    private User getUserFrom(String bearerToken){
        return userRepository.findByUserId(getUserIdFrom(bearerToken)).orElseThrow();
    }

    private boolean isNoteShared(User user, Note note){
       return !shareRepository.findBySenderUserAndNote(user,note).isEmpty();
    }

    private void validateOwnership(UUID senderUserId, Note note, String operation){
        log.info(senderUserId+"  "+ note.getUser().getUserId());
        if(!senderUserId.toString().equalsIgnoreCase( note.getUser().getUserId().toString() )){
            log.error("user doesn't own this note");
            throw new PermissionException("user doesn't have permission to "+ operation+" this note");
        }
    }
}
