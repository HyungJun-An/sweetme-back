package com.sweetme.back.studygroup.dto;

import com.sweetme.back.studygroup.domain.Study;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudySearchRequest {

    private Long locationId; // 지역별 검색
    private Boolean isOnline; // 온라인 여부로 검색
    private Study.StudyType type; // 스터디 타입으로 검색
    private Boolean isOpened; // 현재 운영중인 스터디로 검색
    private boolean allStudies = false; // 전체 조회 여부 플래그 추가

    //페이징 정보
    private int page = 0; // jpa 페이징은 0 부터 시작
    private int size = 10;
}
