package com.assignment.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Set;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Note {
    @Id
    @Column(name = "note_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="note_title")
    private String title;

    @Column(name = "note_content")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id" )
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @OneToMany(mappedBy = "note", cascade = CascadeType.ALL,orphanRemoval = true)
    private Set<Share> shares;

//    public void addShare(Share share)
//    {
//        shares.add(share);
//    }
//    public Note removeShare(Share share)
//    {
//        shares.remove(share);
//        shares.setNote(null);
//        return this;
//    }

}
