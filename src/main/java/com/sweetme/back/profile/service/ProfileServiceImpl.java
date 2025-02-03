package com.sweetme.back.profile.service;

import com.sweetme.back.profile.domain.Position;
import com.sweetme.back.profile.domain.Profile;
import com.sweetme.back.profile.domain.Stack;
import com.sweetme.back.profile.dto.ProfileDTO;
import com.sweetme.back.profile.repository.PositionRepository;
import com.sweetme.back.profile.repository.ProfileRepository;
import com.sweetme.back.profile.repository.StackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;
    private final StackRepository stackRepository;
    private final PositionRepository positionRepository;

    @Override
    public Profile createProfile(ProfileDTO profileDTO) {

        // 선택된 스택 검증
        List<Stack> stacks = stackRepository.findAllById(profileDTO.getStackIds());
        if (stacks.size() != profileDTO.getStackIds().size()) {
            Set<Long> foundIds = stacks.stream()
                    .map(Stack::getId)
                    .collect(Collectors.toSet());

            List<Long> notFoundIds = profileDTO.getStackIds().stream()
                    .filter(id -> !foundIds.contains(id))
                    .collect(Collectors.toList());

            throw new IllegalArgumentException("존재하지 않는 스택이 포함되어 있습니다: " + notFoundIds);
        }

        // 선택된 포지션 검증
        List<Position> positions = positionRepository.findAllById(profileDTO.getPositionIds());
        if (positions.size() != profileDTO.getPositionIds().size()) {
            Set<Long> foundIds = positions.stream()
                    .map(Position::getId)
                    .collect(Collectors.toSet());

            List<Position> notFoundIds = positions.stream()
                    .filter(id -> !foundIds.contains(id))
                    .collect(Collectors.toList());
            throw new IllegalArgumentException("존재하지 않는 포지션이 포함되어 있습니다: " + notFoundIds);
        }


        Profile profile = new Profile();
        profile.setDescription(profileDTO.getDescription());
        profile.setProfileUrl(profileDTO.getProfileUrl());
        profile.setImagePath(profileDTO.getImagePath());
        profile.setStacks(stacks);
        profile.setPositions(positions);

        Profile savedProfile = profileRepository.save(profile);

        // 저장된 profile 을 반환
        return savedProfile;
    }
}
