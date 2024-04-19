package com.seoultech.codemos.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
public class LeaderBoardEntity {
     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     private int id;
     private int score;
     private String time;

     @ManyToOne
     @JoinColumn(name = "user_id")
     @JsonBackReference
    private UserEntity user;
    private String email; // User 객체의 loginId
    private String nickname; // User 객체의 nickname
    @OneToOne(mappedBy = "leaderBoard", cascade = CascadeType.ALL)
    @JoinColumn(name = "code_id")
    private CodeEntity codeEntity;
    public CodeEntity getCodeEntity() {
        return codeEntity;
    }
    public void setCodeEntity(CodeEntity codeEntity) {
        this.codeEntity = codeEntity;
    }
}
