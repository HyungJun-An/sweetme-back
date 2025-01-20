package com.sweetme.back.studygroup.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sweetme.back.auth.domain.User;
import com.sweetme.back.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tbl_study")
@Getter
@Setter
@JsonIgnoreProperties({"userStudyLikes"}) // 순환 참조 방지
public class Study extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "study_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leader_id", nullable = false)
    private User leader;

    @Column(nullable = false)
    private String title;

    @Column(name = "`desc`", nullable = false)
    private String description;

    @Column(name = "is_online", nullable = false)
    private Boolean isOnline = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StudyType type = StudyType.STUDY;

    @Column(name = "is_opened", nullable = false)
    private Boolean isOpened = true;

    @Column(name = "min_cap", nullable = false)
    private Byte minCapacity = 2;

    @Column(name = "max_cap", nullable = false)
    private Byte maxCapacity = 8;

    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;

    @Column(name = "end_at", nullable = false)
    private LocalDateTime endAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Location location;

    private Integer views = 0;

    public enum StudyType {
        STUDY, PROJECT
    }

    @ManyToMany
    @JoinTable(
            name = "tbl_study_like",
            joinColumns = @JoinColumn(name = "study_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> userStudyLikes = new ArrayList<>();
}