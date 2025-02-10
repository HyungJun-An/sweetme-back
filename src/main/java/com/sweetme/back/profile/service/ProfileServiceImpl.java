package com.sweetme.back.profile.service;

import com.sweetme.back.auth.domain.User;
import com.sweetme.back.auth.dto.UserDTO;
import com.sweetme.back.auth.repository.UserRepository;
import com.sweetme.back.common.domain.FileEntity;
import com.sweetme.back.common.domain.FileRepository;
import com.sweetme.back.profile.domain.Position;
import com.sweetme.back.profile.domain.Profile;
import com.sweetme.back.profile.domain.ProfileConstants;
import com.sweetme.back.profile.domain.Stack;
import com.sweetme.back.profile.dto.*;
import com.sweetme.back.profile.repository.PositionRepository;
import com.sweetme.back.profile.repository.ProfileRepository;
import com.sweetme.back.profile.repository.StackRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;
    private final StackRepository stackRepository;
    private final PositionRepository positionRepository;
    private final UserRepository userRepository;
    private final FileRepository fileRepository;

    @Value("${file.upload.path}")
    private String fileUploadPath;

    // 신규 회원 가입시 사용
    // 빈 프로필 생성
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

    // 다른 회원의 프로필 조회
    @Override
    public ProfileDTO readProfile(Long profileId) {
        validateProfileId(profileId); // profileId 검증

        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new EntityNotFoundException("해당 사용자의 프로필을 찾을 수 없습니다."));

        return ProfileDTO.from(profile);
    }

    // 내 프로필 조회
    @Override
    public ProfileDTO readMyProfile(Long userId) {
        // 스택과 조회
        Profile profile = profileRepository.findWithStacksByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("내 프로필을 찾을 수 없습니다."));

//        log.info("profileWithStack id: " + profile.getId());

        // 포지션과 조회
        Profile profileWithPositions = profileRepository.findWithPositionsByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("내 프로필을 찾을 수 없습니다."));

//        log.info("profileWithPosition id: " + profileWithPositions.getId());

        // 병합
        profile.setPositions(profileWithPositions.getPositions());

//        log.info("profileWithMerge id: " + profile.getId());

        return ProfileDTO.from(profile);
    }


    // 내 프로필 수정
    @Override
    public void updateMyProfile(ProfileDTO profileDTO, UserDTO userDTO) {
        // profileId 검증
        Long profileId = profileDTO.getProfileId();
        validateProfileId(profileId);

        // 프로필 소유자 확인
        Optional<Profile> result = profileRepository.findById(profileId);
        Profile profile = result.orElseThrow(() -> new EntityNotFoundException("프로필을 찾을 수 없습니다."));
        Long userId = profileDTO.getUserDTO().getId();

        // 컨트롤러에서 확인된 인증정보와 비교
        if (!userDTO.getId().equals(userId)) {
            throw new AccessDeniedException("프로필 수정 권한이 없습니다.");
        }

        // 수정된 프로필 데이터 검증
        validateProfileInfo(profileDTO);

        // 스택, 포지션 검증
        List<Stack> stacks = validateAndGetStacks(profileDTO.getStackDTOS());
        List<Position> positions = validateAndGetPositions(profileDTO.getPositionDTOS());

        // 프로필 정보 업데이트
        profile.changeProfile(profileDTO.getDescription(),
                profileDTO.getProfileUrl(),
                profileDTO.getImagePath(),
                stacks,
                positions);

        // 회원 닉네임 업데이트
        String newNickname = profileDTO.getUserDTO().getNickname();
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("해당 ID를 가진 회원이 없습니다."));
        userRepository.save(user);

        // repository 저장
        // Stack, Position 과 연관관계를 지정했으므로 중간 테이블에도 값이 저장됨
        profileRepository.save(profile);
    }

    // 회원 탈퇴 시 프로필 삭제
    @Override
    public void deleteProfile(Long profileId) {
        // profileId 검증
        validateProfileId(profileId);

        profileRepository.deleteById(profileId);
    }

    // 스택, 포지션 전체 목록 조회
    @Override
    public Map<String, Object> getProfileOptions() {
        HashMap<String, Object> options = new HashMap<>();
        List<StackDTO> stackDTOList = stackRepository.findAll().stream().map(StackDTO::from).collect(Collectors.toList());
        List<PositionDTO> positionDTOList = positionRepository.findAll().stream().map(PositionDTO::from).collect(Collectors.toList());
        options.put("stackDTOList", stackDTOList); // 모든 스택 반환
        options.put("positionDTOList", positionDTOList); // 모든 포지션 반환

        return options;
    }

    // 심플 프로필 조회
    @Override
    public SimpleProfileDTO readSimpleProfile(Long profileId) {
        validateProfileId(profileId); // profileId 검증

        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new EntityNotFoundException("해당 사용자의 프로필을 찾을 수 없습니다."));

        return SimpleProfileDTO.from(profile);
    }

    // 내 심플 프로필 조회
    @Override
    public SimpleProfileDTO readMySimpleProfile(Long userId) {
        // 스택과 조회
        Profile profile = profileRepository.findWithStacksByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("내 프로필을 찾을 수 없습니다."));

