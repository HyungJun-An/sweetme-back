package com.sweetme.back.task.domain;

import com.sweetme.back.auth.domain.User;
import com.sweetme.back.common.domain.BaseEntity;
import com.sweetme.back.studygroup.domain.Study;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tbl_task")
@Getter
@Setter
public class Task extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_id")
    private Long id;

    // 소속 스터디/프로젝트
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id", nullable = false)
    private Study study;

    // 태스크 생성자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 태스크 제목
    @Column(nullable = false)
    private String title;

    // 태스크 설명
    @Column(name = "`desc`", nullable = false, columnDefinition = "TEXT")
    private String description;

    // 태스크 유형 (과제/프로젝트/기타)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskType type;

    // 태스크 상태 (시작전/진행중/완료)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status = TaskStatus.NOT_STARTED;

    // 시작일시
    @Column(name = "start_at")
    private LocalDateTime startAt;

    // 종료일시
    @Column(name = "end_at")
    private LocalDateTime endAt;

    // 태스크 할당된 사용자 목록
    @ManyToMany
    @JoinTable(
            name = "tbl_assigned",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> assignedUsers = new HashSet<>();

    public enum TaskType {
        ASSIGNMENT, // 과제
        PROJECT,    // 프로젝트
        ETC        // 기타
    }

    public enum TaskStatus {
        NOT_STARTED,    // 시작전
        IN_PROGRESS,    // 진행중
        COMPLETED       // 완료
    }
}
