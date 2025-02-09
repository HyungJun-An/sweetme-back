package com.sweetme.back.profile.controller;

import com.sweetme.back.auth.dto.UserDTO;
import com.sweetme.back.profile.domain.Profile;
import com.sweetme.back.profile.dto.ProfileDTO;
import com.sweetme.back.profile.dto.SimpleProfileDTO;
import com.sweetme.back.profile.repository.PositionRepository;
import com.sweetme.back.profile.repository.StackRepository;
import com.sweetme.back.profile.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import static org.hibernate.query.sqm.tree.SqmNode.log;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    // 스택과 포지션 목록을 함께 조회하는 API
    @PreAuthorize("permitAll()")
    @GetMapping("/options")
    public Map<String, Object> getProfileOptions() {

//        return profileService.getProfileOptions(); // tbl_stack, tbl_position 에 입력된 데이터 반환
        return profileService.getSimpleProfileOptions();
    }

    @PreAuthorize("isAuthenticated()") // 본인만 조회 가능
    @GetMapping("/me")
    public SimpleProfileDTO getMyProfile(Authentication authentication) {
        // JWT 에서 추출된 UserDTO 가져옴
        UserDTO userDTO = (UserDTO) authentication.getPrincipal();
        Long userId = userDTO.getId();

        // 토큰에서 추출한 사용자 id 로 프로필 조회
//        ProfileDTO profileDTO = profileService.readMyProfile(userId);
        SimpleProfileDTO simpleProfileDTO = profileService.readMySimpleProfile(userId);

        return simpleProfileDTO;
    }

    @PreAuthorize("isAuthenticated()") // 본인만 수정 가능
    @PutMapping("/me")
    public Map<String, String> modifyMyProfile(@RequestBody SimpleProfileDTO simpleProfileDTO, Authentication authentication ) {
        log.info("modify My Profile....");
        log.info("SimpleProfileDTO: " + simpleProfileDTO);

        // 인증 정보로 userDTO 생성
        UserDTO userDTO = (UserDTO) authentication.getPrincipal();
        log.info("userDTO: " + userDTO);

        // 나의 프로필 정보 수정
//        profileService.updateMyProfile(profileDTO, userDTO);
        log.info("update My SimpleProfile....");
        profileService.updateMySimpleProfile(simpleProfileDTO, userDTO);
        return Map.of("result", "SUCCESS");
    }

    @PreAuthorize("permitAll()") // 누구나 조회 가능
    @GetMapping("/{profile_id}")
    public SimpleProfileDTO getUserProfile(@PathVariable("profile_id") Long profileId) {
        // 타 회원 프로필 정보 조회
//        ProfileDTO profileDTO = profileService.readProfile(profileId);
        SimpleProfileDTO simpleProfileDTO = profileService.readSimpleProfile(profileId);

        return simpleProfileDTO;
    }
}
