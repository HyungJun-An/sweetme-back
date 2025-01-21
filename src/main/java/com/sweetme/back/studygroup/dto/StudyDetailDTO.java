package com.sweetme.back.studygroup.dto;

import com.sweetme.back.auth.dto.UserDTO;
import com.sweetme.back.studygroup.domain.Study;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class StudyDetailDTO {
    private Long id;
    private String title;
    private String description;
    private Boolean isOnline;
    private Study.StudyType type;
    private Boolean isOpened;
    private Byte minCapacity;
    private Byte maxCapacity;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private LocationDTO location;
    private Integer views;
    // User 정보도 DTO로 변환
    private UserDTO leader;

    public static StudyDetailDTO from(Study study) {
        StudyDetailDTO dto = new StudyDetailDTO();
        dto.setId(study.getId());
        dto.setTitle(study.getTitle());
        dto.setDescription(study.getDescription());
        dto.setIsOnline(study.getIsOnline());
        dto.setType(study.getType());
        dto.setIsOpened(study.getIsOpened());
        dto.setMinCapacity(study.getMinCapacity());
        dto.setMaxCapacity(study.getMaxCapacity());
        dto.setStartAt(study.getStartAt());
        dto.setEndAt(study.getEndAt());
        dto.setViews(study.getViews());
        dto.setLocation(LocationDTO.from(study.getLocation()));
        dto.setLeader(UserDTO.from(study.getLeader()));
        return dto;
    }
}
