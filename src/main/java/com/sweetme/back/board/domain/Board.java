package com.sweetme.back.board.domain;

import com.sweetme.back.auth.domain.User;
import com.sweetme.back.common.domain.BaseEntity;
import com.sweetme.back.common.domain.FileEntity;
import com.sweetme.back.studygroup.domain.Study;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tbl_board")
@Getter
@Setter
public class Board extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id", nullable = false)
    private Study study;

    @Column(nullable = false)
    private String title;

    @Column(name = "`desc`", nullable = false, columnDefinition = "TEXT")
    private String description;

    private Integer views = 0;

    @ManyToMany
    @JoinTable(
            name = "tbl_board_file",
            joinColumns = @JoinColumn(name = "board_id"),
            inverseJoinColumns = @JoinColumn(name = "file_id")
    )
    private List<FileEntity> fileEntities = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "tbl_board_like",
            joinColumns = @JoinColumn(name = "board_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> userBoardLikes = new ArrayList<>();
}
