package com.sweetme.back.profile.controller;

import com.sweetme.back.profile.dto.ProfileDTO;
import com.sweetme.back.profile.repository.PositionRepository;
import com.sweetme.back.profile.repository.StackRepository;
import com.sweetme.back.profile.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;
    private final StackRepository stackRepository;
    private final PositionRepository positionRepository;

    // 스택과 포지션 목록을 함께 조회하는 API
    @GetMapping("/options")
    public Map<String, Object> getProfileOptions() {
        Map<String, Object> options = new HashMap<>();
        options.put("stacks", stackRepository.findAll()); // List<Stack> 으로 모든 스택 반환
        options.put("positions", positionRepository.findAll()); // List<Position> 으로 모든 스택 반환

        return options;
    }

    @GetMapping("/me")
    public Map<String, Object> getMyProfile(@RequestBody ProfileDTO profileDTO) {
        // 나의 프로필 정보 조회

        return null;
    }

    @PutMapping("/me")
    public Map<String, Object> modifyMyProfile(@RequestBody ProfileDTO profileDTO) {
        // 나의 프로필 정보 수정

        return null;
    }

    @GetMapping("/{profile_id}")
    public Map<String, Object> getUserProfile(@PathVariable("profile_id") String profileId) {
        // 타 회원 프로필 정보 조회

        return null;
    }
}