//        log.info("profileWithStack id: " + profile.getId());

        // 포지션과 조회
        Profile profileWithPositions = profileRepository.findWithPositionsByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("내 프로필을 찾을 수 없습니다."));

//        log.info("profileWithPosition id: " + profileWithPositions.getId());

        // 병합
        profile.setPositions(profileWithPositions.getPositions());

//        log.info("profileWithMerge id: " + profile.getId());

        return SimpleProfileDTO.from(profile);
    }

    // 내 심플 프로필 수정
    @Override
    @Transactional
    public void updateMySimpleProfile(SimpleProfileDTO simpleProfileDTO, MultipartFile profileImage, UserDTO userDTO) {
        /*
        * TODO: 파일 용량 확인
        * */
        if (profileImage != null && !profileImage.isEmpty()) {
            // 기존 프로필 이미지 처리
            Profile existingProfile = profileRepository.findById(simpleProfileDTO.getId())
                    .orElseThrow(() -> new EntityNotFoundException("프로필을 찾을 수 없습니다."));

            String existingImagePath = existingProfile.getImagePath();
            if (existingImagePath != null && !existingImagePath.isEmpty()) {
                // 1. DB 에서 기존 파일 정보 조회 및 삭제
                FileEntity existingFile = fileRepository.findByFileUrl(existingImagePath).orElse(null);
                if (existingFile != null) {
                    fileRepository.delete(existingFile);
                }

                // 2. 물리적 파일 삭제
                String fullPath = fileUploadPath + existingImagePath.replace("/uploads", "");
                try {
                    Files.deleteIfExists(Paths.get(fullPath));
                } catch (IOException e) {
                    log.error("Failed to delete existing profile image: {}", e.getMessage());
                    throw new RuntimeException("기존 프로필 이미지 삭제 중 오류가 발생했습니다.", e);
                }
            }

            // 새 파일 저장
            String savedFilePath = saveFile(profileImage);

            // 새 파일 정보를 DB 에 저장
            FileEntity fileEntity = new FileEntity();
            fileEntity.setFilename(profileImage.getOriginalFilename());
            fileEntity.setFileUrl(savedFilePath);

            FileEntity savedFile = fileRepository.save(fileEntity);

            // 프로필의 이미지 경로 업데이트
            simpleProfileDTO.setImagePath(savedFile.getFileUrl());
        }

        // profileId 검증
        Long profileId = simpleProfileDTO.getId();
        validateProfileId(profileId);

        // 프로필 소유자 확인
        Optional<Profile> result = profileRepository.findById(profileId);
        Profile profile = result.orElseThrow(() -> new EntityNotFoundException("프로필을 찾을 수 없습니다."));
        Long userId = simpleProfileDTO.getSimpleUser().getId();

        // 컨트롤러에서 확인된 인증정보와 비교
        if (!userDTO.getId().equals(userId)) {
            throw new AccessDeniedException("프로필 수정 권한이 없습니다.");
        }

        // 수정된 프로필 데이터 검증
        validateSimpleProfileInfo(simpleProfileDTO);

        // 스택, 포지션 검증
        List<Stack> stacks = validateAndGetSimpleStacks(simpleProfileDTO.getSimpleStacks());
        List<Position> positions = validateAndGetSimplePositions(simpleProfileDTO.getSimplePositions());

        // 프로필 정보 업데이트
        profile.changeProfile(simpleProfileDTO.getDescription(),
                simpleProfileDTO.getProfileUrl(),
                simpleProfileDTO.getImagePath(),
                stacks,
                positions);

        // 회원 닉네임 업데이트
        String newNickname = simpleProfileDTO.getSimpleUser().getNickname();
        log.info("newNickname: " + newNickname);
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));
        user.setNickname(newNickname);
        userRepository.save(user);

        // repository 저장
        // Stack, Position 과 연관관계를 지정했으므로 중간 테이블에도 값이 저장됨
        profileRepository.save(profile);
    }

    @Override
    public Map<String, Object> getSimpleProfileOptions() {
        HashMap<String, Object> options = new HashMap<>();
        List<SimpleStackDTO> simpleStacks = stackRepository.findAll().stream().map(SimpleStackDTO::from).collect(Collectors.toList());
        List<SimplePositionDTO> simplePositions = positionRepository.findAll().stream().map(SimplePositionDTO::from).collect(Collectors.toList());
        options.put("simpleStacks", simpleStacks); // 모든 스택 반환
        options.put("simplePositions", simplePositions); // 모든 포지션 반환

        return options;
    }

    // 파일 저장 유틸리티 메서드
    private String saveFile(MultipartFile file) {
        try {
            // 1. 저장할 파일명 생성 (중복 방지를 위해 UUID 사용)
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String savedFilename = UUID.randomUUID().toString() + fileExtension;

            // 2. 저장 경로 설정 (설정 파일에서 가져오거나 상수로 정의)
            String savePath = fileUploadPath + "/profiles/";
            File saveDir = new File(savePath);
            if (!saveDir.exists()) {
                saveDir.mkdirs(); // 필요한 모든 상위 디렉토리들을 함께 생성
            }

            // 3. 저장할 경로에 파일이 이미 존재하는지 확인
            Path targetPath = Paths.get(savePath + savedFilename);
            if (Files.exists(targetPath)) {
                throw new FileAlreadyExistsException(
                        "File already exists with name: " + savedFilename + ". Please try again"
                );
            }

            // 4. 파일 저장
            Files.copy(file.getInputStream(), targetPath);

            // 4. 저장된 파일의 URL 경로 반환
            return "/uploads/profiles/" + savedFilename;

        } catch (FileAlreadyExistsException e) {
            // 파일이 이미 존재하는 경우
            log.error("File already exists: {}", e.getMessage());
            throw new RuntimeException("File already exists. Please try uploading again.", e);
        } catch (IOException e) {
            log.error("Failed to save file: {}", e.getMessage());
            throw new RuntimeException("Failed to save file.", e);
        }
    }

    // profileId 검증
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
//        validateImagePath(imagePath, "프로필 이미지 경로");

        // stackDTOS, positionDTOS 검증
        List<StackDTO> stackDTOS = profileDTO.getStackDTOS();
        List<PositionDTO> positionDTOS = profileDTO.getPositionDTOS();

        if (stackDTOS == null) {
            throw new IllegalArgumentException("기술 스택 목록이 null일 수 없습니다.");
        }
        if (positionDTOS == null) {
            throw new IllegalArgumentException("직무 목록이 null일 수 없습니다.");
        }
    }

    private void validateSimpleProfileInfo(SimpleProfileDTO simpleProfileDTO) {
        String description = simpleProfileDTO.getDescription();
        String profileUrl = simpleProfileDTO.getProfileUrl();
        String imagePath = simpleProfileDTO.getImagePath();

        // description 길이 검증
        if (description != null && description.length() > ProfileConstants.MAX_DESCRIPTION_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("자기소개는 %d자를 초과할 수 없습니다.", ProfileConstants.MAX_DESCRIPTION_LENGTH)
            );
        }

        // URL 길이 및 형식 검증
        validateUrl(profileUrl, "프로필 URL");

        // 이미지 경로 검증
