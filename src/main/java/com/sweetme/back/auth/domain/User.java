package com.sweetme.back.auth.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sweetme.back.board.domain.Board;
import com.sweetme.back.common.domain.BaseEntity;
import com.sweetme.back.studygroup.domain.Study;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tbl_user")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"studyLikes", "boardLikes"})
@JsonIgnoreProperties({"studyLikes", "boardLikes"}) // 순환 참조 방지
public class User extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "login_type", nullable = false)
    private LoginType loginType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private UserStatus status = UserStatus.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private UserRole role = UserRole.ROLE_USER;

    @Column(nullable = false)
    private String password;

    public enum LoginType {
        EMAIL, KAKAO, NAVER
    }

    public enum UserStatus {
        ACTIVE, INACTIVE
    }

    public enum UserRole {
        ROLE_USER, ROLE_ADMIN
    }

    @ManyToMany(mappedBy = "userStudyLikes")
    @Builder.Default
    private List<Study> studyLikes = new ArrayList<>();

    @ManyToMany(mappedBy = "userBoardLikes")
    @Builder.Default
    private List<Board> boardLikes = new ArrayList<>();

    public void changeRole(UserRole role) {
        this.role = role;
    }

    public void changeStatus(UserStatus status) {
        this.status = status;
    }

    public void changeNickname(String nickname) {
        this.nickname = nickname;
    }

    public void changePassword(String password) {
        this.password = password;
    }
}