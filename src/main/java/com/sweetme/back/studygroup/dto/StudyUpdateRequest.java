package com.sweetme.back.studygroup.dto;

import com.sweetme.back.studygroup.domain.Study;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyUpdateRequest {
    private String title;
    private String description;
    private Boolean isOnline;
    private Study.StudyType type;
    private Byte minCapacity;
    private Byte maxCapacity;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private Long locationId;
    private List<Long> positionIds;
    private List<Long> stackIds;
}
