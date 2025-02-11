package com.sweetme.back.profile.service;

import com.sweetme.back.auth.domain.User;
import com.sweetme.back.auth.dto.UserDTO;
import com.sweetme.back.profile.domain.Profile;
import com.sweetme.back.profile.dto.ProfileDTO;
import com.sweetme.back.profile.dto.SimpleProfileDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Transactional
public interface ProfileService {

    Profile createEmptyProfile(User user);

    ProfileDTO readProfile(Long profileId);

    ProfileDTO readMyProfile(Long userId);

    void updateMyProfile(ProfileDTO profileDTO, UserDTO userDTO);

    void deleteProfile(Long profileId);

    Map<String, Object> getProfileOptions();

    // 필요한 정보만 포함하는 메서드
    SimpleProfileDTO readSimpleProfile(Long profileId);

    SimpleProfileDTO readMySimpleProfile(Long userId);

    void updateMySimpleProfile(SimpleProfileDTO simpleProfileDTO, UserDTO userDTO);

    Map<String, Object> getSimpleProfileOptions();

    // ProfileDTO.from() 스태틱 메서드가 존재하므로 중복
//    default ProfileDTO entityToDTO(Profile profile) {
//
//        ProfileDTO dto = new ProfileDTO();
//
//        dto.setProfileId(profile.getId());
//        dto.setUserDTO(UserDTO.from(profile.getUser()));
//        dto.setDescription(profile.getDescription());
//        dto.setProfileUrl(profile.getProfileUrl());
//        dto.setImagePath(profile.getImagePath());
//
//        List<StackDTO> stackDTOList = profile.getStacks().stream().map(StackDTO::from).collect(Collectors.toList());
//        List<PositionDTO> positionDTOList = profile.getPositions().stream().map(PositionDTO::from).collect(Collectors.toList());
//
//        dto.setStackDTOS(stackDTOList);
//        dto.setPositionDTOS(positionDTOList);
//
//        return dto;
//    }
}
