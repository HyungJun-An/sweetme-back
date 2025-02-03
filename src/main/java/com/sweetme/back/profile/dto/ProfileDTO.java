package com.sweetme.back.profile.dto;

import com.sweetme.back.profile.domain.Profile;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class ProfileDTO {

    // 프론트에서 프로파일 생성 후 백으로 정보를 전송할 때
    // 프론트에서 프로파일 정보를 요청할 시 전송할 때
    private String description;
    private String profileUrl;
    private String imagePath;
    private List<Long> stackIds; // 선택된 스택들의 ID 리스트
    private List<Long> positionIds; // 선택된 포지션들의 ID 리스트

    public static ProfileDTO from(Profile profile) {
        if (profile == null) return null;

        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setDescription(profile.getDescription());
        profileDTO.setProfileUrl(profile.getProfileUrl());
        profileDTO.setImagePath(profile.getImagePath());

        List<Long> stacks = profile.getStacks().stream()
                .map(stack -> stack.getId())
                .collect(Collectors.toList());
        profileDTO.setStackIds(stacks);

        List<Long> positions = profile.getPositions().stream()
                .map(position -> position.getId())
                .collect(Collectors.toList());
        profileDTO.setPositionIds(positions);

        return profileDTO;
    }
}
