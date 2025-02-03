package com.sweetme.back.profile.service;

import com.sweetme.back.profile.domain.Profile;
import com.sweetme.back.profile.dto.ProfileDTO;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface ProfileService {

    Profile createProfile(ProfileDTO profileDTO);
}
