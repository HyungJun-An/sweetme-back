package com.sweetme.back.profile.service;

import com.sweetme.back.auth.domain.User;
import com.sweetme.back.auth.dto.AuthUserDTO;
import com.sweetme.back.profile.domain.Position;
import com.sweetme.back.profile.domain.Profile;
import com.sweetme.back.profile.domain.ProfileConstants;
import com.sweetme.back.profile.domain.Stack;
import com.sweetme.back.profile.dto.ProfileDTO;
import com.sweetme.back.profile.repository.PositionRepository;
import com.sweetme.back.profile.repository.ProfileRepository;
import com.sweetme.back.profile.repository.StackRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.sweetme.back.profile.domain.ProfileConstants.*;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;
    private final StackRepository stackRepository;
    private final PositionRepository positionRepository;

    // 신규 회원 가입시 사용
    @Override
    public Profile createEmptyProfile(User user) {
        Profile profile = new Profile();

        profile.setUser(user);
        profile.setDescription("");
        profile.setProfileUrl("");
        profile.setImagePath("");
        profile.setStacks(new ArrayList<>());
        profile.setPositions(new ArrayList<>());

        return profileRepository.save(profile);
    }

    // ProfileDTO 엔티티 반환
    @Override
    public ProfileDTO readProfile(Long profileId) {
        validateProfileId(profileId); // userId 검증

        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new EntityNotFoundException("프로필을 찾을 수 없습니다."));

        return entityToDTO(profile);
    }


    // 반환타입 ProfileDTO 로 변경
    @Override
    public void updateProfile(ProfileDTO profileDTO) {

        // 현재 인증된 사용자 정보 가져오기
        AuthUserDTO currentUser = (AuthUserDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // profileId 검증
        Long profileId = profileDTO.getProfileId();
        validateProfileId(profileId);

        // 프로필 소유자 확인
        Optional<Profile> result = profileRepository.findById(profileId);
        Profile profile = result.orElseThrow(() -> new EntityNotFoundException("프로필을 찾을 수 없습니다."));
        Long userId = profileDTO.getUserId();

        if (!currentUser.getId().equals(userId)) {
            throw new AccessDeniedException("프로필 수정 권한이 없습니다.");
        }

        // 프로필 정보 검증
        validateProfileInfo(profileDTO);

        // stack, position id 검증
        List<Stack> stacks = validateAndGetStacks(profileDTO.getStackIds());
        List<Position> positions = validateAndGetPositions(profileDTO.getPositionIds());

        // 프로필 정보 업데이트
        profile.changeProfile(profileDTO.getDescription(),
                profileDTO.getProfileUrl(),
                profileDTO.getImagePath(),
                stacks,
                positions);

        // repository 저장
        // Stack, Position 과 연관관계를 지정했으므로 중간 테이블에도 값이 저장됨
        profileRepository.save(profile);
    }

    @Override
    public void deleteProfile(Long profileId) {
        profileRepository.deleteById(profileId);
    }

    @Override
    public Map<String, Object> getProfileOptions() {
        HashMap<String, Object> options = new HashMap<>();
        options.put("stacks", stackRepository.findAll()); // List<Stack> 으로 모든 스택 반환
        options.put("positions", positionRepository.findAll()); // List<Position> 으로 모든 포지션 반환

        return options;
    }

    // userId 검증
    private void validateProfileId(Long profileId) {
        if (profileId == null) {
            throw new IllegalArgumentException("프로필 ID는 null일 수 없습니다.");
        }
        if (profileId <= 0) {
            throw new IllegalArgumentException("프로필 ID는 양수여야 합니다.");
        }
    }

    // 프로필 정보 검증
    private void validateProfileInfo(ProfileDTO profileDTO) {
        String description = profileDTO.getDescription();
        String profileUrl = profileDTO.getProfileUrl();
        String imagePath = profileDTO.getImagePath();

        // description 길이 검증
        if (description != null && description.length() > ProfileConstants.MAX_DESCRIPTION_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("자기소개는 %d자를 초과할 수 없습니다.", ProfileConstants.MAX_DESCRIPTION_LENGTH)
            );
        }

        // URL 길이 및 형식 검증
        validateUrl(profileUrl, "프로필 URL");

        // 이미지 경로 검증
        validateImagePath(imagePath, "프로필 이미지 경로");

        // stackIds, positionIds 검증
        List<Long> stackIds = profileDTO.getStackIds();
        List<Long> positionIds = profileDTO.getPositionIds();

        if (stackIds == null) {
            throw new IllegalArgumentException("기술 스택 목록이 null일 수 없습니다.");
        }
        if (positionIds == null) {
            throw new IllegalArgumentException("직무 목록이 null일 수 없습니다.");
        }
    }

    private void validateImagePath(String imagePath, String fieldName) {
        // null 체크
        if (imagePath == null) {
            throw new IllegalArgumentException(
                    String.format("%s는 null일 수 없습니다.", fieldName)
            );
        }

        // 빈 문자열 허용
        if (imagePath.trim().isEmpty()) {
            return;
        }

        // 길이 검증
        if (imagePath.length() > ProfileConstants.MAX_URL_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("%s는 %d자를 초과할 수 없습니다.", fieldName, ProfileConstants.MAX_URL_LENGTH)
            );
        }

        // 절대 경로인지 확인
        if (imagePath.startsWith("/") || imagePath.contains("..")) {
            throw new IllegalArgumentException(
                    String.format("%s는 상대 경로여야 합니다.", fieldName)
            );
        }
    }

    // URL 검증
    private void validateUrl(String url, String fieldName) {
        // Null 체크
        if (url == null) {
            throw new IllegalArgumentException(
                    String.format("%s는 null일 수 없습니다.", fieldName)
            );
        }

        // 빈 문자열 허용
        if (url.trim().isEmpty()) {
            return;
        }

        // 길이 검증
        if (url.length() > ProfileConstants.MAX_URL_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("%s는 %d자를 초과할 수 없습니다.", fieldName, ProfileConstants.MAX_URL_LENGTH)
            );
        }

        // 형식 검증
        if (!Pattern.matches(ProfileConstants.URL_PATTERN, url)) {
            throw new IllegalArgumentException(
                    String.format("%s의 형식이 올바르지 않습니다.", fieldName)
            );
        }
    }

    // stack 리스트에 존재하지 않는 id가 포함되었는지 확인
    private List<Stack> validateAndGetStacks(List<Long> stackIds) {
        List<Stack> stacks = stackRepository.findAllById(stackIds);
        if (stacks.size() != stackIds.size()) {
            Set<Long> foundIds = stacks.stream()
                    .map(Stack::getId)
                    .collect(Collectors.toSet());

            List<Long> notFoundIds = stackIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .collect(Collectors.toList());

            throw new IllegalArgumentException("존재하지 않는 스택이 포함되어 있습니다: " + notFoundIds);
        }
        return stacks;
    }

    // position 리스트에 존재하지 않는 id가 포함되었는지 확인
    private List<Position> validateAndGetPositions(List<Long> positionIds) {
        List<Position> positions = positionRepository.findAllById(positionIds);
        if (positions.size() != positionIds.size()) {
            Set<Long> foundIds = positions.stream()
                    .map(Position::getId)
                    .collect(Collectors.toSet());

            List<Long> notFoundIds = positionIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .collect(Collectors.toList());

            throw new IllegalArgumentException("존재하지 않는 스택이 포함되어 있습니다: " + notFoundIds);
        }
        return positions;
    }
}
