package com.assignment.utils;


import com.assignment.entity.Note;
import com.assignment.entity.User;
import com.assignment.exception.PermissionException;
import com.assignment.repository.ShareRepository;
import com.assignment.repository.UserRepository;
import com.assignment.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class NoteUtils {
    private final ShareRepository shareRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    public  User getUserFrom(String bearerToken){
        return userRepository.findByUserId(getUserIdFrom(bearerToken)).orElseThrow();
    }

    public  boolean isNoteShared(User user, Note note){
        return !shareRepository.findBySenderUserAndNote(user,note).isEmpty();
    }

    public  void validateOwnership(UUID senderUserId, Note note, String operation){
        log.info(senderUserId+"  "+ note.getUser().getUserId());
        if(!senderUserId.toString().equalsIgnoreCase( note.getUser().getUserId().toString() )){
            log.error("user doesn't own this note");
            throw new PermissionException("user doesn't have permission to "+ operation+" this note");
        }
    }

    public UUID getUserIdFrom(String bearerToken) {
        var requestToken = bearerToken.substring(7);
        var userId = jwtService.extractUserName(requestToken);
        log.info("In Application " +userId);
        return UUID.fromString(userId);
    }
}
