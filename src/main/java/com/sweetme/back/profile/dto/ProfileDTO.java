package com.sweetme.back.profile.dto;

import com.sweetme.back.auth.domain.User;
import com.sweetme.back.auth.dto.UserDTO;
import com.sweetme.back.profile.domain.Profile;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileDTO {

    // 프론트에서 프로파일 생성 후 백으로 정보를 전송할 때
    // 프론트에서 프로파일 정보를 요청할 시 전송할 때
    private Long profileId;
    private UserDTO userDTO;
    private String description;
    private String profileUrl;
    private String imagePath;
    private List<StackDTO> stackDTOS; // 선택된 스택들의 ID 리스트
    private List<PositionDTO> positionDTOS; // 선택된 포지션들의 ID 리스트

    // Entity -> DTO
    public static ProfileDTO from(Profile profile) {
        if (profile == null) return null;

        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setProfileId(profile.getId());
        profileDTO.setUserDTO(UserDTO.from(profile.getUser()));
        profileDTO.setDescription(profile.getDescription());
        profileDTO.setProfileUrl(profile.getProfileUrl());
        profileDTO.setImagePath(profile.getImagePath());

        List<StackDTO> stackDTOList = profile.getStacks().stream()
                .map(StackDTO::from)
                .collect(Collectors.toList());
        profileDTO.setStackDTOS(stackDTOList);

        List<PositionDTO> positionDTOList = profile.getPositions().stream()
                .map(PositionDTO::from)
                .collect(Collectors.toList());
        profileDTO.setPositionDTOS(positionDTOList);

        return profileDTO;
    }

    // DTO -> Entity

}
