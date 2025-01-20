package com.sweetme.back.studygroup.controller;

import com.sweetme.back.common.domain.BaseEntity;
import com.sweetme.back.studygroup.domain.Study;
import com.sweetme.back.studygroup.dto.StudyCreateRequest;
import com.sweetme.back.studygroup.repository.StudyRepository;
import com.sweetme.back.studygroup.service.StudyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.HTML;

//@Tag(name = "Study", description = "스터디 관련 API")
@RestController
@RequestMapping("/studies")
@RequiredArgsConstructor
public class StudyController {
    private final StudyService studyService;

    @Operation(summary = "스터디 생성", description = "새로운 스터디를 생성합니다.")
    @PostMapping
    public ResponseEntity<?> createStudy(@RequestBody @Valid StudyCreateRequest request ){// @Valid로 검증
        Study createdStudy = studyService.createStudy(request);
        return ResponseEntity.status(HttpStatus.CREATED) // 성공시 201
                .body(createdStudy);
    }

}
