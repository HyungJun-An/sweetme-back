package com.sweetme.back.profile.dto;

import com.sweetme.back.auth.domain.User;
import com.sweetme.back.auth.dto.SimpleUserDTO;
import com.sweetme.back.profile.domain.Profile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SimpleProfileDTO {

    private Long id;
    private SimpleUserDTO simpleUser;
    private String description;
    private String profileUrl;
    private String imagePath;
    private List<SimpleStackDTO> simpleStacks;
    private List<SimplePositionDTO> simplePositions;

    // Entity -> DTO
    public static SimpleProfileDTO from(Profile profile) {
        // 회원
        User user = profile.getUser();
        SimpleUserDTO simpleUser = SimpleUserDTO.from(user);

        // 스택
        List<SimpleStackDTO> simpleStacks = profile.getStacks().stream().map(SimpleStackDTO::from).collect(Collectors.toList());

        // 포지션
        List<SimplePositionDTO> simplePositions = profile.getPositions().stream().map(SimplePositionDTO::from).collect(Collectors.toList());

        return SimpleProfileDTO.builder()
                .id(profile.getId())
                .simpleUser(simpleUser)
                .description(profile.getDescription())
                .profileUrl(profile.getProfileUrl())
                .imagePath(profile.getImagePath())
                .simpleStacks(simpleStacks)
                .simplePositions(simplePositions)
                .build();
    }
}