//        validateImagePath(imagePath, "프로필 이미지 경로");

        // stackDTOS, positionDTOS 검증
        List<SimpleStackDTO> simpleStacks = simpleProfileDTO.getSimpleStacks();
        List<SimplePositionDTO> simplePositions = simpleProfileDTO.getSimplePositions();

        if (simpleStacks == null) {
            throw new IllegalArgumentException("기술 스택 목록이 null일 수 없습니다.");
        }
        if (simplePositions == null) {
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
    private List<Stack> validateAndGetStacks(List<StackDTO> stackDTOS) {
        List<Long> dtoIds = stackDTOS.stream().map(StackDTO::getId).collect(Collectors.toList());

        List<Stack> stacks = stackRepository.findAllById(dtoIds);
        if (stacks.size() != dtoIds.size()) {
            Set<Long> foundIds = stacks.stream()
                    .map(Stack::getId)
                    .collect(Collectors.toSet());

            List<Long> notFoundIds = dtoIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .collect(Collectors.toList());

            throw new IllegalArgumentException("존재하지 않는 스택이 포함되어 있습니다: " + notFoundIds);
        }
        return stacks;
    }

    // position 리스트에 존재하지 않는 id가 포함되었는지 확인
    private List<Position> validateAndGetPositions(List<PositionDTO> positionDTOS) {
        List<Long> dtoIds = positionDTOS.stream().map(PositionDTO::getId).collect(Collectors.toList());

        List<Position> positions = positionRepository.findAllById(dtoIds);
        if (positions.size() != dtoIds.size()) {
            Set<Long> foundIds = positions.stream()
                    .map(Position::getId)
                    .collect(Collectors.toSet());

            List<Long> notFoundIds = dtoIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .collect(Collectors.toList());

            throw new IllegalArgumentException("존재하지 않는 포지션이 포함되어 있습니다: " + notFoundIds);
        }
        return positions;
    }

    private List<Stack> validateAndGetSimpleStacks(List<SimpleStackDTO> simpleStacks) {
        List<Long> dtoIds = simpleStacks.stream().map(SimpleStackDTO::getId).collect(Collectors.toList());

        List<Stack> stacks = stackRepository.findAllById(dtoIds);
        if (stacks.size() != dtoIds.size()) {
            Set<Long> foundIds = stacks.stream()
                    .map(Stack::getId)
                    .collect(Collectors.toSet());

            List<Long> notFoundIds = dtoIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .collect(Collectors.toList());

            throw new IllegalArgumentException("존재하지 않는 스택이 포함되어 있습니다: " + notFoundIds);
        }
        return stacks;
    }

    private List<Position> validateAndGetSimplePositions(List<SimplePositionDTO> simplePositions) {
        List<Long> dtoIds = simplePositions.stream().map(SimplePositionDTO::getId).collect(Collectors.toList());

        List<Position> positions = positionRepository.findAllById(dtoIds);
        if (positions.size() != dtoIds.size()) {
            Set<Long> foundIds = positions.stream()
                    .map(Position::getId)
                    .collect(Collectors.toSet());

            List<Long> notFoundIds = dtoIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .collect(Collectors.toList());

            throw new IllegalArgumentException("존재하지 않는 포지션이 포함되어 있습니다: " + notFoundIds);
        }
        return positions;
    }
}
