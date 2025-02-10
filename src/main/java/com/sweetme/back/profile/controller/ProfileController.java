package com.sweetme.back.profile.controller;

import com.sweetme.back.auth.dto.UserDTO;
import com.sweetme.back.profile.domain.Profile;
import com.sweetme.back.profile.domain.ProfileConstants;
import com.sweetme.back.profile.dto.ProfileDTO;
import com.sweetme.back.profile.dto.SimpleProfileDTO;
import com.sweetme.back.profile.repository.PositionRepository;
import com.sweetme.back.profile.repository.StackRepository;
import com.sweetme.back.profile.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.hibernate.query.sqm.tree.SqmNode.log;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    private final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/jpg",
            "image/png",
            "image/gif"
    );

    // 스택과 포지션 목록을 함께 조회하는 API
    @PreAuthorize("permitAll()")
    @GetMapping("/options")
    public Map<String, Object> getProfileOptions() {
        return profileService.getSimpleProfileOptions();
    }

    @PreAuthorize("isAuthenticated()") // 본인만 조회 가능
    @GetMapping("/me")
    public SimpleProfileDTO getMyProfile(Authentication authentication) {
        // JWT 에서 추출된 UserDTO 가져옴
        UserDTO userDTO = (UserDTO) authentication.getPrincipal();
        Long userId = userDTO.getId();

        // 토큰에서 추출한 사용자 id 로 프로필 조회
        SimpleProfileDTO simpleProfileDTO = profileService.readMySimpleProfile(userId);

        return simpleProfileDTO;
    }

    @PreAuthorize("isAuthenticated()") // 본인만 수정 가능
    @PutMapping(value = "/me", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, String> modifyProfileWithImage(
            @RequestPart(value = "profileData") SimpleProfileDTO simpleProfileDTO,
            @RequestPart(value = "file", required = false) MultipartFile profileImage,
            Authentication authentication) throws IOException {
        validateProfileImage(profileImage);

        log.info("modify My Profile With Image....");
        log.info("SimpleProfileDTO: " + simpleProfileDTO);
        log.info("Profile Image: " + (profileImage != null ? profileImage.getOriginalFilename() : "no image"));

        // 인증 정보로 userDTO 생성
        UserDTO userDTO = (UserDTO) authentication.getPrincipal();
        log.info("userDTO: " + userDTO);

        // 나의 프로필 정보 수정
        log.info("update My SimpleProfile....");
        profileService.updateMySimpleProfile(simpleProfileDTO, profileImage, userDTO);

        return Map.of("result", "SUCCESS");
    }

    @PreAuthorize("isAuthenticated()") // 본인만 수정 가능
    @PutMapping(value = "/me", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> modifyMyProfile(
            @RequestBody SimpleProfileDTO simpleProfileDTO,
            Authentication authentication) {

        log.info("modify My Profile....");
        log.info("SimpleProfileDTO: " + simpleProfileDTO);

        // 인증 정보로 userDTO 생성
        UserDTO userDTO = (UserDTO) authentication.getPrincipal();
        log.info("userDTO: " + userDTO);

        // 나의 프로필 정보 수정
        log.info("update My SimpleProfile....");
        profileService.updateMySimpleProfile(simpleProfileDTO, null, userDTO);

        return Map.of("result", "SUCCESS");
    }

    @PreAuthorize("permitAll()") // 누구나 조회 가능
    @GetMapping("/{profile_id}")
    public SimpleProfileDTO getUserProfile(@PathVariable("profile_id") Long profileId) {
        // 타 회원 프로필 정보 조회
        SimpleProfileDTO simpleProfileDTO = profileService.readSimpleProfile(profileId);

        return simpleProfileDTO;
    }

    private void validateProfileImage(MultipartFile file) throws IOException {
        // 파일 크기 검증
        if (file.getSize() > ProfileConstants.MAX_FILE_SIZE) {
            throw new IllegalArgumentException("파일 크기는 5MB를 초과할 수 없습니다.");
        }

        // 파일 타입 검증
        String contentType = file.getContentType();
        if (!ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("JPG, PNG, GIF 파일만 업로드 가능합니다.");
        }

        // 이미지 크기 검증
        BufferedImage image = ImageIO.read(file.getInputStream());
        if (image == null) {
            throw new IllegalArgumentException("유효하지 않은 이미지 파일입니다.");
        }

        int width = image.getWidth();
        int height = image.getHeight();

        if (width < ProfileConstants.MIN_IMAGE_DIMENSION || height < ProfileConstants.MIN_IMAGE_DIMENSION) {
            throw new IllegalArgumentException(
                    String.format("이미지 크기는 최소 %dx%d 픽셀이어야 합니다.", ProfileConstants.MIN_IMAGE_DIMENSION, ProfileConstants.MIN_IMAGE_DIMENSION)
            );
        }

        if (width > ProfileConstants.MAX_IMAGE_DIMENSION || height > ProfileConstants.MAX_IMAGE_DIMENSION) {
            throw new IllegalArgumentException(
                    String.format("이미지 크기는 최대 %dx%d 픽셀을 초과할 수 없습니다.", ProfileConstants.MAX_IMAGE_DIMENSION, ProfileConstants.MAX_IMAGE_DIMENSION)
            );
        }
    }
}
