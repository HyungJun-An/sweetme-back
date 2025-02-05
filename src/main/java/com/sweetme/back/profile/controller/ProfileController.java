package com.sweetme.back.profile.controller;

import com.sweetme.back.profile.domain.Profile;
import com.sweetme.back.profile.dto.ProfileDTO;
import com.sweetme.back.profile.repository.PositionRepository;
import com.sweetme.back.profile.repository.StackRepository;
import com.sweetme.back.profile.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    // 스택과 포지션 목록을 함께 조회하는 API
    @PreAuthorize("permitAll()")
    @GetMapping("/options")
    public Map<String, Object> getProfileOptions() {

        return profileService.getProfileOptions(); // tbl_stack, tbl_position 에 입력된 데이터 반환
    }

    @PreAuthorize("isAuthenticated()") // 본인만 조회 가능
    @GetMapping("/me")
    public Map<String, Object> getMyProfile(@RequestBody Long profileId) {
        // 나의 프로필 정보 조회
        ProfileDTO profileDTO = profileService.readProfile(profileId);

        return null;
    }

    @PreAuthorize("isAuthenticated()") // 본인만 수정 가능
    @PutMapping("/me")
    public Map<String, Object> modifyMyProfile(@RequestBody ProfileDTO profileDTO) {
        // 나의 프로필 정보 수정

        return null;
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/{profile_id}")
    public Map<String, Object> getUserProfile(@PathVariable("profile_id") Long profileId) {
        // 타 회원 프로필 정보 조회

        return null;
    }
}
