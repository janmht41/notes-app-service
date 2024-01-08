package com.assignment.repository;
import com.assignment.entity.Note;
import com.assignment.entity.Share;
import com.assignment.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ShareRepository extends JpaRepository<Share, Long> {
    List<Share> findByReceiverUserAndNote(User receiverUser, Note note);

    @Modifying
    @Query("DELETE Share s where s.senderUser = ?1")
    void deleteByNoteID(User user);
}
