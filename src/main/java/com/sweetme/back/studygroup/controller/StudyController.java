package com.sweetme.back.studygroup.controller;

import com.sweetme.back.common.domain.BaseEntity;
import com.sweetme.back.studygroup.domain.Study;
import com.sweetme.back.studygroup.dto.StudyCreateRequest;
import com.sweetme.back.studygroup.dto.StudyDetailDTO;
import com.sweetme.back.studygroup.dto.StudySearchRequest;
import com.sweetme.back.studygroup.repository.StudyRepository;
import com.sweetme.back.studygroup.service.StudyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.HTML;
import java.util.List;
import java.util.stream.Collectors;

//@Tag(name = "Study", description = "스터디 관련 API")
@RestController
@RequestMapping("/studies")
@RequiredArgsConstructor
public class StudyController {
    private final StudyService studyService;

    // 스터디 목록 조회 (검색/필터링)
    @GetMapping
    public ResponseEntity<Page<StudyDetailDTO>> getStudies(
            @RequestParam(defaultValue = "1") int page, // 1부터 시작
            @RequestParam(defaultValue = "10") int size
    ) {
        // 내부적으로는 0부터 시작하므로 1을 빼줌
        StudySearchRequest request = new StudySearchRequest();
        request.setPage(page - 1); // 1을 빼서 0부터 시작하게 변환
        request.setSize(size);

        Page<Study> studies = studyService.getStudies(request);
        Page<StudyDetailDTO> response = studies.map(StudyDetailDTO::from);
        return ResponseEntity.ok(response);
    }

    // 모든 스터디 조회
    @GetMapping("/all")
    public ResponseEntity<Page<StudyDetailDTO>> getAllStudies(
            @RequestParam(defaultValue = "1") int page, // 1부터 시작
            @RequestParam(defaultValue = "10") int size
    ) {
        // 내부적으로는 0부터 시작하므로 1을 빼줌
        StudySearchRequest request = new StudySearchRequest();
        request.setPage(page - 1); // 1을 빼서 0부터 시작하게 변환
        request.setSize(size);

        Page<Study> studies = studyService.getAllStudies(request);
        Page<StudyDetailDTO> response = studies.map(StudyDetailDTO::from);
        return ResponseEntity.ok(response);
    }

    // 스터디 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<StudyDetailDTO> getStudy(@PathVariable Long id) {
        Study study = studyService.getStudy(id);
        return ResponseEntity.ok(StudyDetailDTO.from(study));
    }



//    @Operation(summary = "스터디 생성", description = "새로운 스터디를 생성합니다.")
    @PostMapping
    public ResponseEntity<?> createStudy(@RequestBody @Valid StudyCreateRequest request ){// @Valid로 검증
        Study createdStudy = studyService.createStudy(request);
        return ResponseEntity.status(HttpStatus.CREATED) // 성공시 201
                .body(createdStudy);
    }

}
