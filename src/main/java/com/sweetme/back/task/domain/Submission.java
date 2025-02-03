package com.sweetme.back.task.domain;

import com.sweetme.back.common.domain.BaseEntity;
import com.sweetme.back.common.domain.FileEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tbl_submission")
@Getter
@Setter
public class Submission extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "submission_id")
    private Long id;

    // 제출 대상 태스크
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    // 조회수
    private Integer views = 0;

    // 제출물 설명
    @Column(name = "`desc`", nullable = false, columnDefinition = "TEXT")
    private String description;

    // 제출물 제목
    @Column(nullable = false)
    private String title;

    // 첨부 파일 목록
    @ManyToMany
    @JoinTable(
            name = "tbl_submission_file",
            joinColumns = @JoinColumn(name = "submission_id"),
            inverseJoinColumns = @JoinColumn(name = "file_id")
    )
    private List<FileEntity> fileEntities = new ArrayList<>();
}
