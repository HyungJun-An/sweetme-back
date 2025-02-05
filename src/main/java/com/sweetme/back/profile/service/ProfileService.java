package com.sweetme.back.profile.service;

import com.sweetme.back.auth.domain.User;
import com.sweetme.back.profile.domain.Profile;
import com.sweetme.back.profile.dto.ProfileDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Transactional
public interface ProfileService {

    Profile createEmptyProfile(User user);

    ProfileDTO readProfile(Long profileId);

    void updateProfile(ProfileDTO profileDTO);

    void deleteProfile(Long profileId);

    Map<String, Object> getProfileOptions();

    default ProfileDTO entityToDTO(Profile profile) {

        ProfileDTO dto = new ProfileDTO();

        dto.setProfileId(profile.getId());
        dto.setUserId(profile.getUser().getId());
        dto.setDescription(profile.getDescription());
        dto.setProfileUrl(profile.getProfileUrl());
        dto.setImagePath(profile.getImagePath());

        List<Long> stackIds = profile.getStacks().stream().map(stack -> stack.getId()).collect(Collectors.toList());
        List<Long> positionIds = profile.getPositions().stream().map(position -> position.getId()).collect(Collectors.toList());

        dto.setStackIds(stackIds);
        dto.setPositionIds(positionIds);

        return dto;
    }
}
