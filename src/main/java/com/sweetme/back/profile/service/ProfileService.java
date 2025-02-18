package com.sweetme.back.profile.service;

import com.sweetme.back.auth.domain.User;
import com.sweetme.back.auth.dto.UserDTO;
import com.sweetme.back.profile.domain.Profile;
import com.sweetme.back.profile.dto.ProfileDTO;
import com.sweetme.back.profile.dto.SimpleProfileDTO;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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

    void updateMySimpleProfile(SimpleProfileDTO simpleProfileDTO, MultipartFile profileImage, UserDTO userDTO);

    Map<String, Object> getSimpleProfileOptions();

}
