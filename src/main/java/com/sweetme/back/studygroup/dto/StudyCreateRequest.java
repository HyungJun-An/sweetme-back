package com.sweetme.back.studygroup.dto;


import com.sweetme.back.profile.domain.Position;
import com.sweetme.back.profile.domain.Stack;
import com.sweetme.back.studygroup.domain.Study;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyCreateRequest {
    @NotBlank(message = "스터디 제목은 필수입니다")
    private String title;

    @NotBlank(message = "스터디 설명은 필수입니다.")
    private String description;

    @Min(value = 2, message = "최소 인원은 2명 이상이어야 합니다")
    private Byte minCapacity;

    @Max(value = 8, message = "최대 인원은 8명 이하여야 합니다")
    private Byte maxCapacity;

    @NotNull(message = "시작 날짜는 필수입니다")
    private LocalDateTime startAt;

    @NotNull(message = "종료 날짜는 필수입니다")
    private LocalDateTime endAt;

    @NotNull(message = "온/오프라인 여부는 필수입니다")
    private Boolean isOnline;

    private Long locationId; // 오프라인인 경우 필수

    @NotNull(message = "스터디 유형은 필수입니다")
    private Study.StudyType type;

    @NotEmpty(message = "최소 하나의 포지션을 선택해야 합니다")
    private List<Long> positionIds;

    @NotEmpty(message = "최소 하나의 기술 스택을 선택해야 합니다")
    private List<Long> stackIds;

    private Integer penaltyCount = 0; // 패널티 기본값 0
}
