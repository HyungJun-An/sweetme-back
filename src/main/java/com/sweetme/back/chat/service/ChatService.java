package com.sweetme.back.chat.service;

import com.sweetme.back.auth.domain.User;
import com.sweetme.back.chat.domain.Chat;
import com.sweetme.back.chat.dto.ChatResponseDTO;
import com.sweetme.back.chat.repository.ChatRepository;
import com.sweetme.back.profile.domain.Profile;
import com.sweetme.back.profile.repository.ProfileRepository;
import com.sweetme.back.profile.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatService {

    private final ChatRepository chatRepository;
    private final ProfileService profileService;
    private final ProfileRepository profileRepository;

    // 채팅내역 페이징 조회
    public Page<ChatResponseDTO> getChatsByStudyId(Long studyId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Chat> chats = chatRepository.findByStudyIdOrderByCreatedAtDesc(studyId, pageRequest);
        return chats.map(ChatResponseDTO::new);
    }

    // 채팅방 유저 아이디 조회
    public List<Long> getUserByStudyId(Long studyId) {
        List<User> users = chatRepository.findByStudyId(studyId);
        List<Long> usersId = new ArrayList<>();
        for (User user : users) {
            usersId.add(user.getId());
        }
        return usersId;
    }

    // 채팅방 유저 프로필아이디 조회
    public Long getProfileIdByUserId(Long userId) {
        Optional<Profile> profile  = profileRepository.findWithStacksByUserId(userId);
        return profile.get().getId();
    }
}